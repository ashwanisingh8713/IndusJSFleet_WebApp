package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.services.AuthService
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun HomePage() {
    val ctx = rememberPageContext()

    // Redirect based on auth status
    LaunchedEffect(Unit) {
        if (AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/dashboard")
        } else {
            ctx.router.navigateTo("/auth/login")
        }
    }

    // Show loading while redirecting
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Loading...")
    }
}
