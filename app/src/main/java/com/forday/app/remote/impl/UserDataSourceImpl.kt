package com.forday.app.remote.impl

import com.forday.app.data.model.BlockUserEntity
import com.forday.app.data.model.IsNicknameDuplicateEntity
import com.forday.app.data.model.ProfileEntity
import com.forday.app.data.model.ProfileImageEntity
import com.forday.app.data.model.RegisterNicknameEntity
import com.forday.app.data.model.ReportUserEntity
import com.forday.app.data.model.UserDataEntity
import com.forday.app.data.remote.UserDataSource
import com.forday.app.remote.api.service.UserApi
import com.forday.app.remote.model.request.BlockUserRequest
import com.forday.app.remote.model.request.ProfileImageRequest
import com.forday.app.remote.model.request.RegisterNicknameRequest
import com.forday.app.remote.model.request.ReportUserRequest
import com.forday.app.remote.model.response.IsNicknameDuplicateResponse
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val userApi: UserApi
) : UserDataSource {

    override suspend fun checkNicknameDuplicate(nickname: String): IsNicknameDuplicateEntity =
        userApi.checkNicknameDuplicate(nickname).toData()

    //    override suspend fun registerNickname(nickname: String): RegisterNicknameEntity {
//        return userApi.registerNickname(RegisterNicknameRequest(nickname)).toData()
//    }
    override suspend fun registerNickname(nickname: String?): RegisterNicknameEntity {
        return try {
            // 1. API 호출
            val response = userApi.registerNickname(RegisterNicknameRequest(nickname))

            // 2. 데이터 변환
            val entity = response.toData()

            // ✅ 성공 로그: 입력한 닉네임과 서버에서 반환된 객체 정보 출력
            Timber.d("닉네임 등록 성공: 요청=$nickname, 응답=$entity")

            entity
        } catch (e: HttpException) {
            // HTTP 에러 처리 (403 등)
            val errorBody = e.response()?.errorBody()?.string()
            Timber.e(e, "닉네임 등록 HTTP 에러: 코드 ${e.code()}, 바디: $errorBody")

            throw e
        } catch (e: Exception) {
            // 일반 에러 처리
            Timber.e(e, "닉네임 등록 중 예외 발생: nickname=$nickname")

            throw e
        }
    }

    override suspend fun getUserInfo(userId: String?): ProfileEntity =
        userApi.getUserInfo(userId).toData()

    override suspend fun setProfileImage(imageUrl: String?): ProfileImageEntity =
        userApi.setProfileImage(ProfileImageRequest(imageUrl)).toData()

    override suspend fun blockUser(userId: String): BlockUserEntity =
        userApi.blockUser(BlockUserRequest(userId)).toData()

    override suspend fun reportUser(userId: String, reason: String): ReportUserEntity =
        userApi.reportUser(ReportUserRequest(userId, reason)).toData()

}