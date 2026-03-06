package com.forday.app.presentation.onboarding.timeselect.navigation

import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import kotlinx.serialization.Serializable

@Serializable
data class SelectPerTime(val params: HobbyModifyParams? = null, val mode: ScreenMode) : NavKey  //취미 시간 선택 화면