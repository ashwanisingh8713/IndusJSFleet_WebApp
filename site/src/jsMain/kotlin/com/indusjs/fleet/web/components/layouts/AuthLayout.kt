package com.indusjs.fleet.web.components.layouts

import androidx.compose.runtime.Composable
import com.indusjs.fleet.web.components.widgets.ThemeToggleCompact
import com.indusjs.fleet.web.theme.ThemeColors
import com.indusjs.fleet.web.theme.ThemeState
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.*

/**
 * Auth layout for login, signup, forgot password pages
 * Now theme-aware!
 */
@Composable
fun AuthLayout(content: @Composable () -> Unit) {
    // Force recomposition when theme changes
    val isDarkMode = ThemeState.currentTheme.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .backgroundColor(ThemeColors.background),
        contentAlignment = Alignment.Center
    ) {
        // Theme toggle in corner
        Box(
            modifier = Modifier
                .position(Position.Absolute)
                .top(20.px)
                .right(20.px)
        ) {
            ThemeToggleCompact()
        }

        Column(
            modifier = Modifier
                .backgroundColor(ThemeColors.surface)
                .borderRadius(16.px)
                .boxShadow(BoxShadow.of(0.px, 10.px, 40.px, color = ThemeColors.cardShadow))
                .padding(40.px)
                .width(100.percent)
                .maxWidth(420.px),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

