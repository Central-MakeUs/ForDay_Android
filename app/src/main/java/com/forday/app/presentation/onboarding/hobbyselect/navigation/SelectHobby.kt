package com.forday.app.presentation.onboarding.hobbyselect.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SelectHobby : NavKey  //취미 선택 화면

@Serializable
data object SelectHobbyFromModify : NavKey  // 취미 수정 화면에서 취미 추가로 진입