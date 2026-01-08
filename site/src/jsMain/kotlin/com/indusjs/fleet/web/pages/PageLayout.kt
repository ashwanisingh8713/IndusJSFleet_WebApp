package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.components.widgets.ThemeToggleCompact
import com.indusjs.fleet.web.models.UserRole
import com.indusjs.fleet.web.state.AuthState
import com.indusjs.fleet.web.theme.ThemeColors
import com.indusjs.fleet.web.theme.ThemeState
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun PageLayout(
    title: String,
    subtitle: String,
    onNavigate: (String) -> Unit,
    currentRoute: String,
    content: @Composable () -> Unit
) {
    val currentUser = AuthState.currentUser.value
    var showProfileMenu by remember { mutableStateOf(false) }

    // Force recomposition when theme changes
    val isDarkMode = ThemeState.currentTheme.value

    Row(
        modifier = Modifier
            .fillMaxSize()
            .backgroundColor(ThemeColors.background)
    ) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(260.px)
                .height(100.vh)
                .backgroundColor(ThemeColors.sidebarBackground)
                .boxShadow(BoxShadow.of(2.px, 0.px, 8.px, color = ThemeColors.cardShadow))
                .position(Position.Fixed)
                .top(0.px)
                .left(0.px)
                .zIndex(100)
        ) {
            // Logo
            Row(
                modifier = Modifier.padding(20.px).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.px)
                        .backgroundColor(ThemeColors.primary)
                        .borderRadius(10.px),
                    contentAlignment = Alignment.Center
                ) {
                    Span(attrs = Modifier.color(Color.white).fontWeight(FontWeight.Bold).toAttrs()) {
                        Text("F")
                    }
                }
                Span(
                    attrs = Modifier
                        .margin(left = 12.px)
                        .fontSize(18.px)
                        .fontWeight(FontWeight.Bold)
                        .color(ThemeColors.textPrimary)
                        .toAttrs()
                ) {
                    Text("IndusJS Fleet")
                }
            }

            Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).toAttrs()) {}

            // Navigation
            Column(modifier = Modifier.padding(12.px).weight(1f)) {
                SidebarNavItem("Dashboard", currentRoute == "/dashboard", onNavigate = { onNavigate("/dashboard") }) { FaHouse(modifier = it) }
                SidebarNavItem("Vehicles", currentRoute == "/vehicles", onNavigate = { onNavigate("/vehicles") }) { FaTruck(modifier = it) }
                SidebarNavItem("Drivers", currentRoute == "/drivers", onNavigate = { onNavigate("/drivers") }) { FaIdCard(modifier = it) }
                SidebarNavItem("Trips", currentRoute == "/trips", onNavigate = { onNavigate("/trips") }) { FaRoute(modifier = it) }
                SidebarNavItem("Reports", currentRoute == "/reports", onNavigate = { onNavigate("/reports") }) { FaChartLine(modifier = it) }

                if (currentUser?.userRole == UserRole.OWNER) {
                    Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 12.px).toAttrs()) {}
                    SidebarNavItem("Team", currentRoute == "/team", onNavigate = { onNavigate("/team") }) { FaUsers(modifier = it) }
                }

                Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 12.px).toAttrs()) {}
                SidebarNavItem("Settings", currentRoute == "/settings", onNavigate = { onNavigate("/settings") }) { FaGear(modifier = it) }
            }

            // Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth().padding(leftRight = 12.px, bottom = 8.px),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Span(attrs = Modifier.fontSize(12.px).color(ThemeColors.textSecondary).toAttrs()) {
                    Text("Theme")
                }
                ThemeToggleCompact()
            }

            // User & Logout
            Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).toAttrs()) {}
            Column(modifier = Modifier.padding(12.px)) {
                // Profile Button
                Box(modifier = Modifier.position(Position.Relative)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.px)
                            .borderRadius(8.px)
                            .cursor(Cursor.Pointer)
                            .backgroundColor(if (showProfileMenu) ThemeColors.primaryLight else Color.transparent)
                            .onClick { showProfileMenu = !showProfileMenu },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.px)
                                .backgroundColor(ThemeColors.primaryLight)
                                .borderRadius(18.px),
                            contentAlignment = Alignment.Center
                        ) {
                            Span(
                                attrs = Modifier.color(ThemeColors.primary).fontWeight(FontWeight.SemiBold).toAttrs()
                            ) {
                                Text(currentUser?.firstName?.firstOrNull()?.toString() ?: "U")
                            }
                        }
                        Column(modifier = Modifier.margin(left = 10.px).weight(1f)) {
                            Span(
                                attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).toAttrs()
                            ) {
                                Text(currentUser?.fullName ?: "User")
                            }
                            Span(
                                attrs = Modifier.fontSize(12.px).color(ThemeColors.textSecondary).toAttrs()
                            ) {
                                Text(currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "")
                            }
                        }
                        FaChevronDown(modifier = Modifier.color(ThemeColors.textMuted))
                    }

                    // Profile Dropdown Menu
                    if (showProfileMenu) {
                        Column(
                            modifier = Modifier
                                .position(Position.Absolute)
                                .bottom(65.px)
                                .left(0.px)
                                .fillMaxWidth()
                                .backgroundColor(ThemeColors.surface)
                                .borderRadius(12.px)
                                .border(1.px, LineStyle.Solid, ThemeColors.border)
                                .boxShadow(BoxShadow.of(0.px, 8.px, 24.px, color = ThemeColors.cardShadow))
                                .zIndex(200)
                                .overflow(Overflow.Hidden)
                        ) {
                            // Header with user info
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.px)
                                    .backgroundColor(ThemeColors.primaryLight)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.px)
                                            .backgroundColor(ThemeColors.primary)
                                            .borderRadius(20.px),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Span(attrs = Modifier.color(Color.white).fontWeight(FontWeight.Bold).fontSize(16.px).toAttrs()) {
                                            Text(currentUser?.firstName?.firstOrNull()?.toString() ?: "U")
                                        }
                                    }
                                    Column(modifier = Modifier.margin(left = 12.px)) {
                                        Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                                            Text(currentUser?.fullName ?: "User")
                                        }
                                        Span(attrs = Modifier.fontSize(12.px).color(ThemeColors.textSecondary).toAttrs()) {
                                            Text(currentUser?.email ?: "")
                                        }
                                    }
                                }
                            }

                            // Menu Items
                            Column(modifier = Modifier.padding(8.px)) {
                                EnhancedMenuItem("Profile", "View and edit your profile", { FaUser(modifier = it) }) {
                                    showProfileMenu = false
                                    onNavigate("/profile")
                                }
                                EnhancedMenuItem("Settings", "Account settings", { FaGear(modifier = it) }) {
                                    showProfileMenu = false
                                    onNavigate("/settings")
                                }
                            }

                            // Logout section
                            Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).toAttrs()) {}
                            Column(modifier = Modifier.padding(8.px)) {
                                EnhancedMenuItem("Sign Out", "Log out of your account", { FaRightFromBracket(modifier = it) }, isDestructive = true) {
                                    showProfileMenu = false
                                    AuthState.onLogout()
                                    onNavigate("/auth/login")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Content
        Column(
            modifier = Modifier
                .margin(left = 260.px)
                .padding(24.px)
                .minHeight(100.vh)
                .fillMaxWidth()
        ) {
            // Page Header with Theme Toggle and Hamburger Menu
            Row(
                modifier = Modifier.fillMaxWidth().margin(bottom = 24.px),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    H1(
                        attrs = Modifier
                            .fontSize(28.px)
                            .fontWeight(FontWeight.Bold)
                            .color(ThemeColors.textPrimary)
                            .margin(0.px)
                            .toAttrs()
                    ) {
                        Text(title)
                    }
                    Span(
                        attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()
                    ) {
                        Text(subtitle)
                    }
                }

                // Theme Toggle Button
                Box(
                    modifier = Modifier
                        .size(44.px)
                        .backgroundColor(if (ThemeState.isDarkMode) ThemeColors.primaryLight else ThemeColors.warningBg)
                        .borderRadius(10.px)
                        .border(1.px, LineStyle.Solid, ThemeColors.border)
                        .cursor(Cursor.Pointer)
                        .boxShadow(BoxShadow.of(0.px, 2.px, 8.px, color = ThemeColors.cardShadow))
                        .onClick { ThemeState.toggleTheme() },
                    contentAlignment = Alignment.Center
                ) {
                    if (ThemeState.isDarkMode) {
                        FaMoon(modifier = Modifier.color(ThemeColors.primary))
                    } else {
                        FaLightbulb(modifier = Modifier.color(ThemeColors.warning))
                    }
                }
            }

            // Page Content
            content()
        }
    }

    // Click outside to close profile menu
    if (showProfileMenu) {
        Box(
            modifier = Modifier
                .position(Position.Fixed)
                .top(0.px)
                .left(0.px)
                .width(100.vw)
                .height(100.vh)
                .zIndex(150)
                .onClick { showProfileMenu = false }
        )
    }
}

@Composable
private fun SidebarNavItem(
    label: String,
    isActive: Boolean = false,
    onNavigate: () -> Unit,
    icon: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.px)
            .borderRadius(8.px)
            .backgroundColor(if (isActive) ThemeColors.primaryLight else Color.transparent)
            .cursor(Cursor.Pointer)
            .onClick { onNavigate() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.color(if (isActive) ThemeColors.primary else ThemeColors.textSecondary))
        Span(
            attrs = Modifier
                .margin(left = 12.px)
                .fontSize(14.px)
                .fontWeight(if (isActive) FontWeight.Medium else FontWeight.Normal)
                .color(if (isActive) ThemeColors.primary else ThemeColors.textPrimary)
                .toAttrs()
        ) {
            Text(label)
        }
    }
}

@Composable
private fun ProfileMenuItem(
    label: String,
    icon: @Composable (Modifier) -> Unit,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.px, 12.px)
            .borderRadius(6.px)
            .cursor(Cursor.Pointer)
            .onClick { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.color(if (isDestructive) ThemeColors.error else ThemeColors.textSecondary).margin(right = 10.px))
        Span(
            attrs = Modifier
                .fontSize(14.px)
                .color(if (isDestructive) ThemeColors.error else ThemeColors.textPrimary)
                .toAttrs()
        ) {
            Text(label)
        }
    }
}

@Composable
private fun EnhancedMenuItem(
    label: String,
    subtitle: String,
    icon: @Composable (Modifier) -> Unit,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.px)
            .borderRadius(10.px)
            .cursor(Cursor.Pointer)
            .onClick { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.px)
                .backgroundColor(
                    if (isDestructive) ThemeColors.errorBg
                    else ThemeColors.primaryLight
                )
                .borderRadius(10.px),
            contentAlignment = Alignment.Center
        ) {
            icon(Modifier.color(if (isDestructive) ThemeColors.error else ThemeColors.primary))
        }
        Column(modifier = Modifier.margin(left = 12.px)) {
            Span(
                attrs = Modifier
                    .fontSize(14.px)
                    .fontWeight(FontWeight.Medium)
                    .color(if (isDestructive) ThemeColors.error else ThemeColors.textPrimary)
                    .toAttrs()
            ) {
                Text(label)
            }
            Span(
                attrs = Modifier
                    .fontSize(12.px)
                    .color(ThemeColors.textSecondary)
                    .toAttrs()
            ) {
                Text(subtitle)
            }
        }
    }
}

