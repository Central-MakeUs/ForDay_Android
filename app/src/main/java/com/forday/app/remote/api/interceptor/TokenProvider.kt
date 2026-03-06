package com.forday.app.remote.api.interceptor

interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    suspend fun setAccessToken(accessToken: String)
    suspend fun setRefreshToken(refreshToken: String)
}