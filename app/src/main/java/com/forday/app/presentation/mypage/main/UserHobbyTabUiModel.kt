package com.forday.app.presentation.mypage.main

import com.forday.app.domain.model.HobbyTabDomain
import com.forday.app.domain.model.UserHobbyTabDomain

// 사용자 취미 진행 상단탭 조회 model
data class UserHobbyTabUiModel(
    val inProgressCount: Int?,
    val hobbyCardCount: Int?,
    val hobbyItems: List<HobbyUiModel>?
)

data class HobbyUiModel(
    val hobbyId: Int?,
    val hobbyName: String?,
    val thumbnail: String?,
    val status: String?,
)

/**
 * Domain -> UI Model Mapper
 */
fun UserHobbyTabDomain.toPresentation() = UserHobbyTabUiModel(
    inProgressCount = inProgressCount,
    hobbyCardCount = hobbyCardCount,
    hobbyItems = hobbies?.map { it.toPresentation() }
)

fun HobbyTabDomain.toPresentation() = HobbyUiModel(
    hobbyId = id,
    hobbyName = name,
    thumbnail = imageUrl,
    status = status,
)