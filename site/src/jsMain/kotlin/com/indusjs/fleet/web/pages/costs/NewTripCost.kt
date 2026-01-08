package com.indusjs.fleet.web.pages.costs

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.TripCostService
import com.indusjs.fleet.web.api.services.VehicleService
import com.indusjs.fleet.web.api.services.TripService
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
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page("/costs/trips/new")
@Composable
fun NewTripCostPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var trips by remember { mutableStateOf<List<TripListItem>>(emptyList()) }

    var selectedVehicleId by remember { mutableStateOf<Long?>(null) }
    var selectedTripId by remember { mutableStateOf<Long?>(null) }
    var costType by remember { mutableStateOf("fuel") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Fuel specific
    var fuelType by remember { mutableStateOf("diesel") }
    var fuelQuantity by remember { mutableStateOf("") }
    var fuelRate by remember { mutableStateOf("") }
    var kmPerLiter by remember { mutableStateOf("") }

    // Other type
    var customCostTypeName by remember { mutableStateOf("") }

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

        // Load trips
        scope.launch {
            when (val result = TripService.listTrips(perPage = 100)) {
                is ApiResult.Success -> {
                    trips = result.data.data
                }
                is ApiResult.Error -> {}
                is ApiResult.Loading -> {}
            }
        }
    }

    fun handleSubmit() {
        if (amount.isBlank() || date.isBlank()) {
            error = "Amount and date are required"
            return
        }

        if (costType == "other" && customCostTypeName.isBlank()) {
            error = "Please enter a custom cost type name"
            return
        }

        scope.launch {
            isLoading = true
            error = null

            val request = CreateTripCostRequest(
                tripId = selectedTripId ?: 0,
                vehicleId = selectedVehicleId ?: 0,
                costType = costType,
                amount = amount.toDoubleOrNull() ?: 0.0,
                date = date,
                time = time.ifBlank { null },
                notes = notes.ifBlank { null },
                fuelType = if (costType == "fuel") fuelType else null,
                fuelQuantity = if (costType == "fuel") fuelQuantity.toDoubleOrNull() else null,
                fuelRate = if (costType == "fuel") fuelRate.toDoubleOrNull() else null,
                kmPerLiter = if (costType == "fuel") kmPerLiter.toDoubleOrNull() else null,
                customCostTypeName = if (costType == "other") customCostTypeName else null
            )

            when (val result = TripCostService.createTripCost(request)) {
                is ApiResult.Success -> {
                    ctx.router.navigateTo("/costs/trips")
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
        title = "Add Trip Cost",
        subtitle = "Enter trip expense details",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/costs/trips"
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
                    .onClick { ctx.router.navigateTo("/costs/trips") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                FaArrowLeft(modifier = Modifier.color(ThemeColors.primary).margin(right = 8.px))
                Span(attrs = Modifier.color(ThemeColors.primary).fontSize(14.px).toAttrs()) {
                    Text("Back to Trip Costs")
                }
            }

            error?.let {
                Alert(message = it, type = AlertType.ERROR)
            }

            // Cost Type
            Column(modifier = Modifier.fillMaxWidth().margin(bottom = 16.px)) {
                Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).margin(bottom = 6.px).toAttrs()) {
                    Text("Cost Type *")
                }
                Select({
                    onChange { costType = it.value ?: "fuel" }
                    style {
                        width(100.percent)
                        padding(10.px, 12.px)
                        borderRadius(8.px)
                        border(1.px, LineStyle.Solid, ThemeColors.border)
                        fontSize(14.px)
                        backgroundColor(ThemeColors.background)
                    }
                }) {
                    Option("fuel") { Text("Fuel") }
                    Option("toll") { Text("Toll") }
                    Option("driver_allowance") { Text("Driver Allowance") }
                    Option("parking") { Text("Parking") }
                    Option("loading_charges") { Text("Loading Charges") }
                    Option("unloading_charges") { Text("Unloading Charges") }
                    Option("chalan") { Text("Chalan") }
                    Option("other") { Text("Other") }
                }
            }

            // Custom cost type name for "other"
            if (costType == "other") {
                FormInput(
                    label = "Custom Cost Type Name *",
                    value = customCostTypeName,
                    onValueChange = { customCostTypeName = it },
                    placeholder = "e.g., Driver Meals"
                )
            }

            // Vehicle Selection
            Column(modifier = Modifier.fillMaxWidth().margin(bottom = 16.px)) {
                Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).margin(bottom = 6.px).toAttrs()) {
                    Text("Vehicle")
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
                    Option("") { Text("Select Vehicle (Optional)") }
                    vehicles.forEach { vehicle ->
                        Option(vehicle.id.toString()) { Text(vehicle.registrationNumber) }
                    }
                }
            }

            // Trip Selection
            Column(modifier = Modifier.fillMaxWidth().margin(bottom = 16.px)) {
                Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).margin(bottom = 6.px).toAttrs()) {
                    Text("Trip")
                }
                Select({
                    onChange { selectedTripId = it.value?.toLongOrNull() }
                    style {
                        width(100.percent)
                        padding(10.px, 12.px)
                        borderRadius(8.px)
                        border(1.px, LineStyle.Solid, ThemeColors.border)
                        fontSize(14.px)
                        backgroundColor(ThemeColors.background)
                    }
                }) {
                    Option("") { Text("Select Trip (Optional)") }
                    trips.forEach { trip ->
                        Option(trip.id.toString()) { Text("${trip.startLocation} → ${trip.endLocation}") }
                    }
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

            // Fuel-specific fields
            if (costType == "fuel") {
                Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 16.px).toAttrs()) {}

                Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 12.px).toAttrs()) {
                    Text("Fuel Details")
                }

                Row(modifier = Modifier.fillMaxWidth().gap(16.px).margin(top = 8.px)) {
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
                            }
                        }) {
                            Option("diesel") { Text("Diesel") }
                            Option("petrol") { Text("Petrol") }
                            Option("cng") { Text("CNG") }
                        }
                    }

                    FormInput(
                        label = "Quantity (L)",
                        value = fuelQuantity,
                        onValueChange = { fuelQuantity = it },
                        placeholder = "e.g., 55.5",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                    FormInput(
                        label = "Rate (₹/L)",
                        value = fuelRate,
                        onValueChange = { fuelRate = it },
                        placeholder = "e.g., 90.09",
                        modifier = Modifier.weight(1f)
                    )
                    FormInput(
                        label = "Km per Liter",
                        value = kmPerLiter,
                        onValueChange = { kmPerLiter = it },
                        placeholder = "e.g., 4.5",
                        modifier = Modifier.weight(1f)
                    )
                }
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
                        .onClick { ctx.router.navigateTo("/costs/trips") }
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

