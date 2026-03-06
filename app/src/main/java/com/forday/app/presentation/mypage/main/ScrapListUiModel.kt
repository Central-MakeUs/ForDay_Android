package com.forday.app.presentation.mypage.main

import com.dayn.forday.R
import com.forday.app.domain.model.ScrapItemDomain
import com.forday.app.domain.model.ScrapListDataDomain
import kotlin.collections.map
import kotlin.text.split

data class ScrapListUiModel(
    val totalCount: Int = 0,
    val items: List<ScrapItemUiModel> = emptyList(),
    val hasNextPage: Boolean = false,
    val lastId: Int = 0
)

data class ScrapItemUiModel(
    val scrapId: Int = 0,
    val recordId: Int = 0,
    val imageUrl: String = "",
    val stickerType: Int? = null,
    val memoPreview: String = "",
    val dateDisplay: String = "" // UI에 맞게 포맷팅된 날짜
)

// Domain -> UI Mapper
fun ScrapListDataDomain.toPresentation(): ScrapListUiModel {
    return ScrapListUiModel(
        totalCount = totalScrapCount,
        items = scrapList.map { it.toUiModel() },
        hasNextPage = hasNext,
        lastId = lastScrapId
    )
}

fun ScrapItemDomain.toUiModel(): ScrapItemUiModel {
    return ScrapItemUiModel(
        scrapId = scrapId,
        recordId = recordId,
        imageUrl = thumbnailImageUrl,
        stickerType = when (sticker) {
            "smile.jpg" -> R.drawable.ic_sticker_smile
            "laugh.jpg" -> R.drawable.ic_sticker_laugh
            "sad.jpg" -> R.drawable.ic_sticker_sad
            "angry.jpg" -> R.drawable.ic_sticker_angry
            else -> R.drawable.ic_sticker_smile
        },
        memoPreview = memo,
        dateDisplay = createdAt.split("T")[0] // 단순 예시: 2026-01-30 형태로 변환
    )
}