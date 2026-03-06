package com.forday.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dayn.forday.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_std_variable, FontWeight.Normal),
    Font(R.font.pretendard_std_variable, FontWeight.Medium),
    Font(R.font.pretendard_std_variable, FontWeight.SemiBold),
    Font(R.font.pretendard_std_variable, FontWeight.Bold),
)

@Immutable
data class FordayTypography(
    val title24: TextStyle,
    val title22: TextStyle,
    val title20: TextStyle,
    val title18: TextStyle,
    val title16: TextStyle,
    val title14: TextStyle,
    val title12: TextStyle,
    val body16: TextStyle,
    val body14: TextStyle,
    val body12: TextStyle,
    val label16: TextStyle,
    val label14: TextStyle,
    val label12: TextStyle,
    val label10: TextStyle,
)

val LocalTypography = FordayTypography(
    title24 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.8.sp
    ),
    title22 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 26.4.sp
    ),
    title20 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    title18 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 21.6.sp
    ),
    title16 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 19.2.sp
    ),
    title14 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.8.sp
    ),
    title12 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    body16 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.4.sp
    ),
    body14 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    body12 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.8.sp
    ),
    label16 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.4.sp
    ),
    label14 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    label12 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.8.sp
    ),
    label10 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)

internal val LocalAppTypography = staticCompositionLocalOf<FordayTypography> {
    error("No AppTypography provided")
}