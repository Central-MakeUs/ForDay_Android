package com.forday.app.presentation.inputhobbyroutines.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class InputRoutine(val hobbyId: Long? = null, val aiCallRemaining: Boolean? = null, val hobbyName: String? = null) : NavKey  //루틴 직접 입력 화면