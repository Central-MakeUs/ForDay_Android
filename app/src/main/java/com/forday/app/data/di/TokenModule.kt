package com.forday.app.data.di

import com.forday.app.data.AuthTokenProvider
import com.forday.app.remote.api.interceptor.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenModule {
    @Binds
    abstract fun bindTokenProvider(tokenProvider: AuthTokenProvider): TokenProvider
}