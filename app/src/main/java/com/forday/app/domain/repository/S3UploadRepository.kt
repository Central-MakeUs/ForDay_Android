package com.forday.app.domain.repository

import java.io.File

interface S3UploadRepository {
    suspend fun uploadImageToS3(
        file: File,
        uploadUrl: String,
        contentType: String
    ): Result<Boolean>
}