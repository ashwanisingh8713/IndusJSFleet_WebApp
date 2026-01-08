package com.indusjs.fleet.web.components.widgets

import androidx.compose.runtime.Composable
import com.indusjs.fleet.web.theme.ThemeColors
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.FaSpinner
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

// ===== Legacy Color Tokens (for backwards compatibility) =====
object Colors {
    val primary get() = ThemeColors.primary
    val primaryHover get() = ThemeColors.primaryDark
    val primaryLight get() = ThemeColors.primaryLight
    val success get() = ThemeColors.success
    val warning get() = ThemeColors.warning
    val error get() = ThemeColors.error
    val textPrimary get() = ThemeColors.textPrimary
    val textSecondary get() = ThemeColors.textSecondary
    val textMuted get() = ThemeColors.textMuted
    val border get() = ThemeColors.border
    val background get() = ThemeColors.background
    val white get() = org.jetbrains.compose.web.css.Color.white
}

@Composable
fun FormInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    type: InputType<String> = InputType.Text,
    placeholder: String = "",
    error: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().margin(bottom = 16.px)
    ) {
        Label(
            attrs = Modifier
                .fontSize(14.px)
                .fontWeight(FontWeight.Medium)
                .color(ThemeColors.textPrimary)
                .margin(bottom = 6.px)
                .toAttrs()
        ) {
            Text(label)
        }

        Input(type) {
            classes("form-input")
            placeholder(placeholder)
            value(value)
            onInput { onValueChange(it.value) }
            if (!enabled) disabled()
            style {
                width(100.percent)
                padding(12.px, 16.px)
                borderRadius(8.px)
                border(1.px, LineStyle.Solid, if (error != null) ThemeColors.error else ThemeColors.inputBorder)
                fontSize(15.px)
                backgroundColor(if (enabled) ThemeColors.inputBackground else ThemeColors.border)
                color(if (enabled) ThemeColors.textPrimary else ThemeColors.textMuted)
                property("outline", "none")
                if (!enabled) {
                    cursor("not-allowed")
                }
            }
        }

        if (error != null) {
            Span(
                attrs = Modifier
                    .fontSize(12.px)
                    .color(ThemeColors.error)
                    .margin(top = 4.px)
                    .toAttrs()
            ) {
                Text(error)
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    disabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        attrs = modifier
            .fillMaxWidth()
            .padding(14.px, 24.px)
            .backgroundColor(if (isLoading || disabled) ThemeColors.textMuted else ThemeColors.primary)
            .color(Color.white)
            .borderRadius(8.px)
            .border(0.px)
            .fontSize(15.px)
            .fontWeight(FontWeight.SemiBold)
            .cursor(if (isLoading || disabled) Cursor.NotAllowed else Cursor.Pointer)
            .onClick { if (!isLoading && !disabled) onClick() }
            .toAttrs {
                if (isLoading || disabled) disabled()
            }
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FaSpinner(
                    size = IconSize.SM,
                    modifier = Modifier.margin(right = 8.px)
                )
                Text("Loading...")
            }
        } else {
            Text(text)
        }
    }
}

@Composable
fun LinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        attrs = modifier
            .backgroundColor(Color.transparent)
            .color(ThemeColors.primary)
            .border(0.px)
            .fontSize(14.px)
            .fontWeight(FontWeight.Medium)
            .cursor(Cursor.Pointer)
            .textDecorationLine(TextDecorationLine.None)
            .padding(0.px)
            .onClick { onClick() }
            .toAttrs()
    ) {
        Text(text)
    }
}

// ===== Alert Component =====
@Composable
fun Alert(
    message: String,
    type: AlertType = AlertType.ERROR,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (type) {
        AlertType.SUCCESS -> Pair(ThemeColors.successBg, ThemeColors.success)
        AlertType.WARNING -> Pair(ThemeColors.warningBg, ThemeColors.warning)
        AlertType.ERROR -> Pair(ThemeColors.errorBg, ThemeColors.error)
        AlertType.INFO -> Pair(ThemeColors.infoBg, ThemeColors.info)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .backgroundColor(bgColor)
            .padding(12.px, 16.px)
            .borderRadius(8.px)
            .margin(bottom = 16.px)
    ) {
        Span(
            attrs = Modifier
                .fontSize(14.px)
                .color(textColor)
                .toAttrs()
        ) {
            Text(message)
        }
    }
}

enum class AlertType {
    SUCCESS, WARNING, ERROR, INFO
}

// ===== Logo Component =====
@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.margin(bottom = 32.px)
    ) {
        Box(
            modifier = Modifier
                .size(48.px)
                .backgroundColor(ThemeColors.primary)
                .borderRadius(12.px)
                .margin(right = 12.px),
            contentAlignment = Alignment.Center
        ) {
            Span(
                attrs = Modifier
                    .color(Color.white)
                    .fontSize(20.px)
                    .fontWeight(FontWeight.Bold)
                    .toAttrs()
            ) {
                Text("F")
            }
        }
        Column {
            Span(
                attrs = Modifier
                    .fontSize(20.px)
                    .fontWeight(FontWeight.Bold)
                    .color(ThemeColors.textPrimary)
                    .toAttrs()
            ) {
                Text("IndusJS Fleet")
            }
            Span(
                attrs = Modifier
                    .fontSize(12.px)
                    .color(ThemeColors.textSecondary)
                    .toAttrs()
            ) {
                Text("Fleet Management System")
            }
        }
    }
}

