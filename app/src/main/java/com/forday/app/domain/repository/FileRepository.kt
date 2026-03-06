package com.forday.app.domain.repository

import com.forday.app.domain.model.DeleteS3ImageDomain
import com.forday.app.domain.model.PresignedUrlDomain

interface FileRepository {

    suspend fun getPresignedUrl(images: List<Map<String, Any>>): PresignedUrlDomain
    suspend fun deleteS3Image(imageUrl: String): DeleteS3ImageDomain
}