package com.forday.app.presentation.onboarding.splash

import androidx.navigation3.runtime.NavKey
import com.forday.app.domain.model.AppUpdateType

data class SplashUiState(
    val isLoading: Boolean = false,
    val updateType: AppUpdateType = AppUpdateType.NONE,
    val storeUrl: String = "",
    val message: String = "",
    val realRoute: NavKey? = null,
    val effectiveRoute: NavKey? = null,
)
