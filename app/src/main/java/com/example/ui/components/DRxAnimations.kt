package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.PremiumPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 1. Animated DNA Helix & Floating Molecular Particles Canvas
 */
@Composable
fun AnimatedDnaHelixCanvas(
    modifier: Modifier = Modifier,
    primaryColor: Color = ElectricCyan,
    secondaryColor: Color = MedicalBlue
) {
    val infinite = rememberInfiniteTransition(label = "dna_helix")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dna_phase"
    )
    val floatOffset by infinite.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_float"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w * 0.5f
        val amplitude = w * 0.24f
        val steps = 22

        // Subtle molecular background particles
        val particles = listOf(
            Offset(w * 0.14f, h * 0.22f + floatOffset),
            Offset(w * 0.85f, h * 0.18f - floatOffset),
            Offset(w * 0.18f, h * 0.78f - floatOffset * 0.7f),
            Offset(w * 0.82f, h * 0.72f + floatOffset * 0.9f),
            Offset(w * 0.08f, h * 0.50f + floatOffset * 0.5f),
            Offset(w * 0.92f, h * 0.45f - floatOffset * 0.6f)
        )
        particles.forEachIndexed { idx, pos ->
            drawCircle(
                color = if (idx % 2 == 0) primaryColor.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.18f),
                radius = (6f + (idx % 3) * 4f),
                center = pos
            )
            if (idx > 0 && idx % 2 == 1) {
                drawLine(
                    color = primaryColor.copy(alpha = 0.12f),
                    start = particles[idx - 1],
                    end = pos,
                    strokeWidth = 1.5f
                )
            }
        }

        // DNA double helix strands & base-pair rungs
        for (i in 0 until steps) {
            val t = i.toFloat() / (steps - 1)
            val y = h * 0.08f + t * h * 0.84f
            val angle = t * (3.2f * PI).toFloat() + phase
            val x1 = centerX + sin(angle) * amplitude
            val x2 = centerX - sin(angle) * amplitude
            val depth = (cos(angle) + 1f) * 0.5f // 0..1 depth cue

            drawLine(
                color = Color.White.copy(alpha = 0.18f + depth * 0.25f),
                start = Offset(x1, y),
                end = Offset(x2, y),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.35f + depth * 0.55f),
                radius = 4.5f + depth * 4.5f,
                center = Offset(x1, y)
            )
            drawCircle(
                color = secondaryColor.copy(alpha = 0.35f + (1f - depth) * 0.55f),
                radius = 4.5f + (1f - depth) * 4.5f,
                center = Offset(x2, y)
            )
        }
    }
}

/**
 * 2. Animated DRxGuard Glowing Logo Badge
 */
@Composable
fun DRxGuardLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    glowing: Boolean = true
) {
    val infinite = rememberInfiniteTransition(label = "logo_glow")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            if (glowing) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = glowAlpha * 0.45f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = r * 1.25f
                    ),
                    radius = r * 1.25f,
                    center = center
                )
            }
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(MedicalBlue, ElectricCyan)
                ),
                radius = r * 0.86f,
                center = center
            )
            // White medical cross
            val barLong = r * 0.86f
            val barThick = r * 0.30f
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(center.x - barThick / 2f, center.y - barLong / 2f),
                size = Size(barThick, barLong),
                cornerRadius = CornerRadius(barThick / 2f, barThick / 2f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(center.x - barLong / 2f, center.y - barThick / 2f),
                size = Size(barLong, barThick),
                cornerRadius = CornerRadius(barThick / 2f, barThick / 2f)
            )
        }
    }
}

/**
 * 3. Scientific Circular Loading Indicator
 */
@Composable
fun ScientificLoader(
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    color: Color = ElectricCyan
) {
    val infinite = rememberInfiniteTransition(label = "sci_loader")
    val sweep by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loader_rot"
    )

    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = color.copy(alpha = 0.20f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = stroke
        )
        drawArc(
            brush = Brush.sweepGradient(listOf(Color.Transparent, color, Color.White)),
            startAngle = sweep,
            sweepAngle = 220f,
            useCenter = false,
            style = stroke
        )
        drawArc(
            color = MedicalTeal.copy(alpha = 0.7f),
            startAngle = -sweep * 1.3f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
            size = Size(this.size.width - 16.dp.toPx(), this.size.height - 16.dp.toPx()),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * 4. Animated Pill Scanner Reticle & Laser Sweep
 */
@Composable
fun PillScannerOverlayCanvas(
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "scanner_laser")
    val laserY by infinite.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pad = 22.dp.toPx()
        val cornerLen = 30.dp.toPx()
        val strokeW = 4.dp.toPx()
        val frameColor = if (isScanning) ElectricCyan else Color.White.copy(alpha = 0.85f)

        // Four futuristic reticle corners
        // Top-Left
        drawLine(frameColor, Offset(pad, pad), Offset(pad + cornerLen, pad), strokeW, StrokeCap.Round)
        drawLine(frameColor, Offset(pad, pad), Offset(pad, pad + cornerLen), strokeW, StrokeCap.Round)
        // Top-Right
        drawLine(frameColor, Offset(w - pad, pad), Offset(w - pad - cornerLen, pad), strokeW, StrokeCap.Round)
        drawLine(frameColor, Offset(w - pad, pad), Offset(w - pad, pad + cornerLen), strokeW, StrokeCap.Round)
        // Bottom-Left
        drawLine(frameColor, Offset(pad, h - pad), Offset(pad + cornerLen, h - pad), strokeW, StrokeCap.Round)
        drawLine(frameColor, Offset(pad, h - pad), Offset(pad, h - pad - cornerLen), strokeW, StrokeCap.Round)
        // Bottom-Right
        drawLine(frameColor, Offset(w - pad, h - pad), Offset(w - pad - cornerLen, h - pad), strokeW, StrokeCap.Round)
        drawLine(frameColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - cornerLen), strokeW, StrokeCap.Round)

        // Center target circle
        drawCircle(
            color = frameColor.copy(alpha = 0.3f),
            radius = minOf(w, h) * 0.22f,
            center = Offset(w / 2f, h / 2f),
            style = Stroke(width = 1.5.dp.toPx())
        )

        if (isScanning) {
            val y = h * laserY
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, ElectricCyan.copy(alpha = 0.35f), Color.Transparent),
                    startY = y - 28.dp.toPx(),
                    endY = y + 28.dp.toPx()
                ),
                topLeft = Offset(pad, y - 28.dp.toPx()),
                size = Size(w - pad * 2, 56.dp.toPx())
            )
            drawLine(
                color = ElectricCyan,
                start = Offset(pad, y),
                end = Offset(w - pad, y),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * 5. Animated Molecular Receptor Binding Diagram (for Mechanism of Action & Medicine Change Simulator)
 */
@Composable
fun MolecularMechanismCanvas(
    modifier: Modifier = Modifier,
    accentPrimary: Color = MedicalBlue,
    accentSecondary: Color = MedicalTeal
) {
    val infinite = rememberInfiniteTransition(label = "moa_anim")
    val bindingProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "binding_progress"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val membraneY = h * 0.64f

        // Phospholipid bilayer membrane
        val beadCount = 16
        val stepX = w / (beadCount + 1)
        for (i in 1..beadCount) {
            val x = stepX * i
            if (x > w * 0.38f && x < w * 0.62f) continue // Receptor channel gap
            drawCircle(accentPrimary.copy(alpha = 0.32f), 7.dp.toPx(), Offset(x, membraneY - 8.dp.toPx()))
            drawCircle(accentPrimary.copy(alpha = 0.25f), 7.dp.toPx(), Offset(x, membraneY + 14.dp.toPx()))
            drawLine(
                accentPrimary.copy(alpha = 0.18f),
                Offset(x, membraneY - 2.dp.toPx()),
                Offset(x, membraneY + 8.dp.toPx()),
                2.dp.toPx()
            )
        }

        // Transmembrane receptor protein pocket
        val receptorPath = Path().apply {
            moveTo(w * 0.38f, membraneY - 24.dp.toPx())
            lineTo(w * 0.44f, membraneY - 24.dp.toPx())
            lineTo(w * 0.46f, membraneY - 6.dp.toPx())
            lineTo(w * 0.54f, membraneY - 6.dp.toPx())
            lineTo(w * 0.56f, membraneY - 24.dp.toPx())
            lineTo(w * 0.62f, membraneY - 24.dp.toPx())
            lineTo(w * 0.60f, membraneY + 26.dp.toPx())
            lineTo(w * 0.40f, membraneY + 26.dp.toPx())
            close()
        }
        drawPath(
            path = receptorPath,
            brush = Brush.verticalGradient(listOf(accentSecondary, accentPrimary))
        )

        // Drug ligand molecule approaching receptor pocket
        val startY = h * 0.20f
        val dockedY = membraneY - 20.dp.toPx()
        val drugY = startY + (dockedY - startY) * bindingProgress
        val drugCenter = Offset(w * 0.50f, drugY)

        // Molecular glow when docked
        drawCircle(
            brush = Brush.radialGradient(
                listOf(ElectricCyan.copy(alpha = 0.22f + bindingProgress * 0.4f), Color.Transparent),
                center = drugCenter,
                radius = 34.dp.toPx()
            ),
            radius = 34.dp.toPx(),
            center = drugCenter
        )
        // Molecule cluster (3 bonded atoms)
        val atomOffsets = listOf(
            Offset(-14.dp.toPx(), -6.dp.toPx()),
            Offset(14.dp.toPx(), -6.dp.toPx()),
            Offset(0f, 8.dp.toPx())
        )
        atomOffsets.forEach { off ->
            drawLine(Color.White.copy(alpha = 0.8f), drugCenter, drugCenter + off, 2.5.dp.toPx())
            drawCircle(PremiumPurple, 8.dp.toPx(), drugCenter + off)
        }
        drawCircle(ElectricCyan, 10.dp.toPx(), drugCenter)

        // Intracellular downstream signaling waves when bound
        if (bindingProgress > 0.55f) {
            val waveAlpha = ((bindingProgress - 0.55f) / 0.45f).coerceIn(0f, 1f)
            for (ring in 1..3) {
                drawCircle(
                    color = SuccessGreen.copy(alpha = waveAlpha * (0.45f / ring)),
                    radius = (18f + ring * 18f) * waveAlpha,
                    center = Offset(w * 0.50f, membraneY + 34.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

/**
 * 6. Animated BMI Arc Gauge
 */
@Composable
fun BmiGaugeCanvas(
    bmi: Double,
    modifier: Modifier = Modifier
) {
    val clamped = ((bmi - 12.0) / (42.0 - 12.0)).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "bmi_gauge"
    )
    val indicatorColor = when {
        bmi < 18.5 -> ElectricCyan
        bmi < 25.0 -> SuccessGreen
        bmi < 30.0 -> WarningAmber
        else -> DangerRed
    }

    Canvas(modifier = modifier) {
        val stroke = 16.dp.toPx()
        val arcSize = Size(size.width - stroke * 2, (size.width - stroke * 2))
        val topLeft = Offset(stroke, stroke)

        // Multi-zone background arc (180 degrees)
        drawArc(ElectricCyan.copy(alpha = 0.35f), 180f, 39f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        drawArc(SuccessGreen.copy(alpha = 0.35f), 221f, 41f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        drawArc(WarningAmber.copy(alpha = 0.35f), 264f, 32f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        drawArc(DangerRed.copy(alpha = 0.35f), 298f, 62f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))

        // Active sweep arc
        drawArc(
            color = indicatorColor,
            startAngle = 180f,
            sweepAngle = 180f * animatedProgress,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(stroke, cap = StrokeCap.Round)
        )

        // Pointer dot on arc
        val angleRad = Math.toRadians((180.0 + 180.0 * animatedProgress))
        val radius = arcSize.width / 2f
        val cx = size.width / 2f + (radius * cos(angleRad)).toFloat()
        val cy = topLeft.y + radius + (radius * sin(angleRad)).toFloat()
        drawCircle(Color.White, 10.dp.toPx(), Offset(cx, cy))
        drawCircle(indicatorColor, 6.dp.toPx(), Offset(cx, cy))
    }
}

/**
 * 7. Interactive Reference Range Bar (for LabGuard)
 */
@Composable
fun LabReferenceRangeBar(
    value: Double,
    refLow: Double,
    refHigh: Double,
    modifier: Modifier = Modifier
) {
    val span = (refHigh - refLow).coerceAtLeast(1.0)
    val minDisplay = (refLow - span * 0.5).coerceAtLeast(0.0)
    val maxDisplay = refHigh + span * 0.5
    val totalRange = (maxDisplay - minDisplay).coerceAtLeast(1.0)
    val normStart = ((refLow - minDisplay) / totalRange).toFloat().coerceIn(0f, 1f)
    val normEnd = ((refHigh - minDisplay) / totalRange).toFloat().coerceIn(0f, 1f)
    val markerPos = ((value - minDisplay) / totalRange).toFloat().coerceIn(0.02f, 0.98f)
    val markerColor = when {
        value < refLow -> WarningAmber
        value > refHigh -> DangerRed
        else -> SuccessGreen
    }

    Canvas(modifier = modifier.fillMaxWidth().height(26.dp)) {
        val barY = size.height / 2f
        val barH = 8.dp.toPx()
        // Track
        drawRoundRect(
            color = Color.Gray.copy(alpha = 0.2f),
            topLeft = Offset(0f, barY - barH / 2f),
            size = Size(size.width, barH),
            cornerRadius = CornerRadius(barH / 2f, barH / 2f)
        )
        // Normal zone
        drawRoundRect(
            color = SuccessGreen.copy(alpha = 0.45f),
            topLeft = Offset(size.width * normStart, barY - barH / 2f),
            size = Size(size.width * (normEnd - normStart), barH),
            cornerRadius = CornerRadius(barH / 2f, barH / 2f)
        )
        // Patient value marker
        val mx = size.width * markerPos
        drawCircle(Color.White, 9.dp.toPx(), Offset(mx, barY))
        drawCircle(markerColor, 6.dp.toPx(), Offset(mx, barY))
    }
}

/**
 * 8. Interactive Trend Line Chart (for BMI History & LabGuard Trends)
 */
@Composable
fun TrendSparklineChart(
    points: List<Pair<String, Double>>,
    lineColor: Color = MedicalBlue,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return
    val values = points.map { it.second }
    val minV = (values.minOrNull() ?: 0.0) - 1.0
    val maxV = (values.maxOrNull() ?: 1.0) + 1.0
    val range = (maxV - minV).coerceAtLeast(0.5)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
            val w = size.width
            val h = size.height
            val padX = 16.dp.toPx()
            val padY = 14.dp.toPx()
            val usableW = (w - padX * 2).coerceAtLeast(1f)
            val usableH = (h - padY * 2).coerceAtLeast(1f)

            // Horizontal guide lines
            for (g in 0..2) {
                val gy = padY + usableH * (g / 2f)
                drawLine(
                    color = lineColor.copy(alpha = 0.12f),
                    start = Offset(padX, gy),
                    end = Offset(w - padX, gy),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val coords = points.mapIndexed { idx, (_, v) ->
                val x = if (points.size == 1) w / 2f else padX + usableW * (idx.toFloat() / (points.size - 1))
                val y = padY + usableH * (1f - ((v - minV) / range).toFloat())
                Offset(x, y)
            }

            if (coords.size >= 2) {
                val linePath = Path().apply {
                    moveTo(coords.first().x, coords.first().y)
                    for (i in 1 until coords.size) lineTo(coords[i].x, coords[i].y)
                }
                val fillPath = Path().apply {
                    addPath(linePath)
                    lineTo(coords.last().x, h - padY)
                    lineTo(coords.first().x, h - padY)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(listOf(lineColor.copy(alpha = 0.25f), Color.Transparent))
                )
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            coords.forEach { pt ->
                drawCircle(Color.White, 6.dp.toPx(), pt)
                drawCircle(lineColor, 4.dp.toPx(), pt)
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { (label, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f", v),
                        style = MaterialTheme.typography.labelSmall,
                        color = lineColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 9. Glassmorphic / Frosted Medical Card
 */
@Composable
fun GlassMedicalCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = LocalDRxColors.current.glassBorder,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

/**
 * 10. Medical Safety Disclaimer Banner
 */
@Composable
fun MedicalSafetyBanner(
    text: String,
    isUrgent: Boolean = false,
    modifier: Modifier = Modifier
) {
    val ext = LocalDRxColors.current
    val bg = if (isUrgent) ext.dangerContainer else ext.warningContainer
    val fg = if (isUrgent) ext.danger else ext.warning

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isUrgent) Icons.Rounded.WarningAmber else Icons.Rounded.Shield,
                contentDescription = "Medical safety notice",
                tint = fg,
                modifier = Modifier.size(20.dp).padding(top = 2.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 11. Animated Skeleton Shimmer Placeholder
 */
@Composable
fun SkeletonShimmerCard(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "skeleton")
    val alpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmer_alpha"
    )
    Surface(
        modifier = modifier.fillMaxWidth().height(92.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)
    ) {}
}

/**
 * 12. Severity / Status Pill Badge
 */
@Composable
fun StatusPillBadge(
    label: String,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * 13. Success Confirmation Chip
 */
@Composable
fun SuccessConfirmationBanner(message: String, modifier: Modifier = Modifier) {
    val ext = LocalDRxColors.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ext.successContainer,
        border = BorderStroke(1.dp, ext.success.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = "Success", tint = ext.success, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
