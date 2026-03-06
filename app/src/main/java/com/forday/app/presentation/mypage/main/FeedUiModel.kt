package com.forday.app.presentation.mypage.main

import com.dayn.forday.R
import com.forday.app.domain.model.FeedItem
import com.forday.app.domain.model.UserFeedDomain


data class FeedContainerUiModel(
    val totalFeedCount: Int,
    val lastRecordId: Int,
    val feedList: List<FeedUiModel>,
    val hasMore: Boolean = true
)

data class FeedUiModel(
    val recordId: Int,
    val url: String,
    val stickerIconRes: Int, // 스티커 종류에 따른 로컬 리소스 ID
    val memo: String,
    val formattedDate: String // "2026.01.25" 등 가공된 문자열
)


fun UserFeedDomain.toPresentation() = FeedContainerUiModel(
    totalFeedCount = totalFeedCount,
    lastRecordId = lastRecordId,
    feedList = feeds?.map { it.toPresentation() } ?: emptyList()
)

/**
 * Domain -> UI Mapper (보통 ViewModel에서 수행)
 */
fun FeedItem.toPresentation() = FeedUiModel(
    recordId = id,
    url = imageUrl,
    stickerIconRes = when (sticker) {
        "smile.jpg" -> R.drawable.ic_sticker_smile
        "laugh.jpg" -> R.drawable.ic_sticker_laugh
        "sad.jpg" -> R.drawable.ic_sticker_sad
        "angry.jpg" -> R.drawable.ic_sticker_angry
        else -> R.drawable.ic_sticker_smile
    },
    memo = memo,
    formattedDate = createdAt.split("T")[0].replace("-", ".") // "2026.01.25"
)

// 더미 데이터 생성 함수
//fun createDummyFeedData(): FeedContainerUiModel {
//    val dummyFeedList = listOf(
//        FeedUiModel(
//            recordId = 1,
//            url = "https://picsum.photos/400/480?random=1",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.27"
//        ),
//        FeedUiModel(
//            recordId = 2,
//            url = "https://picsum.photos/400/480?random=2",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.26"
//        ),
//        FeedUiModel(
//            recordId = 3,
//            url = "https://picsum.photos/400/480?random=3",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.25"
//        ),
//        FeedUiModel(
//            recordId = 4,
//            url = "https://picsum.photos/400/480?random=4",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.24"
//        ),
//        FeedUiModel(
//            recordId = 5,
//            url = "https://picsum.photos/400/480?random=5",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.23"
//        ),
//        FeedUiModel(
//            recordId = 6,
//            url = "https://picsum.photos/400/480?random=6",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.22"
//        ),
//        FeedUiModel(
//            recordId = 7,
//            url = "https://picsum.photos/400/480?random=7",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.21"
//        ),
//        FeedUiModel(
//            recordId = 8,
//            url = "https://picsum.photos/400/480?random=8",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.20"
//        ),
//        FeedUiModel(
//            recordId = 9,
//            url = "https://picsum.photos/400/480?random=9",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.19"
//        ),
//        FeedUiModel(
//            recordId = 10,
//            url = "https://picsum.photos/400/480?random=10",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.18"
//        ),
//        FeedUiModel(
//            recordId = 11,
//            url = "https://picsum.photos/400/480?random=11",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.17"
//        ),
//        FeedUiModel(
//            recordId = 12,
//            url = "https://picsum.photos/400/480?random=12",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.16"
//        ),
//        FeedUiModel(
//            recordId = 13,
//            url = "https://picsum.photos/400/480?random=13",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.15"
//        ),
//        FeedUiModel(
//            recordId = 14,
//            url = "https://picsum.photos/400/480?random=14",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.14"
//        ),
//        FeedUiModel(
//            recordId = 15,
//            url = "https://picsum.photos/400/480?random=15",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.13"
//        ),
//        FeedUiModel(
//            recordId = 16,
//            url = "https://picsum.photos/400/480?random=16",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.12"
//        ),
//        FeedUiModel(
//            recordId = 17,
//            url = "https://picsum.photos/400/480?random=17",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.11"
//        ),
//        FeedUiModel(
//            recordId = 18,
//            url = "https://picsum.photos/400/480?random=18",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.10"
//        ),
//        FeedUiModel(
//            recordId = 19,
//            url = "https://picsum.photos/400/480?random=19",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.09"
//        ),
//        FeedUiModel(
//            recordId = 20,
//            url = "https://picsum.photos/400/480?random=20",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.08"
//        ),
//        FeedUiModel(
//            recordId = 21,
//            url = "https://picsum.photos/400/480?random=21",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.07"
//        ),
//        FeedUiModel(
//            recordId = 22,
//            url = "https://picsum.photos/400/480?random=22",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.06"
//        ),
//        FeedUiModel(
//            recordId = 23,
//            url = "https://picsum.photos/400/480?random=23",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.05"
//        ),
//        FeedUiModel(
//            recordId = 24,
//            url = "https://picsum.photos/400/480?random=24",
//            stickerIconRes = R.drawable.ic_profile_empty,
//            formattedDate = "2026.01.04"
//        )
//    )
//
//    return FeedContainerUiModel(
//        totalFeedCount = dummyFeedList.size,
//        lastRecordId = dummyFeedList.lastOrNull()?.recordId ?: 0,
//        feedList = dummyFeedList
//    )
//}