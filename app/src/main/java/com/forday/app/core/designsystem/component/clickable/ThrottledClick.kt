package com.forday.app.core.designsystem.component.clickable

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberThrottledClick(
    throttleMs: Long = 500L,
    onClick: () -> Unit,
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return {
        val now = SystemClock.uptimeMillis()
        if (now - lastClickTime > throttleMs) {
            lastClickTime = now
            onClick()
        }
    }
}
