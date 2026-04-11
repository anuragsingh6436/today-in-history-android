package com.bajrangi.todayinhistory.presentation.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// AURORA DESIGN SYSTEM — Shared DNA with XOMaster & SudokuPro
//
// This palette is extracted from 3 production Bajrangi apps to
// ensure visual consistency across the entire app family.
// ═══════════════════════════════════════════════════════════════

// ── Brand Tokens (Primary Palette) ──────────────────────────────
val IceBlue         = Color(0xFF7FD8FF)   // Primary accent (dark mode)
val IceBlueDeep     = Color(0xFF2A8BC4)   // Primary accent (light mode)
val IceBlueDarker   = Color(0xFF14507A)   // Primary container
val RoseGold        = Color(0xFFE8B4A0)   // Secondary accent
val RoseGoldDeep    = Color(0xFFC07A5F)   // Secondary deep
val MintGlow        = Color(0xFF8FE5C9)   // Tertiary accent
val MintGlowLight   = Color(0xFFB6F0DA)   // Tertiary light

// ── Surfaces — Light (Warm Cream) ───────────────────────────────
val PaperBg         = Color(0xFFFFFAF4)   // Background
val PaperSurface    = Color(0xFFFBF6F0)   // Surface
val PaperSurfaceHi  = Color(0xFFFFFFFF)   // Elevated surface
val PaperDivider    = Color(0xFFE6D8CC)   // Divider / outline
val Ink             = Color(0xFF1B1F36)   // Primary text
val InkMuted        = Color(0xFF49454F)   // Secondary text
val InkFaint        = Color(0xFF79747E)   // Tertiary text

// ── Surfaces — Dark (Deep Navy) ─────────────────────────────────
val InkBg           = Color(0xFF070B1C)   // Background
val InkSurface      = Color(0xFF0E1530)   // Surface
val InkSurfaceHi    = Color(0xFF121A36)   // Elevated surface
val InkDivider      = Color(0xFF26305A)   // Divider / outline
val Paper           = Color(0xFFEAF1FB)   // Primary text
val PaperMuted      = Color(0xFFCAC4D0)   // Secondary text
val PaperFaint      = Color(0xFF6A7493)   // Tertiary text

// ── Glass Surface System ────────────────────────────────────────
// Identical to XOMaster & SudokuPro — the signature "Aurora glass" look.
val GlassFillLight   = Color(0xD4FFF6EE)  // 83% alpha warm cream
val GlassFillDark    = Color(0xB3121A36)  // 70% alpha deep navy
val GlassBorderLight = Color(0x38FFFFFF)  // 22% alpha white
val GlassBorderDark  = Color(0x33A8C5FF)  // 20% alpha ice-blue
val GlassInnerLight  = Color(0x0DFFFFFF)  // 5% alpha white
val GlassInnerDark   = Color(0x0AFFFFFF)  // 4% alpha white

// ── Vignette Backdrop ───────────────────────────────────────────
// Radial gradient for the app background — shared across all Bajrangi apps.
val VignetteCoreLight = Color(0xFFFAF8F5)
val VignetteMidLight  = Color(0xFFEEF2F7)
val VignetteEdgeLight = Color(0xFFDDE3EC)
val VignetteCoreDark  = Color(0xFF1A1C4A)
val VignetteMidDark   = Color(0xFF0B1230)
val VignetteEdgeDark  = Color(0xFF03050E)

// ── Aurora Orb ──────────────────────────────────────────────────
val AuroraOrb = IceBlue  // Same in both modes — #7FD8FF
const val OrbAlphaLight = 0.14f
const val OrbAlphaDark  = 0.18f

// ── Content-App Specific Accents ────────────────────────────────
// Unique to Today in History — year badges, era indicators.
val YearAmber       = Color(0xFFFFD54F)   // Year badge highlight
val YearAmberDeep   = Color(0xFFE5A93B)   // Year badge on light
val EraAncient      = Color(0xFF9F6DD6)   // Purple — ancient history
val EraModern       = Color(0xFF31B6E8)   // Blue — modern history
val EraCurrent      = Color(0xFF8FE5C9)   // Mint — recent events

// ── Muted content-grade variants (for text, not UI chrome) ──────
// These are the era colors at reduced saturation — they tint text
// without competing with the content.
val YearAmberMuted  = Color(0xFFD4B96A)   // Warmer, less neon
val EraAncientMuted = Color(0xFF9B88C4)   // Softer purple
val EraModernMuted  = Color(0xFF6FAEC8)   // Calmer blue
val EraCurrentMuted = Color(0xFF8AC4B0)   // Quieter mint

// ── Image overlay scrims (theme-aware) ──────────────────────────
// Use these instead of hardcoded Color(0xFF070B1C) so light mode works.
val ScrimDark  = Color(0xFF070B1C)   // Dark mode image overlay
val ScrimLight = Color(0xFFFFFAF4)   // Light mode image overlay

// ── Confetti Palette ────────────────────────────────────────────
val ConfettiPalette = listOf(
    IceBlue, IceBlueDeep, RoseGold, RoseGoldDeep,
    MintGlow, YearAmber, Color(0xFF7C4DFF), Color(0xFF26C6DA),
)
