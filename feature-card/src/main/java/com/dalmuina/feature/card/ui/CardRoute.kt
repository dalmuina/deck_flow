package com.dalmuina.feature.card.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

@Composable
fun CardRoute(
    viewModel: CardViewModel = koinViewModel(),
) {
    CardScreen(

    )
}

@Composable
fun CardScreen(

) {
    Box(modifier = Modifier) {
        Text(text = "Card")
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = Devices.PIXEL_7
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.PIXEL_7
)
@Composable
fun CardScreenPreview() {
    CardScreen()
}