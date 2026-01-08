package com.indusjs.fleet.web.pages
import com.indusjs.fleet.web.theme.ThemeColors

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.components.widgets.*
import com.indusjs.fleet.web.state.AuthState
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

@Page("/profile")
@Composable
fun ProfilePage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()
    val currentUser = AuthState.currentUser.value

    var firstName by remember { mutableStateOf(currentUser?.firstName ?: "") }
    var lastName by remember { mutableStateOf(currentUser?.lastName ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var mobile by remember { mutableStateOf(currentUser?.mobile ?: "") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<Pair<String, AlertType>?>(null) }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }
        AuthState.initialize()
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            mobile = it.mobile ?: ""
        }
    }

    PageLayout(
        title = "Profile",
        subtitle = "Manage your account information",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/profile"
    ) {
        Row(modifier = Modifier.fillMaxWidth().gap(24.px)) {
            // Profile Info Card
            Column(
                modifier = Modifier
                    .weight(2f)
                    .backgroundColor(ThemeColors.surface)
                    .borderRadius(12.px)
                    .padding(24.px)
                    .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
            ) {
                H3(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 24.px).toAttrs()) {
                    Text("Personal Information")
                }

                message?.let { (msg, type) ->
                    Alert(message = msg, type = type)
                }

                Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormInput(
                            label = "First Name",
                            value = firstName,
                            onValueChange = { firstName = it },
                            placeholder = "John"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FormInput(
                            label = "Last Name",
                            value = lastName,
                            onValueChange = { lastName = it },
                            placeholder = "Doe"
                        )
                    }
                }

                FormInput(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    type = InputType.Email,
                    placeholder = "john@example.com"
                )

                FormInput(
                    label = "Mobile",
                    value = mobile,
                    onValueChange = { mobile = it },
                    type = InputType.Tel,
                    placeholder = "+91 9876543210"
                )

                Row(modifier = Modifier.fillMaxWidth().margin(top = 16.px), horizontalArrangement = Arrangement.End) {
                    PrimaryButton(
                        text = "Save Changes",
                        onClick = {
                            scope.launch {
                                isLoading = true
                                message = null

                                when (val result = AuthService.updateProfile(firstName, lastName, mobile)) {
                                    is ApiResult.Success -> {
                                        message = Pair("Profile updated successfully!", AlertType.SUCCESS)
                                    }
                                    is ApiResult.Error -> {
                                        message = Pair(result.message, AlertType.ERROR)
                                    }
                                    is ApiResult.Loading -> {}
                                }

                                isLoading = false
                            }
                        },
                        isLoading = isLoading,
                        modifier = Modifier.width(200.px)
                    )
                }
            }

            // Profile Summary Card
            Column(
                modifier = Modifier
                    .weight(1f)
                    .backgroundColor(ThemeColors.surface)
                    .borderRadius(12.px)
                    .padding(24.px)
                    .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.px)
                        .backgroundColor(ThemeColors.primaryLight)
                        .borderRadius(40.px)
                        .margin(bottom = 16.px),
                    contentAlignment = Alignment.Center
                ) {
                    Span(attrs = Modifier.color(ThemeColors.primary).fontWeight(FontWeight.Bold).fontSize(32.px).toAttrs()) {
                        Text(currentUser?.firstName?.firstOrNull()?.toString() ?: "U")
                    }
                }

                Span(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 4.px).toAttrs()) {
                    Text(currentUser?.fullName ?: "User")
                }

                Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).margin(bottom = 16.px).toAttrs()) {
                    Text(currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "")
                }

                Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(bottom = 16.px).toAttrs()) {}

                Column(modifier = Modifier.fillMaxWidth().gap(12.px)) {
                    ProfileInfoRow("Email", currentUser?.email ?: "")
                    ProfileInfoRow("Mobile", currentUser?.mobile ?: "")
                }
            }
        }

        // Change Password Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .margin(top = 24.px)
                .backgroundColor(ThemeColors.surface)
                .borderRadius(12.px)
                .padding(24.px)
                .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
        ) {
            H3(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 16.px).toAttrs()) {
                Text("Change Password")
            }

            Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                var currentPassword by remember { mutableStateOf("") }
                var newPassword by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var passwordMessage by remember { mutableStateOf<Pair<String, AlertType>?>(null) }
                var isChangingPassword by remember { mutableStateOf(false) }

                Column(modifier = Modifier.weight(1f)) {
                    FormInput(
                        label = "Current Password",
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        type = InputType.Password,
                        placeholder = "Enter current password"
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FormInput(
                        label = "New Password",
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        type = InputType.Password,
                        placeholder = "Enter new password"
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FormInput(
                        label = "Confirm Password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        type = InputType.Password,
                        placeholder = "Confirm new password"
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().margin(top = 16.px), horizontalArrangement = Arrangement.End) {
                Button(
                    attrs = Modifier
                        .backgroundColor(ThemeColors.surface)
                        .color(ThemeColors.textPrimary)
                        .padding(10.px, 20.px)
                        .borderRadius(8.px)
                        .border(1.px, LineStyle.Solid, ThemeColors.border)
                        .cursor(Cursor.Pointer)
                        .toAttrs()
                ) {
                    Text("Change Password")
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textSecondary).toAttrs()) {
            Text(label)
        }
        Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textPrimary).fontWeight(FontWeight.Medium).toAttrs()) {
            Text(value)
        }
    }
}

