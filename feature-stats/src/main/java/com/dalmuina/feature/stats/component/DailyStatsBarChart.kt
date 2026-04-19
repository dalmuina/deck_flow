package com.dalmuina.feature.stats.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalmuina.feature.stats.helper.toBarPoints
import com.dalmuina.feature.stats.model.DailyStatsUi
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

@Composable
fun DailyStatsBarChart(
    stats: List<DailyStatsUi>,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val bars = remember(stats) { stats.toBarPoints() }
    val labelListKey = remember { ExtraStore.Key<List<String>>() }

    LaunchedEffect(bars) {
        if (bars.isEmpty()) return@LaunchedEffect
        modelProducer.runTransaction {
            columnSeries { series(bars.map { it.value }) }
            extras { it[labelListKey] = bars.map {startBarPoint-> startBarPoint.label } }
        }
    }

    val columnColor = MaterialTheme.colorScheme.primary
    val labelColor  = MaterialTheme.colorScheme.onSurfaceVariant

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = Fill(columnColor),           // ← Fill con mayúscula, clase
                        thickness = 24.dp,
                        shape = RoundedCornerShape(50),     // ← shape de Compose
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                label = rememberAxisLabelComponent(
                    style = TextStyle(color = labelColor)
                ),
                itemPlacer = VerticalAxis.ItemPlacer.count(count = { 5 }),
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberAxisLabelComponent(
                    style = TextStyle(
                        color = labelColor,
                        fontSize = 11.sp,
                    )
                ),
                guideline = null,
                valueFormatter = CartesianValueFormatter { context, x, _ ->
                    try {
                        context.model.extraStore[labelListKey].getOrNull(x.toInt()).orEmpty()
                    } catch (e: Exception) { e.stackTrace.toString() }
                },
                itemPlacer = HorizontalAxis.ItemPlacer.segmented(),
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
    )
}

