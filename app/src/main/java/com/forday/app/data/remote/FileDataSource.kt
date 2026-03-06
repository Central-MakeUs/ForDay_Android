package com.forday.app.data.remote

import com.forday.app.data.model.DeleteS3ImageEntity
import com.forday.app.data.model.PresignedUrlEntity

interface FileDataSource {

    suspend fun getPresignedUrl(images: List<Map<String, Any>>): PresignedUrlEntity
    suspend fun deleteS3Image(imageUrl: String): DeleteS3ImageEntity
}