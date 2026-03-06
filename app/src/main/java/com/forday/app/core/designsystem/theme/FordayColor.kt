package com.forday.app.core.designsystem.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


object FordayColor {

    @Stable
    val Primary001 = Color(0xFFF4A261)

    @Stable
    val Primary02 = Color(0xFFFEFE6D1)

    @Stable
    val Primary03 = Color(0xFFFFF1E6)

    @Stable
    val Primary04 = Color(0xFFFFF1E6)

    @Stable
    val Yellow = Color(0xFFFFE57C)

    @Stable
    val Purple = Color(0xFFB5A7FF)

    @Stable
    val Orange01 = Color(0xFFEE9449)

    @Stable
    val Orange02 = Color(0xFFDB8843)

    @Stable
    val Orange03 = Color(0xFFEE9449)

    @Stable
    val Gray03 = Color(0xFFE5E5E5)

    @Stable
    val Red01 = Color(0xFFF77F78)

    @Stable
    val Red02 = Color(0xFFF8C8C0)

    @Stable
    val Secondary003 = Color(0xFFF25F59)

    @Stable
    val White = Color(0xFFFFFFFF)

    @Stable
    val Gray300 = Color(0xFFD0D0D0)

    @Stable
    val Gray400 = Color(0xFFB3B3B3)

    @Stable
    val Gray500 = Color(0xFF9E9E9E)

    @Stable
    val Neutral600 = Color(0xFF7A7A7A)

    @Stable
    val Gray700 = Color(0xFF5A5A5A)

    @Stable
    val Gray800 = Color(0xFF3A3A3A)

    @Stable
    val Neutral900 = Color(0xFF1E1E1E)

    @Stable
    val Black = Color(0xFF000000)

    @Stable
    val Neutral50 = Color(0xFFF9F9F9)

    @Stable
    val MediumGray = Color(0xFFF2F2F2)

    @Stable
    val Peach = Color(0xFFFFF5EE)

    @Stable
    val Border = Color(0xFFD1D1D1)

    @Stable
    val StrongDivider = Color(0xFFB5B5B5)

    @Stable
    val PeachLight = Color(0xFFFEE5CE)

    @Stable
    val PeachSoft = Color(0xFFF8C8C0)

    @Stable
    val CoralLight = Color(0xFFFFC4BC)

    @Stable
    val KakaoYellow = Color(0xFFFFDE00)

    @Stable
    val PrimaryBlue = Color(0xFF5F8CF0)

    @Stable
    val FocusBlue = Color(0xFF567CEA)
}

internal val LocalAppColor = staticCompositionLocalOf<FordayColor> {
    error("No AppColor provided")
}