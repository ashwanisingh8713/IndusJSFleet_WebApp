package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.components.widgets.*
import com.indusjs.fleet.web.models.User
import com.indusjs.fleet.web.state.AuthState
import com.indusjs.fleet.web.theme.ThemeColors
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page("/settings")
@Composable
fun SettingsPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Profile form state
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var isProfileSaving by remember { mutableStateOf(false) }

    // Password change state
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordSaving by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordSuccess by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        // Load profile
        scope.launch {
            when (val result = AuthService.getProfile()) {
                is ApiResult.Success -> {
                    result.data.data?.let { user ->
                        currentUser = user
                        firstName = user.firstName
                        lastName = user.lastName
                        mobile = user.mobile ?: ""
                    }
                    isLoading = false
                }
                is ApiResult.Error -> {
                    error = result.message
                    isLoading = false
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun saveProfile() {
        if (firstName.isBlank() || lastName.isBlank()) {
            error = "First name and last name are required"
            return
        }

        scope.launch {
            isProfileSaving = true
            error = null
            successMessage = null

            when (val result = AuthService.updateProfile(firstName, lastName, mobile.ifBlank { null })) {
                is ApiResult.Success -> {
                    result.data.data?.let { user ->
                        currentUser = user
                        AuthState.updateUser(user)
                        successMessage = "Profile updated successfully"
                    }
                    isProfileSaving = false
                }
                is ApiResult.Error -> {
                    error = result.message
                    isProfileSaving = false
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun changePassword() {
        passwordError = null
        passwordSuccess = null

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            passwordError = "All password fields are required"
            return
        }

        if (newPassword != confirmPassword) {
            passwordError = "New passwords do not match"
            return
        }

        if (newPassword.length < 8) {
            passwordError = "Password must be at least 8 characters"
            return
        }

        scope.launch {
            isPasswordSaving = true

            when (val result = AuthService.changePassword(currentPassword, newPassword)) {
                is ApiResult.Success -> {
                    passwordSuccess = "Password changed successfully"
                    currentPassword = ""
                    newPassword = ""
                    confirmPassword = ""
                    isPasswordSaving = false
                }
                is ApiResult.Error -> {
                    passwordError = result.message
                    isPasswordSaving = false
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    PageLayout(
        title = "Settings",
        subtitle = "Manage your account settings",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/settings"
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.px), contentAlignment = Alignment.Center) {
                FaSpinner(size = IconSize.X2)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().gap(24.px)) {
                // Profile Section
                SettingsCard(
                    title = "Profile Information",
                    icon = { FaUser(modifier = it) }
                ) {
                    error?.let {
                        Alert(message = it, type = AlertType.ERROR)
                    }
                    successMessage?.let {
                        Alert(message = it, type = AlertType.SUCCESS)
                    }

                    Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormInput(
                                label = "First Name",
                                value = firstName,
                                onValueChange = { firstName = it },
                                placeholder = "Enter first name"
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FormInput(
                                label = "Last Name",
                                value = lastName,
                                onValueChange = { lastName = it },
                                placeholder = "Enter last name"
                            )
                        }
                    }

                    FormInput(
                        label = "Email",
                        value = currentUser?.email ?: "",
                        onValueChange = { },
                        type = InputType.Email,
                        placeholder = "Email address",
                        enabled = false
                    )

                    FormInput(
                        label = "Mobile",
                        value = mobile,
                        onValueChange = { mobile = it },
                        type = InputType.Tel,
                        placeholder = "Enter mobile number"
                    )

                    Row(modifier = Modifier.fillMaxWidth().margin(top = 16.px), horizontalArrangement = Arrangement.End) {
                        PrimaryButton(
                            text = "Save Changes",
                            onClick = { saveProfile() },
                            isLoading = isProfileSaving
                        )
                    }
                }

                // Password Section
                SettingsCard(
                    title = "Change Password",
                    icon = { FaLock(modifier = it) }
                ) {
                    passwordError?.let {
                        Alert(message = it, type = AlertType.ERROR)
                    }
                    passwordSuccess?.let {
                        Alert(message = it, type = AlertType.SUCCESS)
                    }

                    FormInput(
                        label = "Current Password",
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        type = InputType.Password,
                        placeholder = "Enter current password"
                    )

                    FormInput(
                        label = "New Password",
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        type = InputType.Password,
                        placeholder = "Enter new password"
                    )

                    FormInput(
                        label = "Confirm New Password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        type = InputType.Password,
                        placeholder = "Confirm new password"
                    )

                    Row(modifier = Modifier.fillMaxWidth().margin(top = 16.px), horizontalArrangement = Arrangement.End) {
                        PrimaryButton(
                            text = "Change Password",
                            onClick = { changePassword() },
                            isLoading = isPasswordSaving
                        )
                    }
                }


                // About Section
                SettingsCard(
                    title = "About",
                    icon = { FaCircleInfo(modifier = it) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().margin(bottom = 12.px),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Span(attrs = Modifier.color(ThemeColors.textSecondary).toAttrs()) {
                            Text("Version")
                        }
                        Span(attrs = Modifier.color(ThemeColors.textPrimary).fontWeight(FontWeight.Medium).toAttrs()) {
                            Text("1.0.0")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Span(attrs = Modifier.color(ThemeColors.textSecondary).toAttrs()) {
                            Text("Role")
                        }
                        Span(attrs = Modifier.color(ThemeColors.textPrimary).fontWeight(FontWeight.Medium).toAttrs()) {
                            Text(currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "Unknown")
                        }
                    }
                }

                // Danger Zone
                SettingsCard(
                    title = "Danger Zone",
                    icon = { FaTriangleExclamation(modifier = it) },
                    borderColor = ThemeColors.error
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Span(attrs = Modifier.fontSize(16.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).toAttrs()) {
                                Text("Sign Out")
                            }
                            Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()) {
                                Text("Sign out from your account")
                            }
                        }

                        Button(
                            attrs = Modifier
                                .backgroundColor(ThemeColors.error)
                                .color(Color.white)
                                .padding(10.px, 20.px)
                                .borderRadius(8.px)
                                .border(0.px)
                                .cursor(Cursor.Pointer)
                                .onClick {
                                    AuthService.logout()
                                    AuthState.logout()
                                    ctx.router.navigateTo("/auth/login")
                                }
                                .toAttrs()
                        ) {
                            FaRightFromBracket(modifier = Modifier.margin(right = 8.px))
                            Text("Sign Out")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    borderColor: CSSColorValue = ThemeColors.border,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(ThemeColors.surface)
            .borderRadius(12.px)
            .border(1.px, LineStyle.Solid, borderColor)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.px)
                .borderBottom(1.px, LineStyle.Solid, ThemeColors.border),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon(Modifier.color(ThemeColors.primary).margin(right = 12.px))
            Span(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                Text(title)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(20.px).gap(16.px)) {
            content()
        }
    }
}


