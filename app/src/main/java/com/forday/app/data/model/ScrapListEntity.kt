package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.*
import kotlin.collections.map

data class ScrapListEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ScrapListDataEntity
) : DataMapper<ScrapListDomain> {
    override fun toDomain(): ScrapListDomain = ScrapListDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class ScrapListDataEntity(
    val totalScrapCount: Int,
    val lastScrapId: Int,
    val scrapList: List<ScrapItemEntity>,
    val hasNext: Boolean
) : DataMapper<ScrapListDataDomain> {
    override fun toDomain(): ScrapListDataDomain = ScrapListDataDomain(
        totalScrapCount = totalScrapCount,
        lastScrapId = lastScrapId,
        scrapList = scrapList.map { it.toDomain() },
        hasNext = hasNext
    )
}

data class ScrapItemEntity(
    val scrapId: Int,
    val recordId: Int,
    val thumbnailImageUrl: String,
    val sticker: String,
    val memo: String,
    val createdAt: String
) : DataMapper<ScrapItemDomain> {
    override fun toDomain(): ScrapItemDomain = ScrapItemDomain(
        scrapId = scrapId,
        recordId = recordId,
        thumbnailImageUrl = thumbnailImageUrl,
        sticker = sticker,
        memo = memo,
        createdAt = createdAt
    )
}