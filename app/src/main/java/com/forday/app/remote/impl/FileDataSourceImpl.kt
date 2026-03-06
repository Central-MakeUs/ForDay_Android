package com.forday.app.remote.impl

import com.forday.app.data.model.DeleteS3ImageEntity
import com.forday.app.data.model.PresignedUrlEntity
import com.forday.app.data.remote.FileDataSource
import com.forday.app.remote.api.service.FileApi
import com.forday.app.remote.model.request.DeleteS3ImageRequest
import com.forday.app.remote.model.request.ImageRequest
import com.forday.app.remote.model.request.PresignedUrlRequest
import javax.inject.Inject

class FileDataSourceImpl @Inject constructor(
    private val fileApi: FileApi
): FileDataSource {
    override suspend fun getPresignedUrl(images: List<Map<String, Any>>): PresignedUrlEntity =
        fileApi.getPresignedUrl(PresignedUrlRequest(
            images = images.map { map ->
                ImageRequest(
                    originalFilename = map["fileName"] as String,
                    contentType = map["contentType"] as String,
                    usage = map["usage"] as String,
                    order = map["order"] as Int
                )
            }
        )).toData()

    override suspend fun deleteS3Image(imageUrl: String): DeleteS3ImageEntity =   //todo 이미지 삭제  java.lang.IllegalArgumentException: Non-body HTTP method cannot contain @Body.
        fileApi.deleteS3Image(DeleteS3ImageRequest(imageUrl)).toData()

}