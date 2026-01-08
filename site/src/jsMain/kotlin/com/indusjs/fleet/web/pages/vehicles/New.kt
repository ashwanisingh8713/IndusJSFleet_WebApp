package com.indusjs.fleet.web.pages.vehicles

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.VehicleService
import com.indusjs.fleet.web.components.widgets.*
import com.indusjs.fleet.web.models.VehicleRequest
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

@Page("/vehicles/new")
@Composable
fun NewVehiclePage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var registrationNumber by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("truck") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("diesel") }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
        }
    }

    fun handleSubmit() {
        if (registrationNumber.isBlank()) {
            error = "Registration number is required"
            return
        }

        scope.launch {
            isLoading = true
            error = null

            val request = VehicleRequest(
                registrationNumber = registrationNumber,
                vehicleType = vehicleType,
                make = make.ifBlank { null },
                model = model.ifBlank { null },
                year = year.toIntOrNull(),
                capacity = capacity.toDoubleOrNull(),
                fuelType = fuelType
            )

            when (val result = VehicleService.createVehicle(request)) {
                is ApiResult.Success -> {
                    ctx.router.navigateTo("/vehicles")
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
        title = "Add New Vehicle",
        subtitle = "Enter vehicle details",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/vehicles"
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
                    .onClick { ctx.router.navigateTo("/vehicles") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                FaArrowLeft(modifier = Modifier.color(ThemeColors.primary).margin(right = 8.px))
                Span(attrs = Modifier.color(ThemeColors.primary).fontSize(14.px).toAttrs()) {
                    Text("Back to Vehicles")
                }
            }

            error?.let {
                Alert(message = it, type = AlertType.ERROR)
            }

            FormInput(
                label = "Registration Number *",
                value = registrationNumber,
                onValueChange = { registrationNumber = it },
                placeholder = "Enter registration number"
            )

            Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                Column(modifier = Modifier.weight(1f)) {
                    Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).margin(bottom = 6.px).toAttrs()) {
                        Text("Vehicle Type")
                    }
                    Select({
                        onChange { vehicleType = it.value ?: "truck" }
                        style {
                            width(100.percent)
                            padding(10.px, 12.px)
                            borderRadius(8.px)
                            border(1.px, LineStyle.Solid, ThemeColors.border)
                            fontSize(14.px)
                            backgroundColor(ThemeColors.background)
                            color(ThemeColors.textPrimary)
                        }
                    }) {
                        Option("truck") { Text("Truck") }
                        Option("trailer") { Text("Trailer") }
                        Option("van") { Text("Van") }
                        Option("pickup") { Text("Pickup") }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).margin(bottom = 6.px).toAttrs()) {
                        Text("Fuel Type")
                    }
                    Select({
                        onChange { fuelType = it.value ?: "diesel" }
                        style {
                            width(100.percent)
                            padding(10.px, 12.px)
                            borderRadius(8.px)
                            border(1.px, LineStyle.Solid, ThemeColors.border)
                            fontSize(14.px)
                            backgroundColor(ThemeColors.background)
                            color(ThemeColors.textPrimary)
                        }
                    }) {
                        Option("diesel") { Text("Diesel") }
                        Option("petrol") { Text("Petrol") }
                        Option("cng") { Text("CNG") }
                        Option("electric") { Text("Electric") }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().gap(16.px).margin(top = 16.px)) {
                FormInput(
                    label = "Make",
                    value = make,
                    onValueChange = { make = it },
                    placeholder = "e.g., Tata, Mahindra",
                    modifier = Modifier.weight(1f)
                )

                FormInput(
                    label = "Model",
                    value = model,
                    onValueChange = { model = it },
                    placeholder = "e.g., Prima, Bolero",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                FormInput(
                    label = "Year",
                    value = year,
                    onValueChange = { year = it },
                    placeholder = "e.g., 2023",
                    modifier = Modifier.weight(1f)
                )

                FormInput(
                    label = "Capacity",
                    value = capacity,
                    onValueChange = { capacity = it },
                    placeholder = "e.g., 10 tons",
                    modifier = Modifier.weight(1f)
                )
            }

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
                        .onClick { ctx.router.navigateTo("/vehicles") }
                        .toAttrs()
                ) {
                    Text("Cancel")
                }

                PrimaryButton(
                    text = "Add Vehicle",
                    onClick = { handleSubmit() },
                    isLoading = isLoading
                )
            }
        }
    }
}

