package com.forday.app.domain.model

data class RoutineRecordDetailDomain(
    val hobbyId: Int,
    val hobbyName: String,
    val routineId: Int,
    val content: String,
    val recordId: Int,
    val image: String,
    val sticker: String,
    val date: String,
    val isScraped: Boolean,
    val writerId: String,
    val writerNickname: String,
    val writerProfileImageUrl: String,
    val memo: String,
    val isMine: Boolean,
    val isVisible: Boolean,
    val newReaction: RoutineReactionDomain,
    val userReaction: RoutineUserReactionDomain
)

data class RoutineReactionDomain(
    val awesome: Boolean,
    val great: Boolean,
    val amazing: Boolean,
    val fighting: Boolean
)

data class RoutineUserReactionDomain(
    val pressedAwesome: Boolean,
    val pressedGreat: Boolean,
    val pressedAmazing: Boolean,
    val pressedFighting: Boolean
)