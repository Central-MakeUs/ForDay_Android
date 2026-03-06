package com.forday.app.core.navigation

import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.TabDestination
import kotlinx.serialization.Serializable











@Serializable
data class LoadingRoutines(val hobbyId: Long? = null) : NavKey  //취미 분석 중 화면

@Serializable
data class RoutineAiRecommend(val hobbyId: Long? = null) : NavKey  //루틴 추천 결과 화면
@Serializable
data object MyPage : NavKey, TabDestination
