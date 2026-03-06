package com.forday.app.presentation.sosik.model

import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.domain.model.RoutineRecordDetailDomain
import com.forday.app.domain.model.SosikRecordDomain
import com.forday.app.domain.model.SosikTabInfoDomain

data class SosikUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorData: ErrorDataUiState? = null,
    val selectedTabIndex: Int = 0,
    val content: SosikContentUiState = SosikContentUiState(),
    val socialType: String? = null,
    val hasShownGuestBottomSheet: Boolean = false,
    val routineDetail: RoutineRecordDetailDomain? = null
)

data class SosikContentUiState(
    val tabList: List<SosikTabUiModel> = emptyList(),
    val recordList: List<SosikRecordUiModel> = emptyList(),
    val hasNext: Boolean = false,
    val lastRecordId: Long? = null
)

data class SosikTabUiModel(
    val hobbyId: Long,
    val hobbyName: String,
    val currentHobby: Boolean
)

data class SosikRecordUiModel(
    val recordId: Long,
    val imageUrl: String,
    val title: String,
    val nickname: String,
    val profileImageUrl: String,
    val pressedAweSome: Boolean,
    val sticker: String,
    val memo: String,
    val hobbyName: String,
    val userId: String,
    val recordAuthor: Boolean
)

fun SosikTabInfoDomain.toPresentation(): SosikTabUiModel = SosikTabUiModel(
    hobbyId = hobbyId,
    hobbyName = hobbyName,
    currentHobby = currentHobby
)

fun SosikRecordDomain.toPresentation(): SosikRecordUiModel = SosikRecordUiModel(
    recordId = recordId,
    imageUrl = thumbnailUrl,
    title = title,
    nickname = userInfo.nickname ?: "",
    profileImageUrl = userInfo.profileImageUrl ?: "",
    pressedAweSome = pressedAweSome,
    sticker = sticker,
    memo = memo,
    hobbyName = hobbyName,
    userId = userInfo.userId,
    recordAuthor = recordAuthor
)
