package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
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
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
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
    var showMobileMenu by remember { mutableStateOf(false) }

    // Detect screen size
    val breakpoint = rememberBreakpoint()
    val isMobile = breakpoint < Breakpoint.MD

    // Force recomposition when theme changes
    val isDarkMode = ThemeState.currentTheme.value

    if (isMobile) {
        // Mobile Layout
        MobileLayout(
            title = title,
            subtitle = subtitle,
            currentUser = currentUser,
            currentRoute = currentRoute,
            showMobileMenu = showMobileMenu,
            onToggleMobileMenu = { showMobileMenu = !showMobileMenu },
            showProfileMenu = showProfileMenu,
            onToggleProfileMenu = { showProfileMenu = !showProfileMenu },
            onNavigate = { route ->
                showMobileMenu = false
                showProfileMenu = false
                onNavigate(route)
            },
            onLogout = {
                showProfileMenu = false
                showMobileMenu = false
                AuthState.onLogout()
                onNavigate("/auth/login")
            },
            content = content
        )
    } else {
        // Desktop Layout
        DesktopLayout(
            title = title,
            subtitle = subtitle,
            currentUser = currentUser,
            currentRoute = currentRoute,
            showProfileMenu = showProfileMenu,
            onToggleProfileMenu = { showProfileMenu = !showProfileMenu },
            onNavigate = { route ->
                showProfileMenu = false
                onNavigate(route)
            },
            onLogout = {
                showProfileMenu = false
                AuthState.onLogout()
                onNavigate("/auth/login")
            },
            content = content
        )
    }

    // Click outside to close menus
    if (showProfileMenu || showMobileMenu) {
        Box(
            modifier = Modifier
                .position(Position.Fixed)
                .top(0.px)
                .left(0.px)
                .width(100.vw)
                .height(100.vh)
                .zIndex(150)
                .onClick {
                    showProfileMenu = false
                    showMobileMenu = false
                }
        )
    }
}

// ==================== MOBILE LAYOUT ====================

@Composable
private fun MobileLayout(
    title: String,
    subtitle: String,
    currentUser: com.indusjs.fleet.web.models.User?,
    currentRoute: String,
    showMobileMenu: Boolean,
    onToggleMobileMenu: () -> Unit,
    showProfileMenu: Boolean,
    onToggleProfileMenu: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .backgroundColor(ThemeColors.background)
    ) {
        // Mobile Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(ThemeColors.surface)
                .padding(12.px, 16.px)
                .boxShadow(BoxShadow.of(0.px, 2.px, 8.px, color = ThemeColors.cardShadow))
                .position(Position.Sticky)
                .top(0.px)
                .zIndex(100),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo & Menu Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Menu Toggle Button
                Box(
                    modifier = Modifier
                        .size(40.px)
                        .backgroundColor(if (showMobileMenu) ThemeColors.primaryLight else Color.transparent)
                        .borderRadius(8.px)
                        .cursor(Cursor.Pointer)
                        .onClick { onToggleMobileMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    if (showMobileMenu) {
                        FaXmark(modifier = Modifier.color(ThemeColors.primary))
                    } else {
                        FaBars(modifier = Modifier.color(ThemeColors.textPrimary))
                    }
                }

                // Logo
                Row(
                    modifier = Modifier.margin(left = 12.px),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.px)
                            .backgroundColor(ThemeColors.primary)
                            .borderRadius(8.px),
                        contentAlignment = Alignment.Center
                    ) {
                        Span(attrs = Modifier.color(Color.white).fontWeight(FontWeight.Bold).fontSize(14.px).toAttrs()) {
                            Text("F")
                        }
                    }
                    Span(
                        attrs = Modifier
                            .margin(left = 8.px)
                            .fontSize(16.px)
                            .fontWeight(FontWeight.Bold)
                            .color(ThemeColors.textPrimary)
                            .toAttrs()
                    ) {
                        Text("IndusJS Fleet")
                    }
                }
            }

            // Right side - Theme & Profile
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.gap(8.px)) {
                // Theme Toggle
                Box(
                    modifier = Modifier
                        .size(36.px)
                        .backgroundColor(if (ThemeState.isDarkMode) ThemeColors.primaryLight else ThemeColors.warningBg)
                        .borderRadius(8.px)
                        .cursor(Cursor.Pointer)
                        .onClick { ThemeState.toggleTheme() },
                    contentAlignment = Alignment.Center
                ) {
                    if (ThemeState.isDarkMode) {
                        FaMoon(modifier = Modifier.color(ThemeColors.primary), size = IconSize.SM)
                    } else {
                        FaLightbulb(modifier = Modifier.color(ThemeColors.warning), size = IconSize.SM)
                    }
                }

                // Profile Button
                Box(
                    modifier = Modifier
                        .size(36.px)
                        .backgroundColor(ThemeColors.primaryLight)
                        .borderRadius(18.px)
                        .cursor(Cursor.Pointer)
                        .onClick { onToggleProfileMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Span(attrs = Modifier.color(ThemeColors.primary).fontWeight(FontWeight.SemiBold).fontSize(14.px).toAttrs()) {
                        Text(currentUser?.firstName?.firstOrNull()?.toString() ?: "U")
                    }
                }
            }
        }

        // Mobile Dropdown Menu - Overlay (not pushing content)
        if (showMobileMenu) {
            Column(
                modifier = Modifier
                    .position(Position.Fixed)
                    .top(60.px)  // Below the header
                    .left(0.px)
                    .fillMaxWidth()
                    .backgroundColor(ThemeColors.surface)
                    .boxShadow(BoxShadow.of(0.px, 4.px, 12.px, color = ThemeColors.cardShadow))
                    .zIndex(200)
                    .maxHeight(80.vh)
                    .overflow(Overflow.Auto)
            ) {
                Column(modifier = Modifier.padding(8.px)) {
                    MobileNavItem("Dashboard", currentRoute == "/dashboard", { onNavigate("/dashboard") }) { FaHouse(modifier = it, size = IconSize.SM) }
                    MobileNavItem("Vehicles", currentRoute == "/vehicles", { onNavigate("/vehicles") }) { FaTruck(modifier = it, size = IconSize.SM) }
                    MobileNavItem("Drivers", currentRoute == "/drivers", { onNavigate("/drivers") }) { FaIdCard(modifier = it, size = IconSize.SM) }
                    MobileNavItem("Trips", currentRoute == "/trips", { onNavigate("/trips") }) { FaRoute(modifier = it, size = IconSize.SM) }

                    Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 8.px).toAttrs()) {}
                    Span(attrs = Modifier.fontSize(11.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textMuted).padding(left = 12.px).margin(bottom = 4.px).toAttrs()) {
                        Text("COSTS")
                    }
                    MobileNavItem("Trip Costs", currentRoute.startsWith("/costs/trips"), { onNavigate("/costs/trips") }) { FaReceipt(modifier = it, size = IconSize.SM) }
                    MobileNavItem("Maintenance", currentRoute.startsWith("/costs/maintenance"), { onNavigate("/costs/maintenance") }) { FaWrench(modifier = it, size = IconSize.SM) }

                    Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 8.px).toAttrs()) {}
                    MobileNavItem("Reports", currentRoute == "/reports", { onNavigate("/reports") }) { FaChartLine(modifier = it, size = IconSize.SM) }

                    if (currentUser?.userRole == UserRole.OWNER) {
                        Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 8.px).toAttrs()) {}
                        MobileNavItem("Team", currentRoute == "/team", { onNavigate("/team") }) { FaUsers(modifier = it, size = IconSize.SM) }
                    }

                    Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 8.px).toAttrs()) {}
                    MobileNavItem("Settings", currentRoute == "/settings", { onNavigate("/settings") }) { FaGear(modifier = it, size = IconSize.SM) }
                }
            }
        }

        // Mobile Profile Dropdown
        if (showProfileMenu) {
            Column(
                modifier = Modifier
                    .position(Position.Fixed)
                    .top(60.px)
                    .right(12.px)
                    .width(250.px)
                    .backgroundColor(ThemeColors.surface)
                    .borderRadius(12.px)
                    .border(1.px, LineStyle.Solid, ThemeColors.border)
                    .boxShadow(BoxShadow.of(0.px, 8.px, 24.px, color = ThemeColors.cardShadow))
                    .zIndex(250)
                    .overflow(Overflow.Hidden)
            ) {
                // User Info Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.px)
                        .backgroundColor(ThemeColors.primaryLight)
                ) {
                    Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                        Text(currentUser?.fullName ?: "User")
                    }
                    Span(attrs = Modifier.fontSize(12.px).color(ThemeColors.textSecondary).toAttrs()) {
                        Text(currentUser?.email ?: "")
                    }
                }

                Column(modifier = Modifier.padding(8.px)) {
                    MobileMenuItem("Profile", { FaUser(modifier = it, size = IconSize.SM) }) { onNavigate("/profile") }
                    MobileMenuItem("Settings", { FaGear(modifier = it, size = IconSize.SM) }) { onNavigate("/settings") }
                    Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).margin(topBottom = 4.px).toAttrs()) {}
                    MobileMenuItem("Sign Out", { FaRightFromBracket(modifier = it, size = IconSize.SM) }, isDestructive = true) { onLogout() }
                }
            }
        }

        // Page Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.px)
        ) {
            // Page Title
            Column(modifier = Modifier.margin(bottom = 16.px)) {
                H1(
                    attrs = Modifier
                        .fontSize(22.px)
                        .fontWeight(FontWeight.Bold)
                        .color(ThemeColors.textPrimary)
                        .margin(0.px)
                        .toAttrs()
                ) {
                    Text(title)
                }
                Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textSecondary).toAttrs()) {
                    Text(subtitle)
                }
            }

            content()
        }
    }
}

@Composable
private fun MobileNavItem(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    icon: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.px, 14.px)
            .borderRadius(8.px)
            .backgroundColor(if (isActive) ThemeColors.primaryLight else Color.transparent)
            .cursor(Cursor.Pointer)
            .onClick { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.color(if (isActive) ThemeColors.primary else ThemeColors.textSecondary))
        Span(
            attrs = Modifier
                .margin(left = 12.px)
                .fontSize(15.px)
                .fontWeight(if (isActive) FontWeight.Medium else FontWeight.Normal)
                .color(if (isActive) ThemeColors.primary else ThemeColors.textPrimary)
                .toAttrs()
        ) {
            Text(label)
        }
    }
}

@Composable
private fun MobileMenuItem(
    label: String,
    icon: @Composable (Modifier) -> Unit,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.px, 14.px)
            .borderRadius(8.px)
            .cursor(Cursor.Pointer)
            .onClick { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.color(if (isDestructive) ThemeColors.error else ThemeColors.textSecondary))
        Span(
            attrs = Modifier
                .margin(left = 12.px)
                .fontSize(14.px)
                .color(if (isDestructive) ThemeColors.error else ThemeColors.textPrimary)
                .toAttrs()
        ) {
            Text(label)
        }
    }
}

// ==================== DESKTOP LAYOUT ====================

@Composable
private fun DesktopLayout(
    title: String,
    subtitle: String,
    currentUser: com.indusjs.fleet.web.models.User?,
    currentRoute: String,
    showProfileMenu: Boolean,
    onToggleProfileMenu: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
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
                            .onClick { onToggleProfileMenu() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.px)
                                .backgroundColor(ThemeColors.primaryLight)
                                .borderRadius(18.px),
                            contentAlignment = Alignment.Center
                        ) {
                            Span(attrs = Modifier.color(ThemeColors.primary).fontWeight(FontWeight.SemiBold).toAttrs()) {
                                Text(currentUser?.firstName?.firstOrNull()?.toString() ?: "U")
                            }
                        }
                        Column(modifier = Modifier.margin(left = 10.px).weight(1f)) {
                            Span(attrs = Modifier.fontSize(14.px).fontWeight(FontWeight.Medium).color(ThemeColors.textPrimary).toAttrs()) {
                                Text(currentUser?.fullName ?: "User")
                            }
                            Span(attrs = Modifier.fontSize(12.px).color(ThemeColors.textSecondary).toAttrs()) {
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
                                    onNavigate("/profile")
                                }
                                EnhancedMenuItem("Settings", "Account settings", { FaGear(modifier = it) }) {
                                    onNavigate("/settings")
                                }
                            }

                            // Logout section
                            Div(attrs = Modifier.height(1.px).fillMaxWidth().backgroundColor(ThemeColors.border).toAttrs()) {}
                            Column(modifier = Modifier.padding(8.px)) {
                                EnhancedMenuItem("Sign Out", "Log out of your account", { FaRightFromBracket(modifier = it) }, isDestructive = true) {
                                    onLogout()
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
            // Page Header with Theme Toggle
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
                    Span(attrs = Modifier.fontSize(14.px).color(ThemeColors.textSecondary).toAttrs()) {
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
                .backgroundColor(if (isDestructive) ThemeColors.errorBg else ThemeColors.primaryLight)
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

