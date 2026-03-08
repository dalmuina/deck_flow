package com.dalmuina.designsystem.component.topbar

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.AppTypography
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DFTopBar(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@DFPreview
@Composable
fun DFTopBarPreview() {
    DeckFlowTheme {
        DFTopBar(
            title = "Deck",
            showBack = false
        ) { }
    }
}

@DFPreview
@Composable
fun DFTopBarWithArrowPreview() {
    DeckFlowTheme {
        DFTopBar(
            title = "Deck",
            showBack = true
        ) { }
    }
}