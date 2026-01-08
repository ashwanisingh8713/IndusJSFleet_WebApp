package com.indusjs.fleet.web.components.widgets

import androidx.compose.runtime.Composable
import com.indusjs.fleet.web.theme.ThemeColors
import com.indusjs.fleet.web.theme.ThemeState
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import org.jetbrains.compose.web.css.*

/**
 * Theme toggle switch component
 */
@Composable
fun ThemeToggle(
    modifier: Modifier = Modifier
) {
    val isDark = ThemeState.isDarkMode

    Row(
        modifier = modifier
            .padding(8.px)
            .borderRadius(20.px)
            .backgroundColor(ThemeColors.surface)
            .border(1.px, LineStyle.Solid, ThemeColors.border)
            .cursor(Cursor.Pointer)
            .onClick { ThemeState.toggleTheme() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sun icon (light mode)
        Box(
            modifier = Modifier
                .size(32.px)
                .borderRadius(16.px)
                .backgroundColor(if (!isDark) ThemeColors.primary else Color.transparent),
            contentAlignment = Alignment.Center
        ) {
            FaSun(
                modifier = Modifier.color(
                    if (!isDark) Color.white else ThemeColors.textMuted
                )
            )
        }

        // Moon icon (dark mode)
        Box(
            modifier = Modifier
                .size(32.px)
                .borderRadius(16.px)
                .backgroundColor(if (isDark) ThemeColors.primary else Color.transparent),
            contentAlignment = Alignment.Center
        ) {
            FaMoon(
                modifier = Modifier.color(
                    if (isDark) Color.white else ThemeColors.textMuted
                )
            )
        }
    }
}

/**
 * Compact theme toggle for sidebar
 */
@Composable
fun ThemeToggleCompact(
    modifier: Modifier = Modifier
) {
    val isDark = ThemeState.isDarkMode

    Box(
        modifier = modifier
            .size(36.px)
            .borderRadius(18.px)
            .backgroundColor(ThemeColors.primaryLight)
            .cursor(Cursor.Pointer)
            .onClick { ThemeState.toggleTheme() },
        contentAlignment = Alignment.Center
    ) {
        if (isDark) {
            FaSun(modifier = Modifier.color(ThemeColors.primary))
        } else {
            FaMoon(modifier = Modifier.color(ThemeColors.primary))
        }
    }
}

