package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

/**
 * Tactile press-scale modifier — ported from SudokuPro.
 *
 * Animates scale 1 → [pressedScale] on press, springs back on release.
 * Creates a satisfying "push" feel on glass cards and buttons.
 *
 * Animation spec:
 *   Press:   tween(80ms)
 *   Release: spring(dampingRatio 0.55f, StiffnessMediumLow)
 */
fun Modifier.pressScale(
    pressedScale: Float = 0.97f,
    onClick: () -> Unit,
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    scope.launch { scale.animateTo(pressedScale, tween(80)) }
                    val released = tryAwaitRelease()
                    scope.launch {
                        scale.animateTo(
                            1f,
                            spring(
                                dampingRatio = 0.55f,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        )
                    }
                    if (released) onClick()
                },
            )
        }
}
