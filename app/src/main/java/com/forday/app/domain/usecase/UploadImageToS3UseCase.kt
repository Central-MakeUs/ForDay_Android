package com.forday.app.domain.usecase

import com.forday.app.domain.repository.S3UploadRepository
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class UploadImageToS3UseCase @Inject constructor(
    private val s3Repository: S3UploadRepository
) {
    suspend operator fun invoke(
        file: File,
        uploadUrl: String,
        contentType: String
    ): Boolean {
        return s3Repository.uploadImageToS3(
            file = file,
            uploadUrl = uploadUrl,
            contentType = contentType
        ).getOrThrow()
    }
}