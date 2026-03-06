package com.forday.app.presentation.main

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forday.app.core.designsystem.dialog.AppVersionPolicyDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.session.AuthManager
import com.forday.app.domain.model.AppUpdateType

@Composable
fun AppEntryPoint(authManager: AuthManager) {
    val effectiveRoute by authManager.effectiveRoute.collectAsStateWithLifecycle()
    val versionState by authManager.versionPolicy.state.collectAsStateWithLifecycle()

    ForDayTheme {
        when {
            // FORCE/BLOCK update - 앱 진입 차단
            versionState.updateType == AppUpdateType.FORCE ||
                versionState.updateType == AppUpdateType.BLOCK -> {
                ForceUpdateScreen(
                    message = versionState.message,
                    storeUrl = versionState.storeUrl,
                )
            }

            // 라우트 결정 완료
            effectiveRoute != null -> {
                val route = effectiveRoute!!

                // RECOMMEND update - MainFlow + 다이얼로그 오버레이
                if (versionState.updateType == AppUpdateType.RECOMMEND) {
                    val context = LocalContext.current
                    key(route) {
                        MainFlow(
                            initialRoute = route,
                            authManager = authManager,
                        )
                    }
                    AppVersionPolicyDialog(
                        updateType = versionState.updateType,
                        message = versionState.message,
                        onUpdate = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionState.storeUrl))
                            context.startActivity(intent)
                        },
                        onDismiss = { authManager.dismissUpdatePrompt() },
                    )
                } else {
                    // NONE - 정상 진입
                    key(route) {
                        MainFlow(
                            initialRoute = route,
                            authManager = authManager,
                        )
                    }
                }
            }
        }
    }
}
