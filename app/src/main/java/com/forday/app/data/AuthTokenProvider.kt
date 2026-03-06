package com.forday.app.data

import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.remote.api.interceptor.TokenProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AuthTokenProvider @Inject constructor(
    private val userDataSource: UserLocalDataSource
) : TokenProvider {
    override fun getAccessToken(): String? = runBlocking {
        userDataSource.accessTokenFlow.firstOrNull()
    }

    override fun getRefreshToken(): String? = runBlocking {
        userDataSource.refreshTokenFlow.firstOrNull()
    }

    override suspend fun setAccessToken(accessToken: String) {
        userDataSource.saveAccessToken(accessToken)
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        userDataSource.saveRefreshToken(refreshToken)
    }
}