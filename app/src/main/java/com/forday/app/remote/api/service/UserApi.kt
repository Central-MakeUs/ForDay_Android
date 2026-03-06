package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.BlockUserRequest
import com.forday.app.remote.model.request.ReportUserRequest
import com.forday.app.remote.model.request.ProfileImageRequest
import com.forday.app.remote.model.request.RegisterNicknameRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.forday.app.remote.model.response.BlockUserResponse
import com.forday.app.remote.model.response.ReportUserResponse
import com.forday.app.remote.model.response.CancelAccountResponse
import com.forday.app.remote.model.response.IsNicknameDuplicateResponse
import com.forday.app.remote.model.response.LogoutResponse
import com.forday.app.remote.model.response.ProfileImageResponse
import com.forday.app.remote.model.response.ProfileResponse
import com.forday.app.remote.model.response.RegisterNicknameResponse
import com.forday.app.remote.model.response.SwitchAccountResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @GET("/users/nickname/availability")
    suspend fun checkNicknameDuplicate(
        @Query("nickname") nickname: String
    ): IsNicknameDuplicateResponse

    @PATCH("/users/nickname")
    suspend fun registerNickname(
        @Body nickname: RegisterNicknameRequest
    ): RegisterNicknameResponse

    @GET("/users/info")
    suspend fun getUserInfo(
        @Query("userId") userId: String?,
    ): ProfileResponse  // 사용자 정보 조회

    @PATCH("/users/profile-image")
    suspend fun setProfileImage(    // 사용자 프로필 이미지 설정
        @Body body: ProfileImageRequest
    ): ProfileImageResponse

    @PATCH("/auth/switch-account")
    suspend fun switchAccount(
        @Body request: SwitchAccountRequest
    ): SwitchAccountResponse

    @DELETE("/auth/logout")
    suspend fun logout(
    ): LogoutResponse

    @DELETE("/auth/withdraw")  // 회원 탈퇴
    suspend fun cancelAccount(): CancelAccountResponse

    @POST("/friends/block")  // 사용자 차단
    suspend fun blockUser(
        @Body body: BlockUserRequest
    ): BlockUserResponse

    @POST("/friends/report")  // 사용자 신고
    suspend fun reportUser(
        @Body body: ReportUserRequest
    ): ReportUserResponse


}