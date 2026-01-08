package com.indusjs.fleet.web.pages.costs

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.MaintenanceCostService
import com.indusjs.fleet.web.api.services.VehicleService
import com.indusjs.fleet.web.components.widgets.*
import com.indusjs.fleet.web.models.*
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

@Page("/costs/maintenance/new")
@Composable
fun NewMaintenanceCostPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }

    var selectedVehicleId by remember { mutableStateOf<Long?>(null) }
    var costType by remember { mutableStateOf("tyre") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var vendorName by remember { mutableStateOf("") }
    var invoiceNo by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        // Load vehicles
        scope.launch {
            when (val result = VehicleService.listVehicles(perPage = 100)) {
                is ApiResult.Success -> {
                    vehicles = result.data.data
                }
                is ApiResult.Error -> {}
                is ApiResult.Loading -> {}
            }
        }
    }

    fun handleSubmit() {
        if (selectedVehicleId == null) {
            error = "Please select a vehicle"
            return
        }

        if (amount.isBlank() || date.isBlank()) {
            error = "Amount and date are required"
            return
        }

        scope.launch {
            isLoading = true
            error = null

            val request = CreateMaintenanceCostRequest(
                vehicleId = selectedVehicleId!!,
                costType = costType,
                amount = amount.toDoubleOrNull() ?: 0.0,
                date = date,
                time = time.ifBlank { null },
                description = description.ifBlank { null },
                notes = notes.ifBlank { null },
                vendorName = vendorName.ifBlank { null },
                invoiceNo = invoiceNo.ifBlank { null }
            )

            when (val result = MaintenanceCostService.createMaintenanceCost(request)) {
                is ApiResult.Success -> {
                    ctx.router.navigateTo("/costs/maintenance")
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
        title = "Add Maintenance Cost",
        subtitle = "Enter maintenance expense details",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/costs/maintenance"
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
                    .onClick { ctx.router.navigateTo("/costs/maintenance") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                FaArrowLeft(modifier = Modifier.color(ThemeColors.primary).margin(right = 8.px))
                Span(attrs = Modifier.color(ThemeColors.primary).fontSize(14.px).toAttrs()) {
                    Text("Back to Maintenance Costs")
                }
            }

            error?.let {
                Alert(message = it, type = AlertType.ERROR)
            }

            // Vehicle Selection
            Column(modifier = Modifier.fillMaxWidth().margin(bottom = 16.px)) {
                Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).margin(bottom = 6.px).toAttrs()) {
                    Text("Vehicle *")
                }
                Select({
                    onChange { selectedVehicleId = it.value?.toLongOrNull() }
                    style {
                        width(100.percent)
                        padding(10.px, 12.px)
                        borderRadius(8.px)
                        border(1.px, LineStyle.Solid, ThemeColors.border)
                        fontSize(14.px)
                        backgroundColor(ThemeColors.background)
                    }
                }) {
                    Option("") { Text("Select Vehicle") }
                    vehicles.forEach { vehicle ->
                        Option(vehicle.id.toString()) { Text("${vehicle.registrationNumber} - ${vehicle.make} ${vehicle.model}") }
                    }
                }
            }

            // Cost Type
            Column(modifier = Modifier.fillMaxWidth().margin(bottom = 16.px)) {
                Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).margin(bottom = 6.px).toAttrs()) {
                    Text("Cost Type *")
                }
                Select({
                    onChange { costType = it.value ?: "tyre" }
                    style {
                        width(100.percent)
                        padding(10.px, 12.px)
                        borderRadius(8.px)
                        border(1.px, LineStyle.Solid, ThemeColors.border)
                        fontSize(14.px)
                        backgroundColor(ThemeColors.background)
                    }
                }) {
                    Option("tyre") { Text("Tyre") }
                    Option("battery") { Text("Battery") }
                    Option("oil_change") { Text("Oil Change") }
                    Option("brake_service") { Text("Brake Service") }
                    Option("engine_repair") { Text("Engine Repair") }
                    Option("clutch_repair") { Text("Clutch Repair") }
                    Option("suspension") { Text("Suspension") }
                    Option("electrical") { Text("Electrical") }
                    Option("body_work") { Text("Body Work") }
                    Option("ac_service") { Text("AC Service") }
                    Option("cleaning") { Text("Cleaning") }
                    Option("hydraulic_repair") { Text("Hydraulic Repair") }
                    Option("other") { Text("Other") }
                }
            }

            // Amount and Date
            Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                FormInput(
                    label = "Amount (₹) *",
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = "Enter amount",
                    modifier = Modifier.weight(1f)
                )
                FormInput(
                    label = "Date (DD-MM-YYYY) *",
                    value = date,
                    onValueChange = { date = it },
                    placeholder = "e.g., 08-01-2026",
                    modifier = Modifier.weight(1f)
                )
            }

            // Time
            FormInput(
                label = "Time (HH:MM)",
                value = time,
                onValueChange = { time = it },
                placeholder = "e.g., 14:30"
            )

            // Description
            FormInput(
                label = "Description",
                value = description,
                onValueChange = { description = it },
                placeholder = "e.g., Replaced 2 front tyres"
            )

            Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 16.px).toAttrs()) {}

            Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 12.px).toAttrs()) {
                Text("Vendor Details")
            }

            // Vendor and Invoice
            Row(modifier = Modifier.fillMaxWidth().gap(16.px).margin(top = 8.px)) {
                FormInput(
                    label = "Vendor Name",
                    value = vendorName,
                    onValueChange = { vendorName = it },
                    placeholder = "e.g., Sharma Tyre Works",
                    modifier = Modifier.weight(1f)
                )
                FormInput(
                    label = "Invoice No.",
                    value = invoiceNo,
                    onValueChange = { invoiceNo = it },
                    placeholder = "e.g., INV-2026-001",
                    modifier = Modifier.weight(1f)
                )
            }

            // Notes
            FormInput(
                label = "Notes",
                value = notes,
                onValueChange = { notes = it },
                placeholder = "Any additional notes..."
            )

            // Submit Buttons
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
                        .onClick { ctx.router.navigateTo("/costs/maintenance") }
                        .toAttrs()
                ) {
                    Text("Cancel")
                }

                PrimaryButton(
                    text = "Add Cost",
                    onClick = { handleSubmit() },
                    isLoading = isLoading
                )
            }
        }
    }
}

