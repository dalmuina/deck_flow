package com.dalmuina.designsystem.animation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

object DFAnimations {

    val ScaleFadeIn = scaleIn(
        initialScale = 0.6f
    ) + fadeIn()

    val ScaleFadeOut = scaleOut(
        targetScale = 0.6f
    ) + fadeOut()
}