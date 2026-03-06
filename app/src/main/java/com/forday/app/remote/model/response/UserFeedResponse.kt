package com.forday.app.remote.model.response

import com.forday.app.data.model.FeedItemEntity
import com.forday.app.data.model.UserFeedEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserFeedResponse(
    @SerialName("status") val status: Int,
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: UserFeedDataResponse
)

@Serializable
data class UserFeedDataResponse(
    @SerialName("totalFeedCount") val totalFeedCount: Int,
    @SerialName("lastRecordId") val lastRecordId: Int,
    @SerialName("feedList") val feedList: List<FeedDto>
)

@Serializable
data class FeedDto(
    @SerialName("recordId") val recordId: Int?,
    @SerialName("thumbnailImageUrl") val thumbnailImageUrl: String?,
    @SerialName("sticker") val sticker: String?,
    @SerialName("memo") val memo: String?,
    @SerialName("createdAt") val createdAt: String?
)

/**
 * Remote Mapper: Remote -> Data Entity
 */
fun UserFeedResponse.toData() = UserFeedEntity(
    totalFeedCount = data.totalFeedCount,
    lastRecordId = data.lastRecordId,
    feedList = data.feedList.map { it.toData() }
)

fun FeedDto.toData() = FeedItemEntity(
    recordId = recordId ?: 0,
    imageUrl = thumbnailImageUrl ?: "",
    stickerType = sticker ?: "",
    memo = memo ?: "",
    date = createdAt ?: ""
)