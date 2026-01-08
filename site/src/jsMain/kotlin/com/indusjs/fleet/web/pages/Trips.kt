package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.TripService
import com.indusjs.fleet.web.theme.ThemeColors
import com.indusjs.fleet.web.models.*
import com.indusjs.fleet.web.utils.DateUtils
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

@Page("/trips")
@Composable
fun TripsPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var trips by remember { mutableStateOf<List<TripListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        scope.launch {
            when (val result = TripService.listTrips()) {
                is ApiResult.Success -> {
                    trips = result.data.data
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
        title = "Trips",
        subtitle = "Manage and track all trips",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/trips"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().margin(bottom = 24.px),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Span(attrs = Modifier.fontSize(20.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                Text("All Trips (${trips.size})")
            }

            Button(
                attrs = Modifier
                    .backgroundColor(ThemeColors.primary)
                    .color(Color.white)
                    .padding(10.px, 20.px)
                    .borderRadius(8.px)
                    .border(0.px)
                    .cursor(Cursor.Pointer)
                    .onClick { ctx.router.navigateTo("/trips/new") }
                    .toAttrs()
            ) {
                FaPlus(modifier = Modifier.margin(right = 8.px))
                Text("New Trip")
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
        } else if (trips.isEmpty()) {
            EmptyState(
                icon = { FaRoute(modifier = it) },
                title = "No Trips Yet",
                message = "Create your first trip to start tracking"
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().gap(16.px)) {
                trips.forEach { trip ->
                    TripCard(trip) { ctx.router.navigateTo("/trips/${trip.id}") }
                }
            }
        }
    }
}

@Composable
private fun TripCard(trip: TripListItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaRoute(modifier = Modifier.color(ThemeColors.primary).margin(right = 8.px))
                    Span(attrs = Modifier.fontSize(16.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                        Text("Trip #${trip.id}")
                    }
                }
                StatusBadge(trip.state)
            }

            Row(
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FaLocationDot(modifier = Modifier.color(ThemeColors.success).margin(right = 6.px))
                Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()) {
                    Text(trip.startLocation)
                }
                FaArrowRight(modifier = Modifier.color(ThemeColors.textMuted).margin(leftRight = 8.px))
                FaLocationDot(modifier = Modifier.color(ThemeColors.error).margin(right = 6.px))
                Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()) {
                    Text(trip.endLocation)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaTruck(modifier = Modifier.color(ThemeColors.textMuted).margin(right = 6.px))
                    Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textMuted).toAttrs()) {
                        Text(trip.vehicleNumber ?: "N/A")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaCalendar(modifier = Modifier.color(ThemeColors.textMuted).margin(right = 6.px))
                    Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textMuted).toAttrs()) {
                        Text(trip.scheduledDate ?: "")
                    }
                }
            }
        }
    }
}

