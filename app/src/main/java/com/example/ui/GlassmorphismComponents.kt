package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BcaCobaltBlue
import com.example.ui.theme.BcaDeepIndigo
import com.example.ui.theme.BcaElectricCyan
import com.example.ui.theme.BcaRoyalBlue
import com.example.ui.theme.BcaSoftSky

/**
 * Background Kanvas Fluid / Liquid Gradient ala myBCA Modern.
 * Menciptakan psikologi visual yang sangat bersih, tenang, segar, dan mewah
 * dengan perpaduan warna Pure Porcelain White, Ice Blue, dan gelombang Liquid Azure.
 */
@Composable
fun MyBcaLiquidGradientBackground(
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_mesh_transition")
    val waveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_shift"
    )
    val orbDrift by infiniteTransition.animateFloat(
        initialValue = -0.06f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_drift"
    )

    val baseBgColors = if (isDarkMode) {
        listOf(
            Color(0xFF041224),
            Color(0xFF081E38),
            Color(0xFF051529)
        )
    } else {
        listOf(
            Color(0xFFEAF4FF),
            Color(0xFFF5F9FF),
            Color(0xFFFFFFFF),
            Color(0xFFF0F7FF)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = baseBgColors))
            .drawBehind {
                val w = size.width
                val h = size.height

                // 1. Top Liquid Wave Canopy (Signature myBCA Fluid Header Aura)
                val headerWavePath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w, h * (0.20f + orbDrift * 0.2f))
                    cubicTo(
                        w * 0.72f, h * (0.26f - orbDrift * 0.3f),
                        w * 0.32f, h * (0.14f + orbDrift * 0.3f),
                        0f, h * (0.22f - orbDrift * 0.15f)
                    )
                    close()
                }
                drawPath(
                    path = headerWavePath,
                    brush = Brush.linearGradient(
                        colors = if (isDarkMode) {
                            listOf(
                                BcaRoyalBlue.copy(alpha = 0.35f),
                                BcaElectricCyan.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        } else {
                            listOf(
                                BcaRoyalBlue.copy(alpha = 0.16f),
                                BcaElectricCyan.copy(alpha = 0.12f),
                                Color(0xFFD8EEFF).copy(alpha = 0.45f)
                            )
                        },
                        start = Offset(0f, 0f),
                        end = Offset(w, h * 0.28f)
                    )
                )

                // 2. Secondary Fluid Ribbon Wave
                val secondWavePath = Path().apply {
                    moveTo(0f, h * 0.06f)
                    cubicTo(
                        w * 0.38f, h * (0.18f + waveShift * 0.04f),
                        w * 0.68f, h * (0.04f - waveShift * 0.03f),
                        w, h * 0.15f
                    )
                    lineTo(w, h * 0.34f)
                    cubicTo(
                        w * 0.65f, h * 0.25f,
                        w * 0.25f, h * 0.36f,
                        0f, h * 0.27f
                    )
                    close()
                }
                drawPath(
                    path = secondWavePath,
                    brush = Brush.horizontalGradient(
                        colors = if (isDarkMode) {
                            listOf(
                                BcaElectricCyan.copy(alpha = 0.10f),
                                BcaRoyalBlue.copy(alpha = 0.16f),
                                BcaSoftSky.copy(alpha = 0.06f)
                            )
                        } else {
                            listOf(
                                BcaElectricCyan.copy(alpha = 0.09f),
                                Color.White.copy(alpha = 0.7f),
                                BcaRoyalBlue.copy(alpha = 0.08f)
                            )
                        }
                    )
                )

                // 3. Soft Ambient Liquid Orbs for Glassmorphism Refraction
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BcaElectricCyan.copy(alpha = if (isDarkMode) 0.16f else 0.13f),
                            Color.Transparent
                        ),
                        center = Offset(w * (0.85f + orbDrift), h * 0.12f),
                        radius = w * 0.65f
                    ),
                    radius = w * 0.65f,
                    center = Offset(w * (0.85f + orbDrift), h * 0.12f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BcaRoyalBlue.copy(alpha = if (isDarkMode) 0.15f else 0.09f),
                            Color.Transparent
                        ),
                        center = Offset(w * (0.12f - orbDrift), h * 0.48f),
                        radius = w * 0.70f
                    ),
                    radius = w * 0.70f,
                    center = Offset(w * (0.12f - orbDrift), h * 0.48f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BcaSoftSky.copy(alpha = if (isDarkMode) 0.12f else 0.11f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.82f, h * (0.78f + orbDrift * 0.5f)),
                        radius = w * 0.60f
                    ),
                    radius = w * 0.60f,
                    center = Offset(w * 0.82f, h * (0.78f + orbDrift * 0.5f))
                )
            },
        content = content
    )
}

/**
 * Modifier khusus untuk memberikan efek Frosted Glassmorphism (Kaca Buram Mengkilap)
 * dengan pantulan cahaya spekular di tepi atas dan bayangan biru lembut.
 */
fun Modifier.glassmorphicCardEffect(
    cornerRadius: Dp = 18.dp,
    accentTint: Color = BcaRoyalBlue,
    isDark: Boolean = false
): Modifier = this
    .shadow(
        elevation = if (isDark) 8.dp else 6.dp,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = accentTint.copy(alpha = if (isDark) 0.35f else 0.18f),
        spotColor = accentTint.copy(alpha = if (isDark) 0.40f else 0.22f)
    )
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        brush = Brush.linearGradient(
            colors = if (isDark) {
                listOf(
                    Color(0xFF102A49).copy(alpha = 0.88f),
                    Color(0xFF0A1D35).copy(alpha = 0.82f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.92f),
                    Color(0xFFF3F9FF).copy(alpha = 0.86f),
                    Color.White.copy(alpha = 0.90f)
                )
            }
        )
    )
    .drawWithContent {
        drawContent()
        // Diagonal subtle liquid glass sheen across top-left corner
        val sheenPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width * 0.65f, 0f)
            lineTo(size.width * 0.25f, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path = sheenPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (isDark) 0.04f else 0.22f),
                    Color.Transparent
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width * 0.5f, size.height * 0.6f)
            )
        )
        // Top specular glass highlight line
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (isDark) 0.35f else 0.95f),
                    accentTint.copy(alpha = 0.30f),
                    Color.White.copy(alpha = if (isDark) 0.25f else 0.85f)
                )
            ),
            start = Offset(size.width * 0.06f, 1.dp.toPx()),
            end = Offset(size.width * 0.94f, 1.dp.toPx()),
            strokeWidth = 1.3.dp.toPx()
        )
    }

/**
 * Border kaca spekular untuk semua kartu fitur Glassmorphism.
 */
fun glassmorphicBorderStroke(
    accentColor: Color = BcaRoyalBlue,
    width: Dp = 1.1.dp,
    isSelected: Boolean = false
): BorderStroke {
    return BorderStroke(
        width = if (isSelected) 1.8.dp else width,
        brush = Brush.linearGradient(
            colors = if (isSelected) {
                listOf(
                    accentColor,
                    BcaElectricCyan,
                    accentColor.copy(alpha = 0.8f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.95f),
                    accentColor.copy(alpha = 0.38f),
                    BcaElectricCyan.copy(alpha = 0.28f),
                    Color.White.copy(alpha = 0.80f)
                )
            }
        )
    )
}

/**
 * Kartu Hero Utama bergaya myBCA Liquid Sapphire Blue dengan gelombang cairan bercahaya (Liquid Mesh).
 * Memberikan kontras mewah untuk menampilkan Total Saldo di atas latar belakang biru-putih.
 */
@Composable
fun MyBcaHeroLiquidCard(
    onClick: () -> Unit,
    accentLineColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.3.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.75f),
                    BcaElectricCyan.copy(alpha = 0.65f),
                    BcaRoyalBlue.copy(alpha = 0.50f)
                )
            )
        ),
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = BcaRoyalBlue.copy(alpha = 0.35f),
                spotColor = BcaCobaltBlue.copy(alpha = 0.40f)
            )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            BcaDeepIndigo,
                            BcaRoyalBlue,
                            BcaCobaltBlue,
                            Color(0xFF0088E8)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 500f)
                    )
                )
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    // Liquid cyan glow orb on top-right of the hero balance card
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                BcaElectricCyan.copy(alpha = 0.42f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.88f, h * 0.18f),
                            radius = w * 0.55f
                        ),
                        radius = w * 0.55f,
                        center = Offset(w * 0.88f, h * 0.18f)
                    )

                    // Fluid S-curve glass wave inside the card
                    val wavePath = Path().apply {
                        moveTo(0f, h * 0.65f)
                        cubicTo(
                            w * 0.35f, h * 0.40f,
                            w * 0.65f, h * 0.90f,
                            w, h * 0.52f
                        )
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(
                        path = wavePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.10f),
                                BcaElectricCyan.copy(alpha = 0.18f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                    )

                    // Subtle second glass wave
                    val topRibbon = Path().apply {
                        moveTo(w * 0.25f, 0f)
                        cubicTo(
                            w * 0.55f, h * 0.32f,
                            w * 0.80f, h * 0.05f,
                            w, h * 0.28f
                        )
                        lineTo(w, 0f)
                        close()
                    }
                    drawPath(
                        path = topRibbon,
                        color = Color.White.copy(alpha = 0.08f)
                    )
                },
            content = content
        )
    }
}
