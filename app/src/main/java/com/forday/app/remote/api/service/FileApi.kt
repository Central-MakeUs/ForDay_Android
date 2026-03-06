package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.DeleteS3ImageRequest
import com.forday.app.remote.model.request.PresignedUrlRequest
import com.forday.app.remote.model.response.DeleteS3ImageResponse
import com.forday.app.remote.model.response.PresignedUrlResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface FileApi {

    @POST("/app/presign")
    suspend fun getPresignedUrl(
        @Body body: PresignedUrlRequest
    ): PresignedUrlResponse

//    @DELETE("/app/images/temp")  // S3상의 이미지 삭제
//    suspend fun deleteS3Image(
////        @Query("imageUrl") imageKey: String
//        @Body body: DeleteS3ImageRequest
//    ): DeleteS3ImageResponse

    @HTTP(method = "DELETE", path = "/app/images/temp", hasBody = true)
    suspend fun deleteS3Image(
        @Body body: DeleteS3ImageRequest
    ): DeleteS3ImageResponse

}