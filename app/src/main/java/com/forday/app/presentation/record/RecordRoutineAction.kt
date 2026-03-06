package com.forday.app.presentation.record

import java.io.File

sealed interface RecordRoutineAction {
    // 데이터 로드
    data class FetchRoutineList(val hobbyId: Long?, val size: Int? = null) : RecordRoutineAction
    data class LoadRecordDetail(val recordId: Int) : RecordRoutineAction

    // 활동 기록
    data class WriteRoutine(
        val routineId: Long,
        val sticker: String,
        val memo: String,
        val imageUrl: String,
        val visibility: String,
        val recordId: Int? = null
    ) : RecordRoutineAction

    // 활동 수정
    data class ModifyPosting(
        val recordId: Int,
        val routineId: Int,
        val sticker: String,
        val memo: String,
        val imageUrl: String,
        val visibility: String
    ) : RecordRoutineAction

    // 이미지 업로드
    data class GetPresignedUrl(val images: List<Map<String, Any>>) : RecordRoutineAction
    data class UploadImageToS3(
        val file: File,
        val uploadUrl: String,
        val contentType: String,
        val order: Int
    ) : RecordRoutineAction
    data class DeleteS3Image(val imageUrl: String) : RecordRoutineAction
}
