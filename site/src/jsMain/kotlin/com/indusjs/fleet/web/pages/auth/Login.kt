package com.indusjs.fleet.web.pages.auth
import com.indusjs.fleet.web.theme.ThemeColors

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.components.layouts.AuthLayout
import com.indusjs.fleet.web.components.widgets.*
import com.indusjs.fleet.web.state.AuthState
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Page("/auth/login")
@Composable
fun LoginPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Check if already logged in
    LaunchedEffect(Unit) {
        if (AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/dashboard")
        }
    }

    fun handleLogin() {
        if (email.isBlank() || password.isBlank()) {
            error = "Please enter email and password"
            return
        }

        scope.launch {
            isLoading = true
            error = null

            try {
                console.log("Attempting login for: $email")

                when (val result = AuthService.login(email, password)) {
                    is ApiResult.Success -> {
                        console.log("Login API success, checking data...")
                        result.data.data?.let { loginData ->
                            console.log("Login successful for user: ${loginData.user.email}")
                            AuthService.handleLoginSuccess(loginData)
                            AuthState.onLoginSuccess(loginData.user)
                            ctx.router.navigateTo("/dashboard")
                        } ?: run {
                            error = result.data.message ?: "Login failed - no data returned"
                            console.log("Login failed: ${result.data.message}")
                        }
                    }
                    is ApiResult.Error -> {
                        error = result.message
                        console.log("Login error: ${result.message}")
                    }
                    is ApiResult.Loading -> {
                        console.log("Login loading...")
                    }
                }
            } catch (e: Exception) {
                error = "Network error: ${e.message}"
                console.log("Login exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    AuthLayout {
        AppLogo()

        Span(
            attrs = Modifier
                .fontSize(24.px)
                .fontWeight(600)
                .color(ThemeColors.textPrimary)
                .margin(bottom = 8.px)
                .toAttrs()
        ) {
            Text("Welcome back")
        }

        Span(
            attrs = Modifier
                .fontSize(14.px)
                .color(ThemeColors.textSecondary)
                .margin(bottom = 32.px)
                .toAttrs()
        ) {
            Text("Sign in to manage your fleet")
        }

        error?.let {
            Alert(message = it, type = AlertType.ERROR)
        }

        FormInput(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            type = InputType.Email,
            placeholder = "Enter your email"
        )

        FormInput(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            type = InputType.Password,
            placeholder = "Enter your password"
        )

        Row(
            modifier = Modifier.fillMaxWidth().margin(bottom = 24.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer()
            LinkButton(
                text = "Forgot password?",
                onClick = { ctx.router.navigateTo("/auth/forgot-password") }
            )
        }

        PrimaryButton(
            text = "Sign In",
            onClick = { handleLogin() },
            isLoading = isLoading
        )

        Row(
            modifier = Modifier.margin(top = 24.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Span(
                attrs = Modifier
                    .fontSize(14.px)
                    .color(ThemeColors.textSecondary)
                    .margin(right = 4.px)
                    .toAttrs()
            ) {
                Text("Don't have an account?")
            }
            LinkButton(
                text = "Sign up",
                onClick = { ctx.router.navigateTo("/auth/signup") }
            )
        }
    }
}

