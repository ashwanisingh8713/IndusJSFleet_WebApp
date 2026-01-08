package com.indusjs.fleet.web.pages.drivers

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.DriverService
import com.indusjs.fleet.web.components.widgets.*
import com.indusjs.fleet.web.models.DriverRequest
import com.indusjs.fleet.web.pages.PageLayout
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

@Page("/drivers/new")
@Composable
fun NewDriverPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var licenseExpiry by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
        }
    }

    fun handleSubmit() {
        if (firstName.isBlank() || lastName.isBlank() || mobile.isBlank()) {
            error = "First name, last name, and mobile are required"
            return
        }

        scope.launch {
            isLoading = true
            error = null

            val request = DriverRequest(
                firstName = firstName,
                lastName = lastName,
                mobile = if (mobile.startsWith("+")) mobile else "+91$mobile",
                email = email.ifBlank { null },
                licenseNumber = licenseNumber.ifBlank { null },
                licenseExpiry = licenseExpiry.ifBlank { null },
                address = address.ifBlank { null }
            )

            when (val result = DriverService.createDriver(request)) {
                is ApiResult.Success -> {
                    ctx.router.navigateTo("/drivers")
                }
                is ApiResult.Error -> {
                    error = result.message
                }
                is ApiResult.Loading -> {}
            }
            isLoading = false
        }
    }

    PageLayout(
        title = "Add New Driver",
        subtitle = "Enter driver details",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/drivers"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(600.px)
                .backgroundColor(ThemeColors.surface)
                .borderRadius(12.px)
                .padding(24.px)
                .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
        ) {
            // Back Button
            Row(
                modifier = Modifier
                    .margin(bottom = 24.px)
                    .cursor(Cursor.Pointer)
                    .onClick { ctx.router.navigateTo("/drivers") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                FaArrowLeft(modifier = Modifier.color(ThemeColors.primary).margin(right = 8.px))
                Span(attrs = Modifier.color(ThemeColors.primary).fontSize(14.px).toAttrs()) {
                    Text("Back to Drivers")
                }
            }

            error?.let {
                Alert(message = it, type = AlertType.ERROR)
            }

            Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                FormInput(
                    label = "First Name *",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "Enter first name",
                    modifier = Modifier.weight(1f)
                )

                FormInput(
                    label = "Last Name *",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Enter last name",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                FormInput(
                    label = "Mobile Number *",
                    value = mobile,
                    onValueChange = { mobile = it },
                    placeholder = "Enter mobile number",
                    type = InputType.Tel,
                    modifier = Modifier.weight(1f)
                )

                FormInput(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Enter email address",
                    type = InputType.Email,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                FormInput(
                    label = "License Number",
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    placeholder = "Enter license number",
                    modifier = Modifier.weight(1f)
                )

                FormInput(
                    label = "License Expiry",
                    value = licenseExpiry,
                    onValueChange = { licenseExpiry = it },
                    placeholder = "YYYY-MM-DD",
                    modifier = Modifier.weight(1f)
                )
            }

            FormInput(
                label = "Address",
                value = address,
                onValueChange = { address = it },
                placeholder = "Enter address"
            )

            Row(
                modifier = Modifier.fillMaxWidth().margin(top = 24.px),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    attrs = Modifier
                        .backgroundColor(ThemeColors.border)
                        .color(ThemeColors.textPrimary)
                        .padding(10.px, 20.px)
                        .borderRadius(8.px)
                        .border(0.px)
                        .cursor(Cursor.Pointer)
                        .margin(right = 12.px)
                        .onClick { ctx.router.navigateTo("/drivers") }
                        .toAttrs()
                ) {
                    Text("Cancel")
                }

                PrimaryButton(
                    text = "Add Driver",
                    onClick = { handleSubmit() },
                    isLoading = isLoading
                )
            }
        }
    }
}

