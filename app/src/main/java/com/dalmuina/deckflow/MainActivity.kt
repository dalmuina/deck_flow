package com.dalmuina.deckflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dalmuina.deckflow.navigation.NavigationRoot
import com.dalmuina.designsystem.theme.DeckFlowTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeckFlowTheme {
                NavigationRoot()
            }
        }
    }
}
