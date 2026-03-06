package com.forday.app.domain.model


data class ScrapListDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: ScrapListDataDomain
)

data class ScrapListDataDomain(
    val totalScrapCount: Int,
    val lastScrapId: Int,
    val scrapList: List<ScrapItemDomain>,
    val hasNext: Boolean
)

data class ScrapItemDomain(
    val scrapId: Int,
    val recordId: Int,
    val thumbnailImageUrl: String,
    val sticker: String,
    val memo: String,
    val createdAt: String
)