package com.forday.app.presentation.onboarding.periodselect.navigation

import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import kotlinx.serialization.Serializable

@Serializable
data class SelectPeriod(val params: HobbyModifyParams? = null, val mode: ScreenMode) : NavKey // 목표일 선택