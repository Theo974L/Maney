package com.example.theolaforgeeval.core.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private data class ConfettiParticle(
    val startX: Float,
    val velocityX: Float,
    val color: Color,
    val radius: Dp,
    val delayMs: Int
)

private const val DURATION_MS = 1400
private const val FALL_MS = 1200

@Composable
fun ConfettiOverlay(
    modifier: Modifier = Modifier,
    particleCount: Int = 40,
    onFinished: () -> Unit = {}
) {
    val colors = remember {
        listOf(
            Color(0xFFFDE68A),
            Color(0xFF4ADE80),
            Color(0xFF60A5FA),
            Color(0xFFF472B6),
            Color(0xFFF59E0B)
        )
    }

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                startX = Random.nextFloat(),
                velocityX = (Random.nextFloat() - 0.5f) * 0.7f,
                color = colors[Random.nextInt(colors.size)],
                radius = (3 + Random.nextInt(5)).dp,
                delayMs = Random.nextInt(200)
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = DURATION_MS, easing = LinearEasing))
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val elapsedMs = progress.value * DURATION_MS

        particles.forEach { particle ->
            val t = ((elapsedMs - particle.delayMs) / FALL_MS).coerceIn(0f, 1f)

            if (t > 0f) {
                val y = t * size.height
                val x = (particle.startX * size.width) + (particle.velocityX * size.width * t)
                val alpha = 1f - t

                drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.radius.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}
