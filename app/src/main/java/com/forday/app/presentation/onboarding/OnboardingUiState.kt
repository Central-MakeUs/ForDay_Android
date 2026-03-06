package com.forday.app.presentation.onboarding

import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.util.ImageCodeMapper.getDrawableResId
import com.forday.app.domain.model.HobbyCardAgainDomain
import com.forday.app.domain.model.HobbyCardDomain
import com.forday.app.domain.model.HobbyDomain
import com.forday.app.domain.model.HobbyItemDomain
import com.forday.app.presentation.onboarding.hobbyselect.Hobby
import com.forday.app.presentation.onboarding.periodselect.JourneyMode

data class OnboardingUiState(
    val hobbies: List<Hobby> = emptyList(),
    val isLoading: Boolean = false,
    val appVersion: String = "",
    val hobbyId: Int? = null,
    val selectedHobbyId: Long? = null,
    val customHobbyText: String = "",
    val showDialog: Boolean = false,
    val selectedHobbyName: String? = "",
    val selectedMinutes: Int? = null,
    val customPurposeText: String = "",
    val selectedPurpose: String? = "",
    val selectedFrequency: Int? = null,
    val selectedJourneyMode: JourneyMode? = null,
    val isOnboardingDataSaved: Boolean = false,
    val isHobbyRecreated: Boolean = false,
    val errorData: ErrorDataUiState? = null,
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
