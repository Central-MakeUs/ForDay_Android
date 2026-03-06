package com.forday.app.presentation.record.navigation

import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel
import kotlinx.serialization.Serializable

@Serializable
data class RecordRoutine(val hobbyId: Long? = null, val modifyData: RoutineRecordDetailUiModel? = null, val modifyMode: Boolean = false, val shouldResetToMyPage: Boolean = true, val entryPoint: String = "", val hobbyName: String? = null, val activityName: String? = null) : NavKey