package com.dalmuina.domain.helpers

import com.dalmuina.core.test.data.CardDomainTestData
import io.kotest.matchers.shouldBe
import org.junit.Test

class CardDomainUtilsTest {
    @Test
    fun `sortedForSession should rotate postponed cards by postponedAt instead of static order`() {
        val postponedLongestAgo =
            CardDomainTestData.card(id = 1, order = 0, postponedAt = 1_000L)
        val postponedMiddle =
            CardDomainTestData.card(id = 2, order = 1, postponedAt = 2_000L)
        val postponedMostRecently =
            CardDomainTestData.card(id = 3, order = 2, postponedAt = 3_000L)

        val result =
            listOf(postponedMostRecently, postponedLongestAgo, postponedMiddle)
                .sortedForSession()

        result shouldBe listOf(postponedLongestAgo, postponedMiddle, postponedMostRecently)
    }

    @Test
    fun `sortedForSession should tie-break pending cards by order when none are postponed`() {
        val first = CardDomainTestData.card(id = 1, order = 0)
        val second = CardDomainTestData.card(id = 2, order = 1)
        val third = CardDomainTestData.card(id = 3, order = 2)

        val result = listOf(third, first, second).sortedForSession()

        result shouldBe listOf(first, second, third)
    }

    @Test
    fun `sortedForSession should order pending before postponed before completed`() {
        val pending = CardDomainTestData.card(id = 1, order = 0)
        val postponedOlder = CardDomainTestData.card(id = 2, order = 1, postponedAt = 1_000L)
        val postponedNewer = CardDomainTestData.card(id = 3, order = 2, postponedAt = 2_000L)
        val completed = CardDomainTestData.card(id = 4, order = 3, completedAt = 5_000L)

        val result =
            listOf(completed, postponedNewer, pending, postponedOlder)
                .sortedForSession()

        result shouldBe listOf(pending, postponedOlder, postponedNewer, completed)
    }
}
