package com.forday.app.presentation.mypage.hobbyphotosetting

import java.io.File

sealed interface HobbyPhotoAction {
    // 데이터 로드
    data object LoadInitialData : HobbyPhotoAction
    data class LoadFeedList(
        val hobbyIds: List<Int?>,
        val lastRecordId: Long?,
        val feedSize: Long?
    ) : HobbyPhotoAction

    // 이미지 업로드
    data class GetPresignedUrl(val images: List<Map<String, Any>>) : HobbyPhotoAction
    data class UploadImageToS3(
        val file: File,
        val uploadUrl: String,
        val contentType: String,
        val order: Int
    ) : HobbyPhotoAction

    // 대표사진 설정
    data class SetHobbyMainImage(
        val hobbyId: Long?,
        val imageUrl: String? = null,
        val recordId: Long?
    ) : HobbyPhotoAction
}
