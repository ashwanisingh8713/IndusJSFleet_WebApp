package com.indusjs.fleet.web.pages.vehicles

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.VehicleService
import com.indusjs.fleet.web.models.Vehicle
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

@Page("/vehicles/{id}")
@Composable
fun VehicleDetailPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()
    val vehicleId = ctx.route.params["id"] ?: ""

    var vehicle by remember { mutableStateOf<Vehicle?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(vehicleId) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        if (vehicleId.isBlank()) {
            error = "Invalid vehicle ID"
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            when (val result = VehicleService.getVehicle(vehicleId.toLongOrNull() ?: 0)) {
                is ApiResult.Success -> {
                    vehicle = result.data.data
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
        title = vehicle?.registrationNumber ?: "Vehicle Details",
        subtitle = "View and manage vehicle information",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/vehicles"
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
        } else {
            vehicle?.let { v ->
                Column(modifier = Modifier.fillMaxWidth().gap(24.px)) {
                    // Vehicle Info Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(ThemeColors.surface)
                            .borderRadius(12.px)
                            .padding(24.px)
                            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.px)
                                        .backgroundColor(ThemeColors.primaryLight)
                                        .borderRadius(12.px)
                                        .margin(right = 16.px),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FaTruck(modifier = Modifier.color(ThemeColors.primary), size = IconSize.X2)
                                }

                                Column {
                                    Span(attrs = Modifier.fontSize(24.px).fontWeight(FontWeight.Bold).color(ThemeColors.textPrimary).toAttrs()) {
                                        Text(v.registrationNumber)
                                    }
                                    Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()) {
                                        Text("${v.make} ${v.model} ${if (v.year > 0) "(${v.year})" else ""}")
                                    }
                                    Row(modifier = Modifier.margin(top = 8.px), verticalAlignment = Alignment.CenterVertically) {
                                        StatusBadge(v.state)
                                    }
                                }
                            }

                            Button(
                                attrs = Modifier
                                    .backgroundColor(ThemeColors.primary)
                                    .color(Color.white)
                                    .padding(8.px, 16.px)
                                    .borderRadius(8.px)
                                    .border(0.px)
                                    .cursor(Cursor.Pointer)
                                    .onClick { /* TODO: Edit vehicle */ }
                                    .toAttrs()
                            ) {
                                FaPen(modifier = Modifier.margin(right = 6.px), size = IconSize.SM)
                                Text("Edit")
                            }
                        }
                    }

                    // Details Grid
                    Row(modifier = Modifier.fillMaxWidth().gap(24.px)) {
                        // Basic Info
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .backgroundColor(ThemeColors.surface)
                                .borderRadius(12.px)
                                .padding(20.px)
                                .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
                        ) {
                            Span(attrs = Modifier.fontSize(16.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 16.px).toAttrs()) {
                                Text("Basic Information")
                            }

                            DetailRow("Vehicle Type", v.vehicleType.replaceFirstChar { it.uppercase() })
                            DetailRow("Fuel Type", v.fuelType.replaceFirstChar { it.uppercase() })
                            DetailRow("Capacity", if (v.capacity > 0) v.capacity.toString() else "-")
                            DetailRow("Status", v.state)
                        }

                        // Additional Info
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .backgroundColor(ThemeColors.surface)
                                .borderRadius(12.px)
                                .padding(20.px)
                                .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
                        ) {
                            Span(attrs = Modifier.fontSize(16.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 16.px).toAttrs()) {
                                Text("Additional Details")
                            }

                            DetailRow("Make", v.make.ifBlank { "-" })
                            DetailRow("Model", v.model.ifBlank { "-" })
                            DetailRow("Year", if (v.year > 0) v.year.toString() else "-")
                            DetailRow("Created", v.createdAt?.take(10) ?: "-")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(topBottom = 8.px),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()) {
            Text(label)
        }
        Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).toAttrs()) {
            Text(value)
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "active" -> ThemeColors.successBg to ThemeColors.success
        "on_trip", "on-trip" -> ThemeColors.primaryLight to ThemeColors.primary
        "maintenance" -> ThemeColors.warningBg to ThemeColors.warning
        "inactive" -> ThemeColors.errorBg to ThemeColors.error
        else -> ThemeColors.border to ThemeColors.textSecondary
    }

    Span(
        attrs = Modifier
            .backgroundColor(bgColor)
            .color(textColor)
            .padding(4.px, 12.px)
            .borderRadius(12.px)
            .fontSize(12.px)
            .fontWeight(FontWeight.Medium)
            .toAttrs()
    ) {
        Text(status.replace("_", " ").replaceFirstChar { it.uppercase() })
    }
}

