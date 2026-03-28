package com.dalmuina.feature.stats.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dalmuina.feature.stats.model.DFDailyStatsUi
import com.dalmuina.feature.stats.utils.toBarPoints
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyStatsBarChart(
    stats: List<DFDailyStatsUi>,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val bars = remember(stats) { stats.toBarPoints() }

    val labelListKey = remember { ExtraStore.Key<List<String>>() }

    LaunchedEffect(bars) {
        modelProducer.runTransaction {
            columnSeries {
                series(bars.map { it.value })
            }
            extras { store ->
                store[labelListKey] = bars.map { it.label }
            }
        }
    }

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = CartesianValueFormatter { context, x, _ ->
                        context.model.extraStore[labelListKey]
                            .getOrNull(x.toInt())
                            .orEmpty()
                    }
                ),
            ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}