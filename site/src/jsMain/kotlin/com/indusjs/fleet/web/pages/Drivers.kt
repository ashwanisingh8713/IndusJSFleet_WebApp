package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.DriverService
import com.indusjs.fleet.web.theme.ThemeColors
import com.indusjs.fleet.web.models.*
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

@Page("/drivers")
@Composable
fun DriversPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var drivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        scope.launch {
            when (val result = DriverService.listDrivers()) {
                is ApiResult.Success -> {
                    drivers = result.data.data
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
        title = "Drivers",
        subtitle = "Manage your fleet drivers",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/drivers"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().margin(bottom = 24.px),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Span(attrs = Modifier.fontSize(20.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                Text("All Drivers (${drivers.size})")
            }

            Button(
                attrs = Modifier
                    .backgroundColor(ThemeColors.primary)
                    .color(Color.white)
                    .padding(10.px, 20.px)
                    .borderRadius(8.px)
                    .border(0.px)
                    .cursor(Cursor.Pointer)
                    .onClick { ctx.router.navigateTo("/drivers/new") }
                    .toAttrs()
            ) {
                FaPlus(modifier = Modifier.margin(right = 8.px))
                Text("Add Driver")
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
        } else if (drivers.isEmpty()) {
            EmptyState(
                icon = { FaIdCard(modifier = it) },
                title = "No Drivers Yet",
                message = "Add your first driver to manage your team"
            )
        } else {
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
                drivers.forEach { driver ->
                    DriverCard(driver) { ctx.router.navigateTo("/drivers/${driver.id}") }
                }
            }
        }
    }
}

@Composable
private fun DriverCard(driver: Driver, onClick: () -> Unit) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.px)
                        .backgroundColor(ThemeColors.primaryLight)
                        .borderRadius(22.px)
                        .margin(right = 12.px),
                    contentAlignment = Alignment.Center
                ) {
                    Span(attrs = Modifier.color(ThemeColors.primary).fontWeight(FontWeight.SemiBold).toAttrs()) {
                        Text(driver.firstName.firstOrNull()?.toString() ?: "D")
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Span(attrs = Modifier.fontSize(16.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                        Text("${driver.firstName} ${driver.lastName}")
                    }
                    Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textSecondary).toAttrs()) {
                        Text(driver.mobile)
                    }
                }

                StatusBadge(driver.status)
            }

            if (driver.licenseNumber.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaIdCard(modifier = Modifier.color(ThemeColors.textMuted).margin(right = 6.px))
                    Span(attrs = Modifier.fontSize(12.px).color(ThemeColors.textMuted).toAttrs()) {
                        Text("License: ${driver.licenseNumber}")
                    }
                }
            }
        }
    }
}

