---
name: android-testing
description: |
  Testing patterns for Android/KMP - ViewModel, UseCase, and Repository unit tests with JUnit4, Turbine, Kotest assertions, MockK, MainDispatcherRule, Robot pattern, Room database tests, and Compose UI tests. Use this skill whenever writing or reviewing tests for ViewModels, use cases, repositories, mappers, DAOs, or Compose screens. Trigger on phrases like "write a test", "unit test the ViewModel", "test a use case", "test a repository", "test a DAO", "MockK", "coEvery", "coVerify", "MainDispatcherRule", "runTest", "ComposeTestRule", "shouldBe", "Robot pattern", "fake data" or "core test".
---

# Android / KMP Testing

## Stack

| Concern | Library |
|---|---|
| Test framework | JUnit4 |
| Assertions | Kotest (`shouldBe`, `shouldBeInstanceOf`) |
| Mocking | MockK (`mockk`, `coEvery`, `coVerify`) |
| Flow / StateFlow testing | Turbine |
| Coroutine testing | `kotlinx-coroutines-test` + `StandardTestDispatcher` |
| UI testing | `ComposeTestRule` |
| DB testing | `Room.inMemoryDatabaseBuilder` |

---

## :core:test Module

Shared test infrastructure lives in `:core:test`. Never duplicate these across feature test modules.

### MainDispatcherRule (`core:test/rules`)

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
```

### Test Data Factories (`core:test/data`)

All parameters have sensible defaults so tests only declare what is relevant:

```kotlin
object NoteTestData {
    fun note(
        id: String = "1",
        title: String = "Meeting notes",
    ) = Note(id = id, title = title)

    fun notes(vararg ids: String) = ids.map { note(id = it) }
}
```

---

## Robot Pattern

Every robot owns the construction and MockK setup for a single class under test. Robots expose mocks as `val` properties so tests configure them with `coEvery`/`coVerify` directly.

---

## ViewModel Tests

```kotlin
class CoinsViewModelRobot {
    val getCoinsUseCase = mockk<GetCoinsUseCase>(relaxed = true)
    fun build() = CoinsViewModel(getCoinsUseCase = getCoinsUseCase)
}

@OptIn(ExperimentalCoroutinesApi::class)
class CoinsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var robot: CoinsViewModelRobot
    private lateinit var viewModel: CoinsViewModel

    @Before fun setup() {
        robot = CoinsViewModelRobot()
        viewModel = robot.build()
    }

    @Test
    fun `when Load and use case returns success then emits loaded state`() = runTest {
        val coins = listOf(coin(id = "bitcoin"), coin(id = "ethereum"))
        coEvery { robot.getCoinsUseCase() } returns Result.Success(coins)

        viewModel.uiState.test {
            awaitItem() shouldBe CoinsUiState(isLoading = true)
            viewModel.process(CoinsIntent.Load)
            advanceUntilIdle()
            val state = awaitItem()
            state.isLoading shouldBe false
            state.coins shouldBe coins.map { it.toUi() }
            coVerify(exactly = 1) { robot.getCoinsUseCase() }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

Call `cancelAndIgnoreRemainingEvents()` at the end of Turbine blocks. Call `advanceUntilIdle()` after dispatching an intent when using `StandardTestDispatcher`.

---

## UseCase Tests

Inject the dispatcher from `MainDispatcherRule` when the use case switches context internally:

```kotlin
class GetCoinsUseCaseRobot {
    val repository = mockk<CoinRepository>()
    fun build(rule: MainDispatcherRule) = GetCoinsUseCase(
        repository = repository,
        dispatcher = rule.testDispatcher,
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class GetCoinsUseCaseTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var robot: GetCoinsUseCaseRobot
    private lateinit var useCase: GetCoinsUseCase

    @Before fun setup() {
        robot = GetCoinsUseCaseRobot()
        useCase = robot.build(mainDispatcherRule)
    }

    @Test
    fun `invoke returns coins when repository returns success`() = runTest {
        val coins = listOf(coin(id = "bitcoin"))
        coEvery { robot.repository.getCoins() } returns Result.Success(coins)

        useCase() shouldBe Result.Success(coins)
        coVerify(exactly = 1) { robot.repository.getCoins() }
    }
}
```

---

## Repository Tests

```kotlin
class OfflineFirstNoteRepositoryRobot {
    val localDataSource = mockk<NoteLocalDataSource>(relaxed = true)
    val remoteDataSource = mockk<NoteRemoteDataSource>(relaxed = true)
    fun build() = OfflineFirstNoteRepository(localDataSource, remoteDataSource)
}

class OfflineFirstNoteRepositoryTest {

    private lateinit var robot: OfflineFirstNoteRepositoryRobot
    private lateinit var repository: OfflineFirstNoteRepository

    @Before fun setup() {
        robot = OfflineFirstNoteRepositoryRobot()
        repository = robot.build()
    }

    @Test
    fun `when remote succeeds then saves locally and returns remote data`() = runTest {
        val notes = listOf(Note(id = "1", title = "Meeting notes"))
        coEvery { robot.remoteDataSource.fetchNotes() } returns Result.Success(notes)
        coEvery { robot.localDataSource.insertAll(notes) } returns Result.Success(Unit)

        repository.getNotes() shouldBe Result.Success(notes)
        coVerify(exactly = 1) { robot.localDataSource.insertAll(notes) }
    }

    @Test
    fun `when remote fails then falls back to local data`() = runTest {
        coEvery { robot.remoteDataSource.fetchNotes() } returns Result.Error(DataError.Network.NoInternet)
        coEvery { robot.localDataSource.getNotes() } returns Result.Success(emptyList())

        repository.getNotes() shouldBe Result.Success(emptyList())
    }
}
```

---

## Mapper Tests

No Robot needed — mappers are pure functions. Test every field:

```kotlin
class AssetDtoMapperTest {

    @Test
    fun `maps AssetDto to Coin correctly`() {
        val dto = AssetDto(id = "bitcoin", name = "Bitcoin", symbol = "BTC",
            priceUsd = "100000", changePercent24Hr = "5.5", rank = "1")
        val result = dto.toCoin(euroRateUsd = 1.25)

        result.id shouldBe "bitcoin"
        result.name shouldBe "Bitcoin"
        result.priceEur shouldBe 80000.0
        result.changePercent24h shouldBe 5.5
    }
}
```

---

## DAO / Database Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class CoinDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: CoinDao

    @Before fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.coinDao()
    }

    @After fun teardown() = database.close()

    @Test
    fun `insertAll and getAll return inserted entities`() = runTest {
        val entities = listOf(CoinEntity(id = "bitcoin", name = "Bitcoin",
            symbol = "BTC", priceEur = 80000.0, changePercent24h = 5.5))
        dao.insertAll(entities)
        dao.getAll() shouldBe entities
    }

    @Test
    fun `inserting duplicate id replaces existing entity`() = runTest {
        val original = CoinEntity(id = "bitcoin", name = "Bitcoin",
            symbol = "BTC", priceEur = 80000.0, changePercent24h = 5.5)
        dao.insertAll(listOf(original))
        dao.insertAll(listOf(original.copy(priceEur = 90000.0)))
        dao.getAll().single().priceEur shouldBe 90000.0
    }
}
```

---

## DataStore DataSource Tests

Prefer the in-memory `PreferenceDataStoreFactory` approach — it tests real read/write behavior. Fall back to MockK when the real DataStore can't be used.

### In-memory (preferred)

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreSelectedDeckDataSourceTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource: DataStoreSelectedDeckDataSource

    @Before fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = TestScope(mainDispatcherRule.testDispatcher + Job()),
            produceFile = { File.createTempFile("test_prefs", ".preferences_pb") }
        )
        dataSource = DataStoreSelectedDeckDataSource(dataStore)
    }

    @Test
    fun `getSelectedDeckId returns null when no value stored`() = runTest {
        dataSource.getSelectedDeckIdFlow().test {
            awaitItem() shouldBe Result.Success(null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveSelectedDeckId persists value and flow emits updated value`() = runTest {
        dataSource.saveSelectedDeckId(42)
        dataSource.getSelectedDeckIdFlow().test {
            awaitItem() shouldBe Result.Success(42)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

## Compose UI Tests

```kotlin
class BPLivePriceCardTest {

    @get:Rule val composeRule = createComposeRule()

    @Test
    fun whenStateIsConnecting_showsConnectingText() {
        composeRule.setContent {
            BPLivePriceCard(state = LivePriceUiState(isConnecting = true, symbol = "BTC/USD"))
        }
        composeRule.onNodeWithText("● LIVE").assertIsDisplayed()
        composeRule.onNodeWithText("Connecting…").assertIsDisplayed()
    }
}
```

Apply the Robot pattern when 3+ test cases share setup sequences.

---

## What to Test

- Unit-test every ViewModel, use case, and any non-trivial domain/data logic.
- Use fakes for repository interfaces injected into ViewModels; use MockK for external collaborators.
- Test mappers as pure functions — verify every field.
- Test DAOs with an in-memory Room database — cover insert, replace, delete, and query.
- Test DataStore datasources with `PreferenceDataStoreFactory` — cover read (default + stored) and write (success + `IOException` → `DataError.Preferences`).
- Write Compose tests for critical user flows; apply the Robot pattern when 3+ tests share setup.
- Always test at least one error path per repository and use case.
