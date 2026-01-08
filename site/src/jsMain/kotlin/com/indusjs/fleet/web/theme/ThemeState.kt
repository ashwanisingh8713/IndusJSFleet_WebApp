package com.indusjs.fleet.web.theme

import androidx.compose.runtime.*
import kotlinx.browser.localStorage

/**
 * Theme mode enum
 */
enum class ThemeMode {
    LIGHT,
    DARK
}

/**
 * Global theme state manager
 */
object ThemeState {
    private const val THEME_STORAGE_KEY = "fleet_theme_mode"

    private val _currentTheme = mutableStateOf(loadSavedTheme())
    val currentTheme: State<ThemeMode> = _currentTheme

    val isDarkMode: Boolean get() = _currentTheme.value == ThemeMode.DARK

    private fun loadSavedTheme(): ThemeMode {
        return try {
            val saved = localStorage.getItem(THEME_STORAGE_KEY)
            when (saved) {
                "dark" -> ThemeMode.DARK
                "light" -> ThemeMode.LIGHT
                else -> ThemeMode.LIGHT // Default to light
            }
        } catch (e: Exception) {
            ThemeMode.LIGHT
        }
    }

    fun toggleTheme() {
        val newTheme = if (_currentTheme.value == ThemeMode.LIGHT) ThemeMode.DARK else ThemeMode.LIGHT
        setTheme(newTheme)
    }

    fun setTheme(mode: ThemeMode) {
        _currentTheme.value = mode
        try {
            localStorage.setItem(THEME_STORAGE_KEY, if (mode == ThemeMode.DARK) "dark" else "light")
        } catch (e: Exception) {
            console.log("Failed to save theme preference")
        }
    }
}

/**
 * Theme-aware color tokens
 */
object ThemeColors {
    // Primary colors (same for both themes)
    val primary get() = org.jetbrains.compose.web.css.Color("#3b82f6")
    val primaryDark get() = org.jetbrains.compose.web.css.Color("#2563eb")
    val primaryLight get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#1e3a5f")
    else
        org.jetbrains.compose.web.css.Color("#eff6ff")

    // Semantic colors
    val success get() = org.jetbrains.compose.web.css.Color("#10b981")
    val warning get() = org.jetbrains.compose.web.css.Color("#f59e0b")
    val error get() = org.jetbrains.compose.web.css.Color("#ef4444")
    val info get() = org.jetbrains.compose.web.css.Color("#3b82f6")

    // Background colors
    val background get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#0f172a")
    else
        org.jetbrains.compose.web.css.Color("#f1f5f9")

    val surface get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#1e293b")
    else
        org.jetbrains.compose.web.css.Color("#ffffff")

    val surfaceElevated get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#334155")
    else
        org.jetbrains.compose.web.css.Color("#ffffff")

    // Text colors
    val textPrimary get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#f1f5f9")
    else
        org.jetbrains.compose.web.css.Color("#1e293b")

    val textSecondary get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#94a3b8")
    else
        org.jetbrains.compose.web.css.Color("#64748b")

    val textMuted get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#64748b")
    else
        org.jetbrains.compose.web.css.Color("#94a3b8")

    val textOnPrimary get() = org.jetbrains.compose.web.css.Color("#ffffff")

    // Border colors
    val border get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#334155")
    else
        org.jetbrains.compose.web.css.Color("#e2e8f0")

    val borderLight get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#475569")
    else
        org.jetbrains.compose.web.css.Color("#f1f5f9")

    // Card shadow
    val cardShadow get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.rgba(0, 0, 0, 0.3f)
    else
        org.jetbrains.compose.web.css.rgba(0, 0, 0, 0.1f)

    // Input colors
    val inputBackground get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#1e293b")
    else
        org.jetbrains.compose.web.css.Color("#ffffff")

    val inputBorder get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#475569")
    else
        org.jetbrains.compose.web.css.Color("#e2e8f0")

    // Sidebar colors
    val sidebarBackground get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#1e293b")
    else
        org.jetbrains.compose.web.css.Color("#ffffff")

    val sidebarHover get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#334155")
    else
        org.jetbrains.compose.web.css.Color("#f1f5f9")

    // Status badge backgrounds
    val successBg get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#064e3b")
    else
        org.jetbrains.compose.web.css.Color("#dcfce7")

    val warningBg get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#78350f")
    else
        org.jetbrains.compose.web.css.Color("#fef3c7")

    val errorBg get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#7f1d1d")
    else
        org.jetbrains.compose.web.css.Color("#fee2e2")

    val infoBg get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#1e3a5f")
    else
        org.jetbrains.compose.web.css.Color("#dbeafe")

    val neutralBg get() = if (ThemeState.isDarkMode)
        org.jetbrains.compose.web.css.Color("#334155")
    else
        org.jetbrains.compose.web.css.Color("#f3f4f6")
}

