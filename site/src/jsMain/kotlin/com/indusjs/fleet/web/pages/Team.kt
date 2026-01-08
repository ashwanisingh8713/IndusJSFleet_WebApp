package com.indusjs.fleet.web.pages

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.api.services.TeamService
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page("/team")
@Composable
fun TeamPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var teamMembers by remember { mutableStateOf<List<TeamMember>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val currentUser = AuthState.currentUser.value

    LaunchedEffect(Unit) {
        if (!AuthService.isLoggedIn()) {
            ctx.router.navigateTo("/auth/login")
            return@LaunchedEffect
        }

        // Only owners can view team
        if (currentUser?.userRole != UserRole.OWNER) {
            ctx.router.navigateTo("/dashboard")
            return@LaunchedEffect
        }

        scope.launch {
            when (val result = TeamService.listTeamMembers()) {
                is ApiResult.Success -> {
                    teamMembers = result.data.data
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
        title = "Team Management",
        subtitle = "Manage your team members and permissions",
        onNavigate = { ctx.router.navigateTo(it) },
        currentRoute = "/team"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().margin(bottom = 24.px),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Span(attrs = Modifier.fontSize(20.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                Text("Team Members (${teamMembers.size})")
            }

            Button(
                attrs = Modifier
                    .backgroundColor(ThemeColors.primary)
                    .color(Color.white)
                    .padding(10.px, 20.px)
                    .borderRadius(8.px)
                    .border(0.px)
                    .cursor(Cursor.Pointer)
                    .onClick { ctx.router.navigateTo("/team/invite") }
                    .toAttrs()
            ) {
                FaUserPlus(modifier = Modifier.margin(right = 8.px))
                Text("Invite Member")
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
        } else if (teamMembers.isEmpty()) {
            EmptyState(
                icon = { FaUsers(modifier = it) },
                title = "No Team Members Yet",
                message = "Invite managers and operators to help manage your fleet"
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().gap(12.px)) {
                teamMembers.forEach { member ->
                    TeamMemberCard(member)
                }
            }
        }
    }
}

@Composable
private fun TeamMemberCard(member: TeamMember) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(ThemeColors.surface)
            .borderRadius(12.px)
            .padding(20.px)
            .boxShadow(BoxShadow.of(0.px, 1.px, 3.px, color = ThemeColors.cardShadow))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.px)
                        .backgroundColor(ThemeColors.primaryLight)
                        .borderRadius(24.px)
                        .margin(right = 16.px),
                    contentAlignment = Alignment.Center
                ) {
                    Span(attrs = Modifier.color(ThemeColors.primary).fontWeight(FontWeight.SemiBold).fontSize(18.px).toAttrs()) {
                        Text(member.firstName.firstOrNull()?.toString() ?: "M")
                    }
                }

                Column {
                    Span(attrs = Modifier.fontSize(16.px).fontWeight(FontWeight.SemiBold).color(ThemeColors.textPrimary).toAttrs()) {
                        Text("${member.firstName} ${member.lastName}")
                    }
                    Span(attrs = Modifier.fontSize(13.px).color(ThemeColors.textSecondary).toAttrs()) {
                        Text(member.email)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.gap(12.px)) {
                RoleBadge(member.role)
            }
        }
    }
}

@Composable
private fun RoleBadge(role: String) {
    val (bgColor, textColor) = when (role.lowercase()) {
        "owner" -> Pair(Color("#fef3c7"), Color("#92400e"))
        "manager" -> Pair(Color("#dbeafe"), Color("#1e40af"))
        "operator" -> Pair(Color("#e0e7ff"), Color("#4338ca"))
        else -> Pair(Color("#f3f4f6"), Color("#6b7280"))
    }

    Span(
        attrs = Modifier
            .backgroundColor(bgColor)
            .color(textColor)
            .padding(4.px, 10.px)
            .borderRadius(4.px)
            .fontSize(12.px)
            .fontWeight(FontWeight.Medium)
            .toAttrs()
    ) {
        Text(role.replaceFirstChar { it.uppercase() })
    }
}

