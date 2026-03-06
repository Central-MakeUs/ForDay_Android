package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.RoutineRecordDetailDomain
import com.forday.app.domain.model.RoutineReactionDomain
import com.forday.app.domain.model.RoutineUserReactionDomain

data class RoutineRecordDetailEntity(
    val hobbyId: Int,
    val hobbyName: String,
    val routineId: Int,
    val routineContent: String,
    val routineRecordId: Int,
    val imageUrl: String,
    val sticker: String,
    val createdAt: String,
    val memo: String,
    val isOwner: Boolean,
    val isScraped: Boolean, // ✅ 추가
    val userInfo: UserInfoEntity?, // ✅ 추가
    val visibility: String,
    val newReaction: RoutineReactionEntity,
    val userReaction: RoutineUserReactionEntity
) : DataMapper<RoutineRecordDetailDomain> { // 반환 타입 변경

    override fun toDomain(): RoutineRecordDetailDomain {
        return RoutineRecordDetailDomain(
            hobbyId = hobbyId,
            hobbyName = hobbyName,
            routineId = this@RoutineRecordDetailEntity.routineId,
            content = routineContent,
            recordId = routineRecordId,
            image = imageUrl,
            sticker = sticker,
            date = createdAt,
            isScraped = isScraped,
            writerId = userInfo?.userId ?: "",
            writerNickname = userInfo?.nickname ?: "익명",
            writerProfileImageUrl = userInfo?.profileImageUrl ?: "",
            memo = memo,
            isMine = isOwner,
            isVisible = visibility == "PUBLIC",
            newReaction = newReaction.toDomain(),
            userReaction = userReaction.toDomain()
        )
    }

//    companion object {
//        val EMPTY = RoutineRecordDetailEntity(0, "", 0, "", "", "", "", false, "PRIVATE", RoutineReactionEntity.EMPTY, RoutineUserReactionEntity.EMPTY)
//    }
//}

    data class RoutineReactionEntity(
        val awesome: Boolean,
        val great: Boolean,
        val amazing: Boolean,
        val fighting: Boolean
    ) {
        fun toDomain() = RoutineReactionDomain(awesome, great, amazing, fighting)

        companion object {
            val EMPTY = RoutineReactionEntity(false, false, false, false)
        }
    }

    data class RoutineUserReactionEntity(
        val pressedAwesome: Boolean,
        val pressedGreat: Boolean,
        val pressedAmazing: Boolean,
        val pressedFighting: Boolean
    ) {
        fun toDomain() =
            RoutineUserReactionDomain(pressedAwesome, pressedGreat, pressedAmazing, pressedFighting)

        companion object {
            val EMPTY = RoutineUserReactionEntity(false, false, false, false)
        }
    }
    data class UserInfoEntity(val userId: String, val nickname: String, val profileImageUrl: String) // ✅ 추가
}