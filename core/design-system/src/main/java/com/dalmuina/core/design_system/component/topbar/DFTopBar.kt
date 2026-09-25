package com.dalmuina.core.design_system.component.topbar

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DFTopBar(
    modifier: Modifier = Modifier,
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
) {
    TopAppBar(
        modifier = modifier.statusBarsPadding(),
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
    )
}

@DFPreview
@Composable
fun DFTopBarPreview() {
    DeckFlowTheme {
        DFTopBar(
            title = "Deck",
            showBack = false,
        ) { }
    }
}

@DFPreview
@Composable
fun DFTopBarWithArrowPreview() {
    DeckFlowTheme {
        DFTopBar(
            title = "Deck",
            showBack = true,
        ) { }
    }
}
