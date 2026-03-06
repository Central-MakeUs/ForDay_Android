package com.forday.app.presentation.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.dayn.forday.R
import com.forday.app.core.designsystem.dialog.AppVersionPolicyDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.domain.model.AppUpdateType

@Composable
internal fun ForceUpdateScreen(message: String, storeUrl: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    BackHandler(enabled = true) { activity?.finish() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_forday),
            contentDescription = "포데이 로고",
            contentScale = ContentScale.Fit,
        )
    }

    AppVersionPolicyDialog(
        updateType = AppUpdateType.FORCE,
        message = message,
        onUpdate = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
            context.startActivity(intent)
        },
        onDismiss = { activity?.finish() },
    )
}
