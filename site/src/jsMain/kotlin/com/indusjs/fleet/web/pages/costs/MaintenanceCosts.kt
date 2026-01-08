package com.indusjs.fleet.web.pages.costs

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.MaintenanceCostService
import com.indusjs.fleet.web.models.*
import com.indusjs.fleet.web.pages.PageLayout
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
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page("/costs/maintenance")
@Composable
fun MaintenanceCostsPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    val isMobile = breakpoint < Breakpoint.MD

    var costs by remember { mutableStateOf<List<MaintenanceCost>>(emptyList()) }
    var totalCost by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCostType by remember { mutableStateOf<String?>(null) }

    // Filtered costs
    val filteredCosts = remember(costs, searchQuery, selectedCostType) {
        costs.filter { cost ->
            val matchesSearch = searchQuery.isBlank() ||
                cost.costType.contains(searchQuery, ignoreCase = true) ||
                cost.description?.contains(searchQuery, ignoreCase = true) == true ||
                cost.vendorName?.contains(searchQuery, ignoreCase = true) == true
            val matchesType = selectedCostType == null || cost.costType == selectedCostType
            matchesSearch && matchesType
        }
    }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        scope.launch {
            when (val result = MaintenanceCostService.listMaintenanceCosts(perPage = 100)) {
                is ApiResult.Success -> {
                    val response = result.data.data
                    costs = response?.costs ?: emptyList()
                    totalCost = response?.totalCost ?: 0.0
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
        title = "Maintenance Costs",
        subtitle = "Track vehicle maintenance expenses",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/costs/maintenance"
    ) {
        // Summary Cards
        if (!isLoading && costs.isNotEmpty()) {
            MaintenanceCostsSummary(costs, totalCost, isMobile)
        }

        // Header with Search and Add
        Row(
            modifier = Modifier.fillMaxWidth().margin(top = 24.px, bottom = 16.px),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Span(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                Text("All Maintenance Costs (${filteredCosts.size})")
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.gap(12.px)) {
                if (!isMobile) {
                    MaintenanceSearchBox(searchQuery) { searchQuery = it }
                }

                Button(
                    attrs = Modifier
                        .backgroundColor(ThemeColors.primary)
                        .color(Color.white)
                        .padding(10.px, 16.px)
                        .borderRadius(8.px)
                        .border(0.px)
                        .cursor(Cursor.Pointer)
                        .onClick { ctx.router.navigateTo("/costs/maintenance/new") }
                        .toAttrs()
                ) {
                    FaPlus(modifier = Modifier.margin(right = 8.px), size = IconSize.SM)
                    Text("Add Cost")
                }
            }
        }

        // Mobile Search
        if (isMobile) {
            MaintenanceSearchBox(searchQuery, Modifier.fillMaxWidth().margin(bottom = 16.px)) { searchQuery = it }
        }

        // Cost Type Filter
        Row(modifier = Modifier.fillMaxWidth().margin(bottom = 16.px).gap(8.px)) {
            MaintenanceFilterChip("All", selectedCostType == null) { selectedCostType = null }
            MaintenanceFilterChip("Tyre", selectedCostType == "tyre") { selectedCostType = "tyre" }
            MaintenanceFilterChip("Oil", selectedCostType == "oil_change") { selectedCostType = "oil_change" }
            MaintenanceFilterChip("Battery", selectedCostType == "battery") { selectedCostType = "battery" }
        }

        // Content
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.px), contentAlignment = Alignment.Center) {
                FaSpinner(size = IconSize.X2)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxWidth().backgroundColor(ThemeColors.errorBg).padding(16.px).borderRadius(8.px)
            ) {
                Span(attrs = Modifier.color(ThemeColors.error).toAttrs()) {
                    Text("Error: $error")
                }
            }
        } else if (filteredCosts.isEmpty()) {
            MaintenanceEmptyState(
                title = if (searchQuery.isBlank() && selectedCostType == null) "No Maintenance Costs Yet" else "No Results Found",
                message = if (searchQuery.isBlank() && selectedCostType == null) "Add your first maintenance cost to track expenses" else "Try adjusting your filters"
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().gap(12.px)) {
                filteredCosts.forEach { cost ->
                    MaintenanceCostCard(cost, isMobile) {
                        ctx.router.navigateTo("/costs/maintenance/${cost.id}")
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceCostsSummary(costs: List<MaintenanceCost>, totalCost: Double, isMobile: Boolean) {
    val tyreCost = costs.filter { it.costType == "tyre" }.sumOf { it.amount }
    val oilCost = costs.filter { it.costType == "oil_change" }.sumOf { it.amount }
    val otherCost = totalCost - tyreCost - oilCost

    Row(
        modifier = Modifier.fillMaxWidth().gap(if (isMobile) 8.px else 16.px),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MaintenanceSummaryCard("Total", totalCost, ThemeColors.primary, isMobile, Modifier.weight(1f))
        MaintenanceSummaryCard("Tyres", tyreCost, ThemeColors.warning, isMobile, Modifier.weight(1f))
        MaintenanceSummaryCard("Oil", oilCost, ThemeColors.success, isMobile, Modifier.weight(1f))
        if (!isMobile) {
            MaintenanceSummaryCard("Other", otherCost, ThemeColors.textSecondary, isMobile, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MaintenanceSummaryCard(title: String, amount: Double, color: CSSColorValue, isMobile: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .backgroundColor(ThemeColors.surface)
            .borderRadius(10.px)
            .padding(if (isMobile) 12.px else 16.px)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
    ) {
        Column {
            Span(attrs = Modifier.fontSize(if (isMobile) 11.px else 12.px).color(ThemeColors.textSecondary).toAttrs()) {
                Text(title)
            }
            Span(attrs = Modifier.fontSize(if (isMobile) 16.px else 20.px).fontWeight(FontWeight.Bold).color(color).margin(top = 4.px).toAttrs()) {
                Text(FormatUtils.formatCurrency(amount))
            }
        }
    }
}

@Composable
private fun MaintenanceCostCard(cost: MaintenanceCost, isMobile: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(ThemeColors.surface)
            .borderRadius(10.px)
            .padding(16.px)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
            .cursor(Cursor.Pointer)
            .onClick { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(if (isMobile) 40.px else 48.px)
                        .backgroundColor(getMaintenanceCostColor(cost.costType).second)
                        .borderRadius(10.px),
                    contentAlignment = Alignment.Center
                ) {
                    getMaintenanceCostIcon(cost.costType, Modifier.color(getMaintenanceCostColor(cost.costType).first))
                }

                Column(modifier = Modifier.margin(left = 12.px)) {
                    Span(attrs = Modifier.fontSize(if (isMobile) 14.px else 16.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                        Text(getMaintenanceDisplayType(cost.costType))
                    }
                    Span(attrs = Modifier.fontSize(12.px).color(ThemeColors.textSecondary).margin(top = 4.px).toAttrs()) {
                        Text(cost.date ?: "-")
                    }
                    if (cost.vendorName != null) {
                        Span(attrs = Modifier.fontSize(11.px).color(ThemeColors.textMuted).margin(top = 2.px).toAttrs()) {
                            Text(cost.vendorName)
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Span(attrs = Modifier.fontSize(if (isMobile) 16.px else 18.px).fontWeight(FontWeight.Bold).color(ThemeColors.textPrimary).toAttrs()) {
                    Text(FormatUtils.formatCurrency(cost.amount))
                }
                if (cost.invoiceNo != null) {
                    Span(attrs = Modifier.fontSize(11.px).color(ThemeColors.textMuted).margin(top = 2.px).toAttrs()) {
                        Text("#${cost.invoiceNo}")
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceSearchBox(query: String, modifier: Modifier = Modifier, onQueryChange: (String) -> Unit) {
    Row(
        modifier = modifier
            .backgroundColor(ThemeColors.surface)
            .border(1.px, LineStyle.Solid, ThemeColors.border)
            .borderRadius(8.px)
            .padding(8.px, 12.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaMagnifyingGlass(modifier = Modifier.color(ThemeColors.textMuted).margin(right = 8.px), size = IconSize.SM)
        Input(InputType.Text) {
            value(query)
            onInput { onQueryChange(it.value) }
            style {
                property("border", "none")
                property("outline", "none")
                property("background", "transparent")
                width(180.px)
                fontSize(14.px)
            }
            attr("placeholder", "Search costs...")
        }
        if (query.isNotBlank()) {
            FaXmark(
                modifier = Modifier.color(ThemeColors.textMuted).cursor(Cursor.Pointer).onClick { onQueryChange("") },
                size = IconSize.SM
            )
        }
    }
}

@Composable
private fun MaintenanceFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Span(
        attrs = Modifier
            .backgroundColor(if (isSelected) ThemeColors.primary else ThemeColors.surface)
            .color(if (isSelected) Color.white else ThemeColors.textPrimary)
            .padding(6.px, 14.px)
            .borderRadius(20.px)
            .border(1.px, LineStyle.Solid, if (isSelected) ThemeColors.primary else ThemeColors.border)
            .fontSize(13.px)
            .cursor(Cursor.Pointer)
            .onClick { onClick() }
            .toAttrs()
    ) {
        Text(label)
    }
}

@Composable
private fun MaintenanceEmptyState(title: String, message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(64.px).backgroundColor(ThemeColors.primaryLight).borderRadius(32.px),
            contentAlignment = Alignment.Center
        ) {
            FaWrench(modifier = Modifier.color(ThemeColors.primary))
        }
        Span(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(top = 16.px).toAttrs()) {
            Text(title)
        }
        Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).margin(top = 8.px).toAttrs()) {
            Text(message)
        }
    }
}

private fun getMaintenanceCostColor(costType: String): Pair<CSSColorValue, CSSColorValue> {
    return when (costType) {
        "tyre" -> ThemeColors.warning to ThemeColors.warningBg
        "oil_change" -> ThemeColors.success to ThemeColors.successBg
        "battery" -> Color("#8b5cf6") to Color("#f3e8ff")
        "brake_service" -> ThemeColors.error to ThemeColors.errorBg
        "engine_repair" -> ThemeColors.primary to ThemeColors.primaryLight
        else -> ThemeColors.textSecondary to ThemeColors.border
    }
}

private fun getMaintenanceDisplayType(costType: String): String {
    return when (costType) {
        "tyre" -> "Tyre"
        "battery" -> "Battery"
        "oil_change" -> "Oil Change"
        "brake_service" -> "Brake Service"
        "engine_repair" -> "Engine Repair"
        "clutch_repair" -> "Clutch Repair"
        "suspension" -> "Suspension"
        "electrical" -> "Electrical"
        "body_work" -> "Body Work"
        "ac_service" -> "AC Service"
        "cleaning" -> "Cleaning"
        "hydraulic_repair" -> "Hydraulic Repair"
        "other" -> "Other"
        else -> costType.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}

@Composable
private fun getMaintenanceCostIcon(costType: String, modifier: Modifier) {
    when (costType) {
        "tyre" -> FaCircle(modifier = modifier, size = IconSize.SM)
        "oil_change" -> FaOilCan(modifier = modifier, size = IconSize.SM)
        "battery" -> FaBatteryFull(modifier = modifier, size = IconSize.SM)
        "brake_service" -> FaCircleStop(modifier = modifier, size = IconSize.SM)
        "engine_repair" -> FaGears(modifier = modifier, size = IconSize.SM)
        else -> FaWrench(modifier = modifier, size = IconSize.SM)
    }
}

