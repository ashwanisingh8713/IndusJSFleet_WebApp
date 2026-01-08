package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.DashboardService
import com.indusjs.fleet.web.models.*
import com.indusjs.fleet.web.state.AuthState
import com.indusjs.fleet.web.theme.ThemeColors
import com.indusjs.fleet.web.utils.FormatUtils
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
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val StatCardStyle = CssStyle {
    base {
        Modifier
            .backgroundColor(Color.white) // Will be overridden inline
            .borderRadius(12.px)
            .padding(20.px)
    }
}

@Page("/dashboard")
@Composable
fun DashboardPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var dashboard by remember { mutableStateOf<Dashboard?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val currentUser = AuthState.currentUser.value

    // Check auth and load dashboard
    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        AuthState.initialize()

        scope.launch {
            when (val result = DashboardService.getDashboard()) {
                is ApiResult.Success -> {
                    dashboard = result.data.data
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
        title = "Welcome back, ${currentUser?.firstName ?: "User"}!",
        subtitle = "Here's what's happening with your fleet today",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/dashboard"
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 100.px),
                contentAlignment = Alignment.Center
            ) {
                FaSpinner(size = IconSize.X2)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .backgroundColor(Color("#fee2e2"))
                    .padding(16.px)
                    .borderRadius(8.px)
            ) {
                Text("Error: $error")
            }
        } else {
            dashboard?.let { data ->
                // Stats Grid
                StatsGrid(data.fleetOverview, data.todaySummary)

                // Quick Actions & Alerts
                Row(
                    modifier = Modifier.fillMaxWidth().margin(top = 24.px).gap(24.px)
                ) {
                    QuickActionsCard(
                        onNavigate = { ctx.router.navigateTo(it) },
                        modifier = Modifier.weight(1f)
                    )
                    AlertsCard(
                        alerts = data.alerts,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsGrid(fleetOverview: FleetOverview?, todaySummary: TodaySummary?) {
    // Row 1: Fleet Overview
    Row(
        modifier = Modifier.fillMaxWidth().gap(20.px)
    ) {
        StatCard(
            title = "Total Vehicles",
            value = fleetOverview?.totalVehicles?.toString() ?: "0",
            subtitle = "${fleetOverview?.activeVehicles ?: 0} active",
            color = ThemeColors.primary,
            modifier = Modifier.weight(1f)
        ) { FaTruck(modifier = it) }

        StatCard(
            title = "On Trip",
            value = fleetOverview?.onTrip?.toString() ?: "0",
            subtitle = "vehicles in transit",
            color = ThemeColors.success,
            modifier = Modifier.weight(1f)
        ) { FaRoute(modifier = it) }

        StatCard(
            title = "Total Drivers",
            value = fleetOverview?.totalDrivers?.toString() ?: "0",
            subtitle = "${fleetOverview?.availableDrivers ?: 0} available",
            color = Color("#8b5cf6"),
            modifier = Modifier.weight(1f)
        ) { FaIdCard(modifier = it) }

        StatCard(
            title = "Maintenance",
            value = fleetOverview?.maintenance?.toString() ?: "0",
            subtitle = "under service",
            color = ThemeColors.warning,
            modifier = Modifier.weight(1f)
        ) { FaWrench(modifier = it) }
    }

    // Row 2: Today's Summary
    Row(
        modifier = Modifier.fillMaxWidth().margin(top = 20.px).gap(20.px)
    ) {
        StatCard(
            title = "Today's Trips",
            value = "${todaySummary?.tripsCompleted ?: 0}/${(todaySummary?.tripsPlanned ?: 0) + (todaySummary?.tripsInProgress ?: 0) + (todaySummary?.tripsCompleted ?: 0)}",
            subtitle = "${todaySummary?.tripsInProgress ?: 0} in progress",
            color = ThemeColors.primary,
            modifier = Modifier.weight(1f)
        ) { FaCalendarCheck(modifier = it) }

        StatCard(
            title = "Revenue",
            value = FormatUtils.formatCurrency(todaySummary?.totalRevenue),
            subtitle = "today's earnings",
            color = ThemeColors.success,
            modifier = Modifier.weight(1f)
        ) { FaIndianRupeeSign(modifier = it) }

        StatCard(
            title = "Expenses",
            value = FormatUtils.formatCurrency(todaySummary?.totalExpenses),
            subtitle = "today's costs",
            color = ThemeColors.error,
            modifier = Modifier.weight(1f)
        ) { FaReceipt(modifier = it) }

        StatCard(
            title = "Profit/Loss",
            value = FormatUtils.formatCurrency(todaySummary?.profitLoss),
            subtitle = if ((todaySummary?.profitLoss ?: 0.0) >= 0) "profit" else "loss",
            color = if ((todaySummary?.profitLoss ?: 0.0) >= 0) ThemeColors.success else ThemeColors.error,
            modifier = Modifier.weight(1f)
        ) { FaChartLine(modifier = it) }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: CSSColorValue,
    modifier: Modifier = Modifier,
    icon: @Composable (Modifier) -> Unit
) {
    Box(
        modifier = modifier
            .backgroundColor(ThemeColors.surface)
            .borderRadius(12.px)
            .padding(20.px)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Span(
                    attrs = Modifier.fontSize(13.px).color(ThemeColors.textSecondary).toAttrs()
                ) {
                    Text(title)
                }
                Span(
                    attrs = Modifier
                        .fontSize(28.px)
                        .fontWeight(FontWeight.Bold)
                        .color(ThemeColors.textPrimary)
                        .margin(top = 4.px, bottom = 4.px)
                        .toAttrs()
                ) {
                    Text(value)
                }
                Span(
                    attrs = Modifier.fontSize(12.px).color(ThemeColors.textMuted).toAttrs()
                ) {
                    Text(subtitle)
                }
            }
            Box(
                modifier = Modifier
                    .size(44.px)
                    .borderRadius(10.px),
                contentAlignment = Alignment.Center
            ) {
                icon(Modifier.color(color))
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .backgroundColor(ThemeColors.surface)
            .borderRadius(12.px)
            .padding(20.px)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
    ) {
        H3(
            attrs = Modifier
                .fontSize(16.px)
                .fontWeight(FontWeight.SemiBold)
                .color(ThemeColors.textPrimary)
                .margin(bottom = 16.px)
                .toAttrs()
        ) {
            Text("Quick Actions")
        }

        Row(modifier = Modifier.fillMaxWidth().gap(12.px)) {
            QuickActionButton("Add Vehicle", ThemeColors.primary, onClick = { onNavigate("/vehicles/new") }) { FaTruck(modifier = it) }
            QuickActionButton("Add Driver", Color("#8b5cf6"), onClick = { onNavigate("/drivers/new") }) { FaUserPlus(modifier = it) }
            QuickActionButton("New Trip", ThemeColors.success, onClick = { onNavigate("/trips/new") }) { FaPlus(modifier = it) }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    color: CSSColorValue,
    onClick: () -> Unit,
    icon: @Composable (Modifier) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.px)
            .borderRadius(8.px)
            .border(1.px, LineStyle.Dashed, ThemeColors.border)
            .cursor(Cursor.Pointer)
            .onClick { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon(Modifier.color(color))
        Span(
            attrs = Modifier
                .fontSize(12.px)
                .color(ThemeColors.textSecondary)
                .margin(top = 8.px)
                .toAttrs()
        ) {
            Text(label)
        }
    }
}

@Composable
private fun AlertsCard(
    alerts: List<DashboardAlert>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .backgroundColor(ThemeColors.surface)
            .borderRadius(12.px)
            .padding(20.px)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
    ) {
        H3(
            attrs = Modifier
                .fontSize(16.px)
                .fontWeight(FontWeight.SemiBold)
                .color(ThemeColors.textPrimary)
                .margin(bottom = 16.px)
                .toAttrs()
        ) {
            Text("Recent Alerts")
        }

        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(20.px),
                contentAlignment = Alignment.Center
            ) {
                Span(
                    attrs = Modifier.fontSize(14.px).color(ThemeColors.textMuted).toAttrs()
                ) {
                    Text("No alerts")
                }
            }
        } else {
            alerts.take(3).forEach { alert ->
                AlertItem(alert)
            }
        }
    }
}

@Composable
private fun AlertItem(alert: DashboardAlert) {
    val (bgColor, iconColor) = when (alert.priority.lowercase()) {
        "critical" -> Pair(ThemeColors.errorBg, ThemeColors.error)
        "warning" -> Pair(ThemeColors.warningBg, ThemeColors.warning)
        else -> Pair(ThemeColors.infoBg, ThemeColors.primary)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(bgColor)
            .padding(12.px)
            .borderRadius(8.px)
            .margin(bottom = 8.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaTriangleExclamation(modifier = Modifier.color(iconColor))
        Column(modifier = Modifier.margin(left = 12.px)) {
            Span(
                attrs = Modifier
                    .fontSize(13.px)
                    .fontWeight(FontWeight.Medium)
                    .color(ThemeColors.textPrimary)
                    .toAttrs()
            ) {
                Text(alert.title)
            }
            Span(
                attrs = Modifier.fontSize(12.px).color(ThemeColors.textSecondary).toAttrs()
            ) {
                Text(alert.message)
            }
        }
    }
}

