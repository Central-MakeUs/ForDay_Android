package com.forday.app.presentation.onboarding.frequencyselect.navigation

import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import kotlinx.serialization.Serializable

@Serializable
data class SelectPerWeek(val params: HobbyModifyParams? = null, val mode: ScreenMode) : NavKey  // 주당 실행횟수