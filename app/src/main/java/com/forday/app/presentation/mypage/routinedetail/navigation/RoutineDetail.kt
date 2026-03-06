package com.forday.app.presentation.mypage.routinedetail.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RoutineDetail(val routineId: Long, val isNewRecord: Boolean = false, val isUserPageEntry: Boolean = false) : NavKey