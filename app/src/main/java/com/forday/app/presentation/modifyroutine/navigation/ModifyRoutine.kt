package com.forday.app.presentation.modifyroutine.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class ModifyRoutine(val hobbyId: Long? = null, val hobbyName: String? = null) : NavKey // 취미 활동을 삭제 및 수정하는 화면