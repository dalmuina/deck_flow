package com.dalmuina.designsystem.component.button

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dalmuina.designsystem.tokens.Dimens

@Composable
fun DFButton(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    isEnable: Boolean = true,
    onClick: () -> Unit
) {

    Button(
        enabled = isEnable,
        onClick = onClick,
    ) {
       text()
    }
}

