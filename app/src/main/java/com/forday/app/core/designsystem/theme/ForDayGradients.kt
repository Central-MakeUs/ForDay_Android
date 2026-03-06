package com.forday.app.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object ForDayGradients {

    val gradient001 = Brush.linearGradient(
        colorStops = arrayOf(
            0.021067f to Color(0xFFFFE6D1),
            1.0f to Color(0xFFF4A261)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val gradient002 = Brush.linearGradient(
        colorStops = arrayOf(
            0.028812f to Color(0xFFF4A261),
            1.0f to Color(0xFFF77F78)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val gradient003 = Brush.linearGradient(
        colorStops = arrayOf(
            0.028812f to Color(0xFFFFE6D1),
            1.0f to Color(0xFFF8C8C0)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val gradient004 = Brush.linearGradient(
        colorStops = arrayOf(
            0.028812f to Color(0xFFFFE6D1),
            1.0f to Color(0xFFF77F78)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )
}

internal val LocalAppGradients = staticCompositionLocalOf<ForDayGradients> {
    error("No AppGradients provided")
}