package com.forday.app.presentation.onboarding

import androidx.navigation3.runtime.NavKey
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.util.ImageCodeMapper.getDrawableResId
import com.forday.app.domain.model.HobbyCardAgainDomain
import com.forday.app.domain.model.HobbyCardDomain
import com.forday.app.domain.model.HobbyDomain
import com.forday.app.domain.model.HobbyItemDomain
import com.forday.app.domain.model.IsNicknameDuplicateDomain
import com.forday.app.presentation.onboarding.hobbyselect.Hobby
import com.forday.app.presentation.onboarding.periodselect.JourneyMode

data class OnboardingUiState(
    val hobbies: List<Hobby> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
    val appVersion: String = "",
    val isNewUser: Boolean? = null,
    val isNicknameSet: Boolean? = null,
    val isOnboardingCompleted: Boolean? = null,
    val hobbyId: Int? = null,
    val accessToken: String? = null,
    val selectedHobbyId: Long? = null,// ✅ 최종 선택된 취미 아이디
    val customHobbyText: String = "",
    val showDialog: Boolean = false,
    val selectedHobbyName: String? = "", // ✅ 최종 선택된 취미 이름
    val selectedMinutes: Int? = null,
    val customPurposeText: String = "",
    val selectedPurpose: String? = "",
    val selectedFrequency: Int? = null,
    val selectedJourneyMode: JourneyMode? = null,  //66일로 세팅할 경우 true
    val nicknameCheckMessage: String = "",
    val isNicknameAvailable: Boolean = false,
    val isNicknameChecked: Boolean = false, // 중복 확인 완료 여부
    val nicknameRegisterSuccess: Boolean = false,
    val isOnboardingDataSaved: Boolean = false, // 온보딩 데이터 서버에 저장됐는지 여부
    val isHobbyRecreated: Boolean = false,
    val initialRoute: NavKey? = null,
    val isSplashLoading: Boolean = true,
    val isLoginSuccess: Boolean = false, // 로그인 성공 플래그
    val errorData: ErrorDataUiState? = null,
    val hasSeenIntro: Boolean? = null,
    val socialType: String? = null,
)

fun HobbyCardDomain.toPresentation() =
    OnboardingUiState(hobbies = hobbies.map { it.toPresentation() }, appVersion = appVersion)

fun HobbyDomain.toPresentation() = Hobby(
    id = id,
    name = name,
    description = description,
    imageResId = getDrawableResId(imageCode),
)

fun HobbyCardAgainDomain.toPresentation() =
    OnboardingUiState(hobbies = hobbies.map { it.toPresentation() })

fun HobbyItemDomain.toPresentation() = Hobby(
    id = id,
    name = name,
    description = description,
    imageResId = getDrawableResId(imageCode),
)

fun IsNicknameDuplicateDomain.toPresentation() =
    OnboardingUiState(
        selectedHobbyName = data.nickname,
        nicknameCheckMessage = data.message,
        isNicknameChecked = data.available
    )