package com.forday.app.data.impl

import com.forday.app.domain.repository.S3UploadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class S3UploadRepositoryImpl @Inject constructor(): S3UploadRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun uploadImageToS3(
        file: File,
        uploadUrl: String,
        contentType: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Timber.e("Starting S3 upload: ${file.name} to $uploadUrl")

            val requestBody = file.asRequestBody(contentType.toMediaType())

            val request = Request.Builder()
                .url(uploadUrl)
                .put(requestBody)
                .header("Content-Type", contentType)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Timber.e("S3 upload successful: ${file.name}")
                Result.success(true)
            } else {
                Timber.e("S3 upload failed: ${response.code} - ${response.message}")
                Result.failure(Exception("Upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "S3 upload exception")
            Result.failure(e)
        }
    }
}