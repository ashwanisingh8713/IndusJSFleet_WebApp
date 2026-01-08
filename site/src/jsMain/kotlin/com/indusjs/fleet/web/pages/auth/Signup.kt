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

@Page("/auth/signup")
@Composable
fun SignupPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Check if already logged in
    LaunchedEffect(Unit) {
        if (AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/dashboard")
        }
    }

    fun handleSignup() {
        // Validation
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
            mobile.isBlank() || password.isBlank()) {
            error = "Please fill all required fields"
            return
        }

        if (password != confirmPassword) {
            error = "Passwords do not match"
            return
        }

        if (password.length < 6) {
            error = "Password must be at least 6 characters"
            return
        }

        scope.launch {
            isLoading = true
            error = null

            val formattedMobile = if (mobile.startsWith("+")) mobile else "+91$mobile"

            when (val result = AuthService.signup(email, formattedMobile, password, firstName, lastName)) {
                is ApiResult.Success -> {
                    result.data.data?.let { loginData ->
                        AuthService.handleLoginSuccess(loginData)
                        AuthState.onLoginSuccess(loginData.user)
                        ctx.router.navigateTo("/dashboard")
                    } ?: run {
                        error = result.data.message ?: "Signup failed"
                    }
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
            Text("Create account")
        }

        Span(
            attrs = Modifier
                .fontSize(14.px)
                .color(ThemeColors.textSecondary)
                .margin(bottom = 32.px)
                .toAttrs()
        ) {
            Text("Start managing your fleet today")
        }

        error?.let {
            Alert(message = it, type = AlertType.ERROR)
        }

        Row(modifier = Modifier.fillMaxWidth().gap(12.px)) {
            FormInput(
                label = "First Name",
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = "Enter first name",
                modifier = Modifier.weight(1f)
            )
            FormInput(
                label = "Last Name",
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = "Enter last name",
                modifier = Modifier.weight(1f)
            )
        }

        FormInput(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            type = InputType.Email,
            placeholder = "Enter email address"
        )

        FormInput(
            label = "Mobile",
            value = mobile,
            onValueChange = { mobile = it },
            type = InputType.Tel,
            placeholder = "Enter mobile number"
        )

        FormInput(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            type = InputType.Password,
            placeholder = "Enter password (min 6 characters)"
        )

        FormInput(
            label = "Confirm Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            type = InputType.Password,
            placeholder = "Re-enter password"
        )

        PrimaryButton(
            text = "Create Account",
            onClick = { handleSignup() },
            isLoading = isLoading,
            modifier = Modifier.margin(top = 8.px)
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
                Text("Already have an account?")
            }
            LinkButton(
                text = "Sign in",
                onClick = { ctx.router.navigateTo("/auth/login") }
            )
        }
    }
}

