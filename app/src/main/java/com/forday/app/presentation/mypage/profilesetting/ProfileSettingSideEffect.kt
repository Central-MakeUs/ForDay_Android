package com.forday.app.presentation.mypage.profilesetting

sealed interface ProfileSettingSideEffect {
    data object NicknameRegisterSuccess : ProfileSettingSideEffect
}
