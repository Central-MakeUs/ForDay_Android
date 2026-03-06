package com.forday.app.presentation.mypage

import com.forday.app.presentation.mypage.main.FeedContainerUiModel
import com.forday.app.presentation.mypage.main.ScrapListUiModel
import com.forday.app.presentation.mypage.main.UserHobbyTabUiModel
import com.forday.app.presentation.mypage.main.UserInfoUiModel

data class MyPageUiState(
    val profile: ProfileState = ProfileState(),
    val feed: FeedState = FeedState(),
    val scrap: ScrapState = ScrapState(),
    val auth: AuthState = AuthState(),
    val isRefreshing: Boolean = false,
    val hasShownGuestBottomSheet: Boolean = false,
    val blockUserSuccess: Boolean = false,
    val isBlockedUser: Boolean = false,
)

data class ProfileState(
    val userInfo: UserInfoUiModel? = null,
    val hobbyTabs: UserHobbyTabUiModel? = null,
)

data class FeedState(
    val container: FeedContainerUiModel? = null,
)

data class ScrapState(
    val list: ScrapListUiModel = ScrapListUiModel(),
    val count: Int? = 0,
)

data class AuthState(
    val socialType: String? = null,
    val isKakaoLoginSuccess: Boolean? = null,
)
