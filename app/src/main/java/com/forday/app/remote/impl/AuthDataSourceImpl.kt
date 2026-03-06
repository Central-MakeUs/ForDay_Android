package com.forday.app.remote.impl

import android.util.Log
import com.forday.app.data.model.CancelAccountEntity
import com.forday.app.data.model.KakaoLoginEntity
import com.forday.app.data.model.LogoutEntity
import com.forday.app.data.model.SwitchAccountEntity
import com.forday.app.data.remote.AuthDataSource
import com.forday.app.remote.api.service.AuthApi
import com.forday.app.remote.api.service.UserApi
import com.forday.app.remote.model.request.GuestLoginRequest
import com.forday.app.remote.model.request.KakaoLoginRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.forday.app.remote.model.response.GuestLoginResponse
import com.forday.app.remote.model.response.KakaoLoginResponse
import com.forday.app.remote.model.response.LogoutResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi
) : AuthDataSource {

    private val tolerantGson: Gson = GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, JsonDeserializer { json: JsonElement, _, _ ->
            when {
                json.isJsonNull -> false
                json.isJsonPrimitive -> {
                    val p = json.asJsonPrimitive
                    when {
                        p.isBoolean -> p.asBoolean
                        p.isNumber -> p.asInt != 0
                        p.isString -> p.asString.equals("true", ignoreCase = true) || p.asString == "1"
                        else -> false
                    }
                }
                else -> false
            }
        })
        .create()

    override suspend fun kakaoLogin(kakaoAccessToken: KakaoLoginRequest): KakaoLoginEntity {
        return try {
            Log.e("AuthDataSource", "kakaoLogin: calling /auth/kakao")
            val rawResponse = authApi.kakaoLogin(kakaoAccessToken)
            val code = rawResponse.code()
            val errorBodyString = rawResponse.errorBody()?.string()
            val bodyString = rawResponse.body()?.string()

            Log.e("AuthDataSource", "kakaoLogin: httpCode=$code")

            if (!rawResponse.isSuccessful) {
                Log.e("AuthDataSource", "kakaoLogin: errorBody=$errorBodyString")
                throw HttpException(rawResponse)
            }

            if (bodyString.isNullOrBlank()) {
                Log.e("AuthDataSource", "kakaoLogin: empty body")
                throw IOException("Empty response body")
            }

            Log.e("AuthDataSource", "kakaoLogin: api call success, parsing body")
            val remoteResponse = tolerantGson.fromJson(bodyString, KakaoLoginResponse::class.java)
            Log.e("AuthDataSource", "kakaoLogin: parse success, mapping to entity")
            val entity = remoteResponse.toData()
            Timber.d("카카오 로그인 성공: $entity")
            entity
        } catch (e: HttpException) {
            // 4xx, 5xx 서버 응답 에러
            Log.e("AuthDataSource", "kakaoLogin: HttpException status=${e.code()}")
            Log.e("AuthDataSource", "kakaoLogin: errorBody=${e.response()?.errorBody()?.string()}")
            Timber.e(e, "카카오 API 서버 오류 발생 (Status: ${e.code()})")
            Timber.e("에러 내용: ${e.response()?.errorBody()?.string()}")
            // 필요 시 응답 바디 로그: Timber.e("에러 내용: ${e.response()?.errorBody()?.string()}")
            throw e
        } catch (e: IOException) {
            // 네트워크 연결 끊김, 타임아웃 등
            Log.e("AuthDataSource", "kakaoLogin: IOException", e)
            Timber.e(e, "네트워크 연결 오류 또는 타임아웃 발생")
            throw e
        } catch (e: Exception) {
            // 그 외 데이터 파싱 오류 등 예상치 못한 에러
            Log.e("AuthDataSource", "kakaoLogin: unexpected exception", e)
            Timber.e(e, "카카오 로그인 중 알 수 없는 예외 발생")
            throw e
        }
    }

    override suspend fun guestLogin(guestUserId: GuestLoginRequest): GuestLoginResponse =
        authApi.guestLogin(guestUserId)

    override suspend fun switchAccount(switchAccountRequest: SwitchAccountRequest): SwitchAccountEntity =
        userApi.switchAccount(switchAccountRequest).toData()

    override suspend fun logout(): LogoutEntity =  // TODO 로그아웃 안되는 오류ㅜ
        userApi.logout().toData()

    override suspend fun cancelAccount(): CancelAccountEntity =
        userApi.cancelAccount().toData()

}