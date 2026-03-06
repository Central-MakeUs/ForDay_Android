package com.forday.app.core.session

import com.dayn.forday.BuildConfig
import com.forday.app.domain.model.AppUpdateType
import com.forday.app.domain.usecase.GetAppVersionPolicyUseCase
import com.forday.app.presentation.common.SnackbarManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

data class VersionPolicyState(
    val isChecked: Boolean = false,
    val updateType: AppUpdateType = AppUpdateType.NONE,
    val storeUrl: String = "",
    val message: String = "",
)

@Singleton
class VersionPolicyProvider @Inject constructor(
    private val getAppVersionPolicyUseCase: GetAppVersionPolicyUseCase,
    private val snackbarManager: SnackbarManager,
) {
    private val _state = MutableStateFlow(VersionPolicyState())
    val state: StateFlow<VersionPolicyState> = _state.asStateFlow()

    suspend fun checkVersionPolicy() {
        try {
            val result = getAppVersionPolicyUseCase(
                platform = "ANDROID",
                appVersion = BuildConfig.VERSION_NAME,
                build = BuildConfig.VERSION_CODE
            )
            val data = result.data
            _state.update {
                it.copy(
                    isChecked = true,
                    updateType = data?.updateType ?: AppUpdateType.NONE,
                    storeUrl = data?.storeUrl ?: "",
                    message = data?.message ?: "",
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "checkVersionPolicy failed")
            _state.update { it.copy(isChecked = true) }
        }
    }

    fun dismissRecommendUpdate() {
        _state.update { it.copy(updateType = AppUpdateType.NONE) }
    }
}
