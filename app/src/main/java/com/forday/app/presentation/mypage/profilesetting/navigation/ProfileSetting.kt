package com.forday.app.presentation.mypage.profilesetting.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class ProfileSetting(
    val profileImageUrl: String? = null,
    val currentNickname: String? = null,
) : NavKey