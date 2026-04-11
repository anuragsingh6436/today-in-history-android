package com.bajrangi.todayinhistory.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ═══════════════════════════════════════════════════════════════
// Aurora Color Schemes — extracted from XOMaster & SudokuPro
// ═══════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = IceBlue,
    onPrimary = InkBg,
    primaryContainer = Color(0xFF13344E),
    onPrimaryContainer = Color(0xFFD6EEFB),
    secondary = RoseGold,
    onSecondary = Color(0xFF3A1A0E),
    secondaryContainer = Color(0xFF5A2A18),
    onSecondaryContainer = Color(0xFFF7E3D9),
    tertiary = MintGlow,
    onTertiary = Color(0xFF0A2A1E),
    tertiaryContainer = Color(0xFF1B4636),
    onTertiaryContainer = MintGlowLight,
    background = InkBg,
    onBackground = Paper,
    surface = InkSurface,
    onSurface = Paper,
    surfaceVariant = InkSurfaceHi,
    onSurfaceVariant = PaperMuted,
    outline = InkDivider,
    outlineVariant = Color(0xFF1A2240),
)

private val LightColorScheme = lightColorScheme(
    primary = IceBlueDeep,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6F4FB),
    onPrimaryContainer = IceBlueDarker,
    secondary = RoseGoldDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF7E3D9),
    onSecondaryContainer = Color(0xFF5A2A18),
    tertiary = Color(0xFF3FA889),
    onTertiary = Color.White,
    tertiaryContainer = MintGlowLight,
    onTertiaryContainer = Color(0xFF0F3A2C),
    background = PaperBg,
    onBackground = Ink,
    surface = PaperSurface,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF3EAE0),
    onSurfaceVariant = InkMuted,
    outline = PaperDivider,
    outlineVariant = Color(0xFFEFE3D6),
)

/**
 * Provides glass-surface colors that adapt to the current theme.
 * Consumed by GlassSurface and other premium components.
 */
data class GlassColors(
    val fill: Color,
    val border: Color,
    val inner: Color,
)

val LocalGlassColors = staticCompositionLocalOf {
    GlassColors(GlassFillDark, GlassBorderDark, GlassInnerDark)
}

@Composable
fun TodayInHistoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val glassColors = if (darkTheme) {
        GlassColors(GlassFillDark, GlassBorderDark, GlassInnerDark)
    } else {
        GlassColors(GlassFillLight, GlassBorderLight, GlassInnerLight)
    }

    CompositionLocalProvider(LocalGlassColors provides glassColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
