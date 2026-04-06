package com.dalmuina.feature.card.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import com.dalmuina.feature.card.model.SwipeDirection
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeCard(
    modifier: Modifier = Modifier,
    onSwiped: (SwipeDirection) -> Unit,
    onDragProgress: (Float) -> Unit = {},
    content: @Composable () -> Unit,
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
                            val newOffset = offsetX.value + dragAmount.x
                            offsetX.snapTo(newOffset)
                            onDragProgress(newOffset)
                        }
                    },

                    onDragEnd = {

                        scope.launch {

                            if (offsetX.value > threshold) {

                                offsetX.animateTo(
                                    targetValue = 1000f,
                                    animationSpec = tween(300)
                                )

                                onSwiped(SwipeDirection.RIGHT)

                                offsetX.snapTo(0f)
                                onDragProgress(0f)

                            } else if (offsetX.value < -threshold) {

                                offsetX.animateTo(
                                    targetValue = -1000f,
                                    animationSpec = tween(300)
                                )

                                onSwiped(SwipeDirection.LEFT)

                                offsetX.snapTo(0f)
                                onDragProgress(0f)

                            } else {

                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring()
                                )

                                onDragProgress(0f)
                            }
                        }
                    }
                )
            }
    ) {
        content()
    }
}
