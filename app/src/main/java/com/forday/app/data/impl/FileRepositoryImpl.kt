package com.forday.app.data.impl

import com.forday.app.data.remote.FileDataSource
import com.forday.app.domain.model.DeleteS3ImageDomain
import com.forday.app.domain.model.PresignedUrlDomain
import com.forday.app.domain.repository.FileRepository
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileDataSource: FileDataSource
): FileRepository {
    override suspend fun getPresignedUrl(images: List<Map<String, Any>>): PresignedUrlDomain =
        fileDataSource.getPresignedUrl(images).toDomain()

    override suspend fun deleteS3Image(imageUrl: String): DeleteS3ImageDomain =
        fileDataSource.deleteS3Image(imageUrl).toDomain()

}