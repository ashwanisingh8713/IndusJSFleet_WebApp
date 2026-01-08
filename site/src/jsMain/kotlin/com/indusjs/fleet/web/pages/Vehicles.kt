package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.VehicleService
import com.indusjs.fleet.web.theme.ThemeColors
import com.indusjs.fleet.web.models.*
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
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page("/vehicles")
@Composable
fun VehiclesPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Filtered vehicles based on search
    val filteredVehicles = remember(vehicles, searchQuery) {
        if (searchQuery.isBlank()) {
            vehicles
        } else {
            vehicles.filter { vehicle ->
                vehicle.registrationNumber.contains(searchQuery, ignoreCase = true) ||
                vehicle.vehicleType.contains(searchQuery, ignoreCase = true) ||
                (vehicle.make?.contains(searchQuery, ignoreCase = true) == true) ||
                (vehicle.model?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    // Check auth
    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        scope.launch {
            when (val result = VehicleService.listVehicles()) {
                is ApiResult.Success -> {
                    vehicles = result.data.data
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

    PageLayout(
        title = "Vehicles",
        subtitle = "Manage your fleet vehicles",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/vehicles"
    ) {
        // Header with Search and Add button
        Row(
            modifier = Modifier.fillMaxWidth().margin(bottom = 24.px),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Span(attrs = Modifier.fontSize(20.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                Text("All Vehicles (${filteredVehicles.size})")
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.gap(12.px)) {
                // Search Box
                Row(
                    modifier = Modifier
                        .backgroundColor(ThemeColors.surface)
                        .border(1.px, LineStyle.Solid, ThemeColors.border)
                        .borderRadius(8.px)
                        .padding(8.px, 12.px),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FaMagnifyingGlass(modifier = Modifier.color(ThemeColors.textMuted).margin(right = 8.px), size = IconSize.SM)
                    Input(InputType.Text) {
                        value(searchQuery)
                        onInput { searchQuery = it.value }
                        style {
                            property("border", "none")
                            property("outline", "none")
                            property("background", "transparent")
                            width(200.px)
                            fontSize(14.px)
                            color(ThemeColors.textPrimary.toString())
                        }
                        attr("placeholder", "Search vehicles...")
                    }
                    if (searchQuery.isNotBlank()) {
                        FaXmark(
                            modifier = Modifier
                                .color(ThemeColors.textMuted)
                                .cursor(Cursor.Pointer)
                                .onClick { searchQuery = "" },
                            size = IconSize.SM
                        )
                    }
                }

                Button(
                    attrs = Modifier
                        .backgroundColor(ThemeColors.primary)
                        .color(Color.white)
                        .padding(10.px, 20.px)
                        .borderRadius(8.px)
                        .border(0.px)
                        .cursor(Cursor.Pointer)
                        .onClick { ctx.router.navigateTo("/vehicles/new") }
                        .toAttrs()
                ) {
                    FaPlus(modifier = Modifier.margin(right = 8.px))
                    Text("Add Vehicle")
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.px), contentAlignment = Alignment.Center) {
                FaSpinner(size = IconSize.X2)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxWidth().backgroundColor(Color("#fee2e2")).padding(16.px).borderRadius(8.px)
            ) {
                Text("Error: $error")
            }
        } else if (filteredVehicles.isEmpty()) {
            EmptyState(
                icon = { FaTruck(modifier = it) },
                title = if (searchQuery.isBlank()) "No Vehicles Yet" else "No Results Found",
                message = if (searchQuery.isBlank()) "Add your first vehicle to start managing your fleet" else "Try adjusting your search"
            )
        } else {
            // Vehicle Grid
            Div(
                attrs = Modifier
                    .display(DisplayStyle.Grid)
                    .gap(20.px)
                    .fillMaxWidth()
                    .toAttrs {
                        style {
                            property("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
                        }
                    }
            ) {
                filteredVehicles.forEach { vehicle ->
                    VehicleCard(vehicle) { ctx.router.navigateTo("/vehicles/${vehicle.id}") }
                }
            }
        }
    }
}

@Composable
private fun VehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .backgroundColor(ThemeColors.surface)
            .borderRadius(12.px)
            .padding(20.px)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
            .cursor(Cursor.Pointer)
            .onClick { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().margin(bottom = 12.px),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Span(attrs = Modifier.fontSize(16.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                    Text(vehicle.registrationNumber)
                }
                StatusBadge(vehicle.state)
            }

            Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).margin(bottom = 8.px).toAttrs()) {
                Text("${vehicle.make} ${vehicle.model}")
            }

            if (vehicle.vehicleType != null) {
                Span(attrs = Modifier.fontSize(12.px).color(ThemeColors.textMuted).toAttrs()) {
                    Text(vehicle.vehicleType)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "active", "available" -> Pair(ThemeColors.successBg, ThemeColors.success)
        "on_trip", "in_transit" -> Pair(ThemeColors.infoBg, ThemeColors.info)
        "maintenance" -> Pair(ThemeColors.warningBg, ThemeColors.warning)
        "inactive" -> Pair(ThemeColors.neutralBg, ThemeColors.textMuted)
        "planned" -> Pair(ThemeColors.infoBg, ThemeColors.info)
        "in_progress" -> Pair(ThemeColors.warningBg, ThemeColors.warning)
        "completed" -> Pair(ThemeColors.successBg, ThemeColors.success)
        "cancelled" -> Pair(ThemeColors.errorBg, ThemeColors.error)
        else -> Pair(ThemeColors.neutralBg, ThemeColors.textMuted)
    }

    Span(
        attrs = Modifier
            .backgroundColor(bgColor)
            .color(textColor)
            .padding(4.px, 8.px)
            .borderRadius(4.px)
            .fontSize(12.px)
            .fontWeight(FontWeight.Medium)
            .toAttrs()
    ) {
        Text(status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
    }
}

@Composable
fun EmptyState(
    icon: @Composable (Modifier) -> Unit,
    title: String,
    message: String
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(60.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.px)
                .backgroundColor(ThemeColors.primaryLight)
                .borderRadius(40.px)
                .margin(bottom = 20.px),
            contentAlignment = Alignment.Center
        ) {
            icon(Modifier.color(ThemeColors.primary).fontSize(32.px))
        }
        Span(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 8.px).toAttrs()) {
            Text(title)
        }
        Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()) {
            Text(message)
        }
    }
}

