package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.ReportService
import com.indusjs.fleet.web.api.services.ProfitLossReport
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page("/reports")
@Composable
fun ReportsPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var profitLossReport by remember { mutableStateOf<ProfitLossReport?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedPeriod by remember { mutableStateOf("month") }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        scope.launch {
            when (val result = ReportService.getFleetProfitLoss(period = selectedPeriod)) {
                is ApiResult.Success -> {
                    profitLossReport = result.data.data
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

    fun loadReport(period: String) {
        selectedPeriod = period
        isLoading = true
        scope.launch {
            when (val result = ReportService.getFleetProfitLoss(period = period)) {
                is ApiResult.Success -> {
                    profitLossReport = result.data.data
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
        title = "Reports",
        subtitle = "View financial reports and analytics",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/reports"
    ) {
        // Period Selector
        Row(
            modifier = Modifier.fillMaxWidth().margin(bottom = 24.px),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Span(attrs = Modifier.fontSize(20.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                Text("Profit & Loss Report")
            }

            Row(modifier = Modifier.gap(8.px)) {
                listOf("week" to "Week", "month" to "Month", "year" to "Year").forEach { (value, label) ->
                    Button(
                        attrs = Modifier
                            .backgroundColor(if (selectedPeriod == value) ThemeColors.primary else Color.white)
                            .color(if (selectedPeriod == value) Color.white else ThemeColors.textPrimary)
                            .padding(8.px, 16.px)
                            .borderRadius(8.px)
                            .border(1.px, LineStyle.Solid, if (selectedPeriod == value) ThemeColors.primary else ThemeColors.border)
                            .cursor(Cursor.Pointer)
                            .onClick { loadReport(value) }
                            .toAttrs()
                    ) {
                        Text(label)
                    }
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
        } else {
            // Summary Cards
            Row(modifier = Modifier.fillMaxWidth().gap(20.px).margin(bottom = 24.px)) {
                ReportCard(
                    title = "Total Revenue",
                    value = FormatUtils.formatCurrency(profitLossReport?.totalRevenue),
                    icon = { FaIndianRupeeSign(modifier = it) },
                    color = ThemeColors.success,
                    modifier = Modifier.weight(1f)
                )
                ReportCard(
                    title = "Total Expenses",
                    value = FormatUtils.formatCurrency(profitLossReport?.totalExpenses),
                    icon = { FaReceipt(modifier = it) },
                    color = ThemeColors.error,
                    modifier = Modifier.weight(1f)
                )
                ReportCard(
                    title = "Net Profit",
                    value = FormatUtils.formatCurrency(profitLossReport?.profitLoss),
                    icon = { FaChartLine(modifier = it) },
                    color = if ((profitLossReport?.profitLoss ?: 0.0) >= 0) ThemeColors.success else ThemeColors.error,
                    modifier = Modifier.weight(1f)
                )
            }

            // Expense Breakdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .backgroundColor(ThemeColors.surface)
                    .borderRadius(12.px)
                    .padding(24.px)
                    .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
            ) {
                H3(attrs = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).margin(bottom = 20.px).toAttrs()) {
                    Text("Expense Breakdown")
                }

                val breakdown = profitLossReport?.expenseBreakdown

                ExpenseRow("Fuel Costs", breakdown?.fuelCost) { FaGasPump(modifier = it) }
                ExpenseRow("Maintenance", breakdown?.maintenanceCost) { FaWrench(modifier = it) }
                ExpenseRow("Toll Charges", breakdown?.tollCost) { FaRoad(modifier = it) }
                ExpenseRow("Driver Allowance", breakdown?.driverAllowance) { FaUser(modifier = it) }
                ExpenseRow("Other Expenses", breakdown?.otherCosts) { FaEllipsis(modifier = it) }
            }
        }
    }
}

@Composable
private fun ReportCard(
    title: String,
    value: String,
    icon: @Composable (Modifier) -> Unit,
    color: CSSColorValue,
    modifier: Modifier = Modifier
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
                Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textSecondary).toAttrs()) {
                    Text(title)
                }
                Span(attrs = Modifier.fontSize(24.px).fontWeight(FontWeight.Bold).color(ThemeColors.textPrimary).margin(top = 8.px).toAttrs()) {
                    Text(value)
                }
            }
            Box(
                modifier = Modifier.size(44.px).borderRadius(10.px),
                contentAlignment = Alignment.Center
            ) {
                icon(Modifier.color(color))
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    label: String,
    amount: Double?,
    icon: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.px)
            .borderRadius(8.px)
            .margin(bottom = 8.px),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon(Modifier.color(ThemeColors.textSecondary).margin(right = 12.px))
            Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textPrimary).toAttrs()) {
                Text(label)
            }
        }
        Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
            Text(FormatUtils.formatCurrency(amount))
        }
    }
}

