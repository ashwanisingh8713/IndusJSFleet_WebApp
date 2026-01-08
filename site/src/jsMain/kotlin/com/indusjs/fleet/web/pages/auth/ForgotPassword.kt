package com.indusjs.fleet.web.pages.auth
import com.indusjs.fleet.web.theme.ThemeColors

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.components.layouts.AuthLayout
import com.indusjs.fleet.web.components.widgets.*
import com.varabyte.kobweb.compose.foundation.layout.Row
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

@Page("/auth/forgot-password")
@Composable
fun ForgotPasswordPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    fun handleSubmit() {
        if (email.isBlank()) {
            error = "Please enter your email"
            return
        }

        scope.launch {
            isLoading = true
            error = null

            when (val result = AuthService.forgotPassword(email)) {
                is ApiResult.Success -> {
                    success = true
                }
                is ApiResult.Error -> {
                    error = result.message
                }
                is ApiResult.Loading -> {}
            }

            isLoading = false
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
            Text("Forgot password?")
        }

        Span(
            attrs = Modifier
                .fontSize(14.px)
                .color(ThemeColors.textSecondary)
                .margin(bottom = 32.px)
                .toAttrs()
        ) {
            Text("Enter your email and we'll send you a reset link")
        }

        if (success) {
            Alert(
                message = "Password reset link sent! Check your email.",
                type = AlertType.SUCCESS
            )

            PrimaryButton(
                text = "Back to Login",
                onClick = { ctx.router.navigateTo("/auth/login") }
            )
        } else {
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

            PrimaryButton(
                text = "Send Reset Link",
                onClick = { handleSubmit() },
                isLoading = isLoading
            )
        }

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
                Text("Remember your password?")
            }
            LinkButton(
                text = "Sign in",
                onClick = { ctx.router.navigateTo("/auth/login") }
            )
        }
    }
}

