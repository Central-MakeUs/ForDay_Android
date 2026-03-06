package com.forday.app.remote.api.interceptor

import android.util.Log
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.core.session.AuthEvent
import com.forday.app.core.session.AuthEventBus
import com.forday.app.remote.api.service.TokenApi
import com.forday.app.remote.model.request.RefreshRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val tokenApi: TokenApi,
    private val tokenProvider: TokenProvider,
    private val authEventBus: AuthEventBus,
    private val userLocalDataSource: UserLocalDataSource,
) : Interceptor {

    private val mutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.d("TokenInterceptor", "###########22######╔════════════════════════════════════════════════════════════")
        Log.d("TokenInterceptor", "###########22######║ REQUEST")
        Log.d("TokenInterceptor", "###########22######╠════════════════════════════════════════════════════════════")
        Log.d("TokenInterceptor", "###########22######║ URL: ${originalRequest.url}")
        Log.d("TokenInterceptor", "###########22######║ METHOD: ${originalRequest.method}")
        Log.d("TokenInterceptor", "###########22######║ HEADERS:")
        val accessToken = tokenProvider.getAccessToken()
        Log.d("TokenInterceptor", "@###@!@$@$@A#@# $accessToken")
        val authedRequest = originalRequest.newBuilder().apply {
            if (!accessToken.isNullOrBlank()) header("Authorization", "Bearer $accessToken")
        }.build()
        Log.d("TokenInterceptor", "###########22###### RefreshToken from provider: ${tokenProvider.getRefreshToken()}")
        val response = chain.proceed(authedRequest)
        Log.d("TokenInterceptor", "###########22######╔════════════════════════════════════════════════════════════")
        Log.d("TokenInterceptor", "###########22######║ RESPONSE"+accessToken)
        Log.d("TokenInterceptor", "###########22######╠════════════════════════════════════════════════════════════")
        Log.d("TokenInterceptor", "###########22######║ encodedPath: ${originalRequest.url.encodedPath}")
        Log.d("TokenInterceptor", "###########22######║ CODE: ${response.code}")
        Log.d("TokenInterceptor", "###########22######║ MESSAGE: ${response.message}")
        Log.d("TokenInterceptor", "###########22######║ HEADERS:")

        val responseBody = response.body
        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE)
        val buffer = source?.buffer
        val charset = responseBody?.contentType()?.charset(Charset.forName("UTF-8")) ?: Charset.forName("UTF-8")
        val bodyString = buffer?.clone()?.readString(charset) ?: ""

        Log.d("TokenInterceptor", "###########22######║ BODY: $bodyString")
        Log.d("TokenInterceptor", "###########22######╚════════════════════════════════════════════════════════════")
        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return response
        }

        val newAccess = runBlocking { refreshSafely(accessToken) }

        return if (newAccess.isNullOrBlank()) {
            response
        } else {
            response.close()
            val retried = originalRequest.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()
            chain.proceed(retried)
        }
    }

    private suspend fun refreshSafely(oldAccess: String?): String? {
        Log.d("TokenInterceptor", "1############################### "+oldAccess)
        return mutex.lockAndGet {
            val latest = tokenProvider.getAccessToken()
            Log.d("TokenInterceptor", "2############################### "+latest)

            if (!latest.isNullOrBlank() && latest != oldAccess) return@lockAndGet latest

            val refreshToken = tokenProvider.getRefreshToken()
            Log.d("TokenInterceptor", "2############################### "+refreshToken)
            val rt = refreshToken ?: return@lockAndGet null
            try {
                Log.d("TokenInterceptor", "4############################### "+rt)
                val res = tokenApi.refreshToken(RefreshRequest(rt))
                Log.d("TokenInterceptor", "4############################### "+res.status)
                tokenProvider.setAccessToken(res.data.accessToken)
                tokenProvider.setRefreshToken(res.data.refreshToken)
                res.data.accessToken
            } catch (e: Exception) {
                Log.d("TokenInterceptor", "===== refreshToken failed =====")
                Log.d("TokenInterceptor", "Exception type: ${e.javaClass.simpleName}")
                Log.d("TokenInterceptor", "Exception message: ${e.message}")

                // Retrofit/OkHttp 에러인 경우 상세 정보
                when (e) {
                    is retrofit2.HttpException -> {
                        Log.d("TokenInterceptor", "HTTP Status Code: ${e.code()}")
                        Log.d("TokenInterceptor", "HTTP Message: ${e.message()}")

                        // 응답 본문 읽기
                        try {
                            val errorBody = e.response()?.errorBody()?.string()
                            Log.d("TokenInterceptor", "Error Response Body: $errorBody")
                        } catch (bodyException: Exception) {
                            Log.d("TokenInterceptor", "Failed to read error body: ${bodyException.message}")
                        }
                    }
                    is java.io.IOException -> {
                        Log.d("TokenInterceptor", "Network error (IOException): ${e.message}")
                    }
                    is kotlinx.serialization.SerializationException -> {
                        Log.d("TokenInterceptor", "Serialization error: ${e.message}")
                    }
                    else -> {
                        Log.d("TokenInterceptor", "Unknown error type")
                    }
                }

                // 전체 스택 트레이스
                Log.d("TokenInterceptor", "Full stack trace:", e)

                runBlocking {
                    userLocalDataSource.clearTokensOnly()
                }
                authEventBus.tryEmit(AuthEvent.Expired)

                null
            }
        }
    }

    private suspend inline fun <T> Mutex.lockAndGet(block: () -> T): T {
        lock()
        return try { block() } finally { unlock() }
    }
}