package com.forday.app.remote.model.response

import com.forday.app.data.model.RoutineRecordDetailEntity
import com.forday.app.data.model.RoutineRecordDetailEntity.RoutineReactionEntity
import com.forday.app.data.model.RoutineRecordDetailEntity.RoutineUserReactionEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class RoutineRecordDetailResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: RoutineRecordDetailDataResponse?
) : RemoteMapper<RoutineRecordDetailEntity?> {
    override fun toData(): RoutineRecordDetailEntity? = data?.toData()
}

data class RoutineRecordDetailDataResponse(
    @SerializedName("hobbyId") val hobbyId: Int?,
    @SerializedName("hobbyName") val hobbyName: String?,
    @SerializedName("activityId") val activityId: Int?,
    @SerializedName("activityContent") val activityContent: String?,
    @SerializedName("activityRecordId") val activityRecordId: Int?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("sticker") val sticker: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("memo") val memo: String?,
    @SerializedName("recordOwner") val recordOwner: Boolean?,
    @SerializedName("scraped") val scraped: Boolean?, // ✅ 추가
    @SerializedName("userInfo") val userInfo: UserInfoResponse?, // ✅ 추가
    @SerializedName("visibility") val visibility: String?,
    @SerializedName("newReaction") val newReaction: RoutineNewReactionResponse?,
    @SerializedName("userReaction") val userReaction: RoutineUserReactionResponse?
) : RemoteMapper<RoutineRecordDetailEntity> {
    override fun toData(): RoutineRecordDetailEntity {
        return RoutineRecordDetailEntity(
            hobbyId = hobbyId ?: 0,
            hobbyName = hobbyName.orEmpty(),
            routineId = activityId ?: 0,
            routineContent = activityContent.orEmpty(),
            routineRecordId = activityRecordId ?: 0,
            imageUrl = imageUrl.orEmpty(),
            sticker = sticker.orEmpty(),
            createdAt = createdAt.orEmpty(),
            memo = memo.orEmpty(),
            isOwner = recordOwner ?: false,
            isScraped = scraped ?: false, // ✅ 매핑
            userInfo = userInfo?.toData(), // ✅ 매핑
            visibility = visibility ?: "PRIVATE",
            newReaction = newReaction?.toData() ?: RoutineReactionEntity.EMPTY,
            userReaction = userReaction?.toData() ?: RoutineUserReactionEntity.EMPTY
        )
    }
}

data class UserInfoResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
) {
    fun toData() = RoutineRecordDetailEntity.UserInfoEntity(
        userId = userId.orEmpty(),
        nickname = nickname.orEmpty(),
        profileImageUrl = profileImageUrl.orEmpty()
    )
}

data class RoutineNewReactionResponse(
    @SerializedName("newAweSome") val awesome: Boolean?,
    @SerializedName("newGreat") val great: Boolean?,
    @SerializedName("newAmazing") val amazing: Boolean?,
    @SerializedName("newFighting") val fighting: Boolean?
) {
    fun toData() = RoutineReactionEntity(
        awesome = awesome ?: false,
        great = great ?: false,
        amazing = amazing ?: false,
        fighting = fighting ?: false
    )
}

data class RoutineUserReactionResponse(
    @SerializedName("pressedAweSome") val pressedAwesome: Boolean?,
    @SerializedName("pressedGreat") val pressedGreat: Boolean?,
    @SerializedName("pressedAmazing") val pressedAmazing: Boolean?,
    @SerializedName("pressedFighting") val pressedFighting: Boolean?
) {
    fun toData() = RoutineUserReactionEntity(
        pressedAwesome = pressedAwesome ?: false,
        pressedGreat = pressedGreat ?: false,
        pressedAmazing = pressedAmazing ?: false,
        pressedFighting = pressedFighting ?: false
    )
}