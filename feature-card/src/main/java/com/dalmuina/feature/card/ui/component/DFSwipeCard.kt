package com.dalmuina.feature.card.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DFSwipeCard(
    modifier: Modifier = Modifier,
    onSwiped: () -> Unit,
    content: @Composable () -> Unit
) {

    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val threshold = 300f

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer {
                rotationZ = offsetX.value / 60
            }
            .pointerInput(Unit) {
                detectDragGestures(

                    onDrag = { change, dragAmount ->
                        change.consume()

                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                        }
                    },

                    onDragEnd = {

                        scope.launch {

                            if (offsetX.value > threshold) {

                                offsetX.animateTo(
                                    targetValue = 1000f,
                                    animationSpec = tween(300)
                                )

                                onSwiped()

                                offsetX.snapTo(0f)

                            } else {

                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring()
                                )
                            }
                        }
                    }
                )
            }
    ) {
        content()
    }
}
