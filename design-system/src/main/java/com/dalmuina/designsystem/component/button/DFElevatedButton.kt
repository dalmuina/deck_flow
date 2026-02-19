package com.dalmuina.designsystem.component.button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dalmuina.designsystem.tokens.Dimens

@Composable
fun DFElevatedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (Unit) -> Unit
) {

    ElevatedButton(
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = Dimens.defaultElevation
        ),
        onClick = {
            onClick
        },
    ) {
        Text(text = text)
    }
}

