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
    glassSurface = Color(0xEBF7FBFF),
    glassBorder = Color(0x99B9D9FF),
    cardElevated = SoftWhite,
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
    successContainer = Color(0xFF0E3A2F),
    warning = Color(0xFFFBBF24),
    warningContainer = Color(0xFF3E2D12),
    danger = Color(0xFFFB7185),
    dangerContainer = Color(0xFF421824),
    glassSurface = Color(0xCC13314D),
    glassBorder = Color(0x6638BDF8),
    cardElevated = DarkCard,
    pregnancySurface = Color(0xFF1F1D3A),
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
    secondaryContainer = Color(0xFFD9F7F4),
    onSecondaryContainer = Color(0xFF084C45),
    tertiary = PremiumPurple,
    onTertiary = SoftWhite,
    tertiaryContainer = PregnancyLavenderSoft,
    onTertiaryContainer = Color(0xFF331B7A),
    background = LightBlueBackground,
    onBackground = DeepNavy,
    surface = SoftWhite,
    onSurface = DeepNavy,
    surfaceVariant = Color(0xFFEAF3FF),
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline,
    outlineVariant = Color(0xFFE1EEFA),
    error = DangerRed,
    onError = SoftWhite,
    errorContainer = DangerRedBg,
    onErrorContainer = Color(0xFF8A1B27)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = DarkBackground,
    primaryContainer = Color(0xFF143D5E),
    onPrimaryContainer = Color(0xFFE0F4FF),
    secondary = DarkAccentCyan,
    onSecondary = DarkBackground,
    secondaryContainer = Color(0xFF0F3B47),
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
    outlineVariant = Color(0xFF1B3A57),
    error = Color(0xFFFB7185),
    onError = DarkBackground,
    errorContainer = Color(0xFF421824),
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
