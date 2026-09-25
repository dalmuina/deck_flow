package com.dalmuina.feature.stats.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.core.design_system.tokens.Stroke
import com.dalmuina.feature.stats.model.MonthHeatmapDayUi
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth

private val DAY_LABELS = listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do")

@Composable
fun HeatmapCalendar(
    year: Int,
    month: Int,
    days: List<MonthHeatmapDayUi>,
    hasPreviousData: Boolean,
    hasNextMonth: Boolean,
    isLoading: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val monthLabel =
        Month
            .of(month)
            .getDisplayName(java.time.format.TextStyle.FULL, LocalLocale.current.platformLocale)
            .replaceFirstChar { it.uppercase() }

    val firstDayOffset =
        YearMonth.of(year, month).atDay(1).dayOfWeek.let {
            (it.value - DayOfWeek.MONDAY.value + 7) % 7
        }

    var selectedDay by remember(year, month) { mutableStateOf<MonthHeatmapDayUi?>(null) }

    Column(modifier = modifier.fillMaxWidth()) {
        MonthNavigatorRow(
            label = "$monthLabel $year",
            hasPreviousData = hasPreviousData,
            hasNextMonth = hasNextMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
        )

        WeekDayHeaderRow()

        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else {
            CalendarGrid(
                days = days,
                firstDayOffset = firstDayOffset,
                selectedDay = selectedDay,
                onDaySelected = { day ->
                    selectedDay = if (selectedDay?.dayOfMonth == day.dayOfMonth) null else day
                },
            )
        }

        Spacer(Modifier.height(Spacing.s))

        BottomRow(selectedDay = selectedDay)
    }
}

@Composable
private fun MonthNavigatorRow(
    label: String,
    hasPreviousData: Boolean,
    hasNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPreviousMonth, enabled = hasPreviousData) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Mes anterior",
                tint =
                    if (hasPreviousData) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        IconButton(onClick = onNextMonth, enabled = hasNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Mes siguiente",
                tint =
                    if (hasNextMonth) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
            )
        }
    }
}

@Composable
private fun WeekDayHeaderRow() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        DAY_LABELS.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    days: List<MonthHeatmapDayUi>,
    firstDayOffset: Int,
    selectedDay: MonthHeatmapDayUi?,
    onDaySelected: (MonthHeatmapDayUi) -> Unit,
) {
    val totalCells = firstDayOffset + days.size
    val rows = (totalCells + 6) / 7

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        repeat(rows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val dayIndex = cellIndex - firstDayOffset
                    val day = days.getOrNull(dayIndex)

                    if (day != null) {
                        DayCell(
                            day = day,
                            isSelected = selectedDay?.dayOfMonth == day.dayOfMonth,
                            onClick = { onDaySelected(day) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: MonthHeatmapDayUi,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val empty = MaterialTheme.colorScheme.surfaceVariant
    val backgroundColor = day.level.toHeatmapColor(primary = primary, empty = empty)

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(Corner.xs))
                .background(backgroundColor)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = Stroke.m,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(Corner.xs),
                        )
                    } else {
                        Modifier
                    },
                ).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            fontSize = 10.sp,
            color =
                if (day.level >= 3) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
    }
}

@Composable
private fun BottomRow(selectedDay: MonthHeatmapDayUi?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (selectedDay != null) {
            Text(
                text =
                    if (selectedDay.totalSpentMillis > 0) {
                        "Día ${selectedDay.dayOfMonth} · ${selectedDay.totalSpentMillis.toReadableTime()}"
                    } else {
                        "Día ${selectedDay.dayOfMonth} · Sin actividad"
                    },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
        } else {
            Spacer(Modifier.weight(1f))
        }

        ColorLegend()
    }
}

@Composable
private fun ColorLegend() {
    val primary = MaterialTheme.colorScheme.primary
    val empty = MaterialTheme.colorScheme.surfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = "Mín",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        (0..4).forEach { level ->
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(level.toHeatmapColor(primary = primary, empty = empty)),
            )
        }
        Text(
            text = "Máx",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun Int.toHeatmapColor(
    primary: Color,
    empty: Color,
): Color =
    when (this) {
        0 -> empty
        1 -> primary.copy(alpha = 0.25f)
        2 -> primary.copy(alpha = 0.50f)
        3 -> primary.copy(alpha = 0.75f)
        else -> primary
    }

private fun Long.toReadableTime(): String {
    val totalMinutes = this / 60_000L
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}min"
        hours > 0 -> "${hours}h"
        else -> "${minutes}min"
    }
}

@DFPreview
@Composable
private fun HeatmapCalendarPreview() {
    val previewDays =
        (1..30).map { day ->
            MonthHeatmapDayUi(
                dayOfMonth = day,
                dayStart = 0L,
                totalSpentMillis =
                    when {
                        day % 7 == 0 -> 0L
                        day % 3 == 0 -> 3_600_000L
                        day % 2 == 0 -> 1_800_000L
                        else -> 900_000L
                    },
                completedCount = if (day % 7 == 0) 0 else 1,
                level =
                    when {
                        day % 7 == 0 -> 0
                        day % 3 == 0 -> 4
                        day % 2 == 0 -> 2
                        else -> 1
                    },
            )
        }
    DeckFlowTheme(darkTheme = false) {
        HeatmapCalendar(
            year = 2025,
            month = 4,
            days = previewDays,
            hasPreviousData = true,
            hasNextMonth = false,
            isLoading = false,
            onPreviousMonth = {},
            onNextMonth = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
