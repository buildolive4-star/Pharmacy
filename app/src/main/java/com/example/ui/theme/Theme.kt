package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class DRxExtendedColors(
    val cyanAccent: Color,
    val tealAccent: Color,
    val purpleAccent: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val danger: Color,
    val dangerContainer: Color,
    val glassSurface: Color,
    val glassBorder: Color,
    val cardElevated: Color,
    val subtleSurface: Color,
    val heroStart: Color,
    val heroMid: Color,
    val heroEnd: Color,
    val pregnancySurface: Color,
    val pregnancyAccent: Color,
    val pregnancyPink: Color,
    val isDark: Boolean
)

val LightExtendedColors = DRxExtendedColors(
    cyanAccent = ElectricCyan,
    tealAccent = MedicalTeal,
    purpleAccent = PremiumPurple,
    success = SuccessGreen,
    successContainer = SuccessGreenBg,
    warning = WarningAmber,
    warningContainer = WarningAmberBg,
    danger = DangerRed,
    dangerContainer = DangerRedBg,
    glassSurface = Color(0xF5FFFFFF),
    glassBorder = Color(0xFFD5E6FA),
    cardElevated = SoftWhite,
    subtleSurface = SubtleIceCard,
    heroStart = DeepNavy,
    heroMid = Color(0xFF0C489E),
    heroEnd = Color(0xFF0E8AB8),
    pregnancySurface = PregnancyLavenderSoft,
    pregnancyAccent = PregnancyLavender,
    pregnancyPink = PregnancyPink,
    isDark = false
)

val DarkExtendedColors = DRxExtendedColors(
    cyanAccent = DarkAccentCyan,
    tealAccent = MedicalTeal,
    purpleAccent = Color(0xFF9F84FA),
    success = Color(0xFF34D399),
    successContainer = Color(0xFF0C382D),
    warning = Color(0xFFFBBF24),
    warningContainer = Color(0xFF3C2A10),
    danger = Color(0xFFFB7185),
    dangerContainer = Color(0xFF421623),
    glassSurface = Color(0xE60F243C),
    glassBorder = Color(0x5538BDF8),
    cardElevated = DarkCard,
    subtleSurface = Color(0xFF0A1B2E),
    heroStart = Color(0xFF061324),
    heroMid = Color(0xFF0D3566),
    heroEnd = Color(0xFF0B6E8C),
    pregnancySurface = Color(0xFF1D1A38),
    pregnancyAccent = Color(0xFFB79CFF),
    pregnancyPink = Color(0xFFF48FB1),
    isDark = true
)

val LocalDRxColors = staticCompositionLocalOf { LightExtendedColors }

private val LightColorScheme = lightColorScheme(
    primary = MedicalBlue,
    onPrimary = SoftWhite,
    primaryContainer = IceSurface,
    onPrimaryContainer = DeepNavy,
    secondary = MedicalTeal,
    onSecondary = SoftWhite,
    secondaryContainer = Color(0xFFD6F7F2),
    onSecondaryContainer = Color(0xFF064A42),
    tertiary = PremiumPurple,
    onTertiary = SoftWhite,
    tertiaryContainer = PregnancyLavenderSoft,
    onTertiaryContainer = Color(0xFF331B7A),
    background = LightBlueBackground,
    onBackground = DeepNavy,
    surface = SoftWhite,
    onSurface = DeepNavy,
    surfaceVariant = Color(0xFFEAF2FE),
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline,
    outlineVariant = Color(0xFFE0ECFA),
    error = DangerRed,
    onError = SoftWhite,
    errorContainer = DangerRedBg,
    onErrorContainer = Color(0xFF8A1B27)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = DarkBackground,
    primaryContainer = Color(0xFF123B5E),
    onPrimaryContainer = Color(0xFFE0F4FF),
    secondary = DarkAccentCyan,
    onSecondary = DarkBackground,
    secondaryContainer = Color(0xFF0D3947),
    onSecondaryContainer = Color(0xFFCFF8FC),
    tertiary = Color(0xFFA78BFA),
    onTertiary = DarkBackground,
    tertiaryContainer = Color(0xFF2D2159),
    onTertiaryContainer = Color(0xFFEDE9FE),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    outlineVariant = Color(0xFF1A3654),
    error = Color(0xFFFB7185),
    onError = DarkBackground,
    errorContainer = Color(0xFF421623),
    onErrorContainer = Color(0xFFFFD6DC)
)

val DRxShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun DRxGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalDRxColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = DRxShapes,
            content = content
        )
    }
}
