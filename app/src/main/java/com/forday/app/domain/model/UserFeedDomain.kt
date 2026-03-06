package com.forday.app.domain.model

data class UserFeedDomain(
    val totalFeedCount: Int,
    val lastRecordId: Int,
    val feeds: List<FeedItem>?
)

data class FeedItem(
    val id: Int,
    val imageUrl: String,
    val sticker: String,
    val memo: String,
    val createdAt: String
)