package com.forday.app.presentation.mypage.hobbyphotosetting

import com.forday.app.presentation.mypage.PresignedUrlUiModel
import com.forday.app.presentation.mypage.main.FeedContainerUiModel
import com.forday.app.presentation.mypage.main.UserHobbyTabUiModel
import com.forday.app.presentation.mypage.routinedetail.HobbyMainImageUiModel

data class HobbyPhotoUiState(
    val feed: HobbyFeedState = HobbyFeedState(),
    val image: HobbyImageState = HobbyImageState(),
)

data class HobbyFeedState(
    val hobbyTabs: UserHobbyTabUiModel? = null,
    val feedContainer: FeedContainerUiModel? = null,
)

data class HobbyImageState(
    val uploadState: PresignedUrlUiModel = PresignedUrlUiModel(),
    val mainImage: HobbyMainImageUiModel = HobbyMainImageUiModel(),
)
