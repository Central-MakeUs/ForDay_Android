package com.forday.app.data.model

import com.forday.app.domain.model.FeedItem
import com.forday.app.domain.model.UserFeedDomain

data class UserFeedEntity(
    val totalFeedCount: Int,
    val lastRecordId: Int,
    val feedList: List<FeedItemEntity>
)

data class FeedItemEntity(
    val recordId: Int,
    val imageUrl: String,
    val stickerType: String,
    val memo: String,
    val date: String
)

/**
 * Data Mapper: Data Entity -> Domain Model
 */
fun UserFeedEntity.toDomain() = UserFeedDomain(
    totalFeedCount = this@toDomain.totalFeedCount,
    lastRecordId = lastRecordId,
    feeds = feedList.map { it.toDomain() }
)

fun FeedItemEntity.toDomain() = FeedItem(
    id = recordId,
    imageUrl = imageUrl,
    sticker = stickerType, // String을 Enum으로 변환
    memo = memo,
    createdAt = date // 필요 시 여기서 ISO 날짜 파싱 수행
)