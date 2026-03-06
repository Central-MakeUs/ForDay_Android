package com.forday.app.remote.di

import com.dayn.forday.BuildConfig
import com.forday.app.remote.api.interceptor.TokenInterceptor
import com.forday.app.remote.api.service.AppVersionPolicyApi
import com.forday.app.remote.api.service.AuthApi
import com.forday.app.remote.api.service.FileApi
import com.forday.app.remote.api.service.HobbyApi
import com.forday.app.remote.api.service.RoutineApi
import com.forday.app.remote.api.service.TokenApi
import com.forday.app.remote.api.service.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class TokenInterceptorHttpClient

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class NoHeaderHttpClient

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class TokenRetrofit

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class NoHeaderRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TYPE_JSON = "application/json"

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideFileApi(
        @TokenRetrofit retrofit: Retrofit
    ): FileApi {
        return retrofit.create(FileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppVersionPolicyApi(retrofit: Retrofit): AppVersionPolicyApi {
        return retrofit.create(AppVersionPolicyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(
        @TokenRetrofit retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHobbyApi(
        @TokenRetrofit retrofit: Retrofit
    ): HobbyApi {
        return retrofit.create(HobbyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRoutineApi(
        @TokenRetrofit retrofit: Retrofit
    ): RoutineApi {
        return retrofit.create(RoutineApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenApi(
        @NoHeaderRetrofit retrofit: Retrofit
    ): TokenApi = retrofit.create(TokenApi::class.java)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @TokenInterceptorHttpClient
    @Provides
    @Singleton
    fun provideTokenHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        tokenInterceptor: TokenInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @NoHeaderHttpClient
    @Provides
    @Singleton
    fun provideNoHeaderHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @TokenRetrofit
    @Provides
    @Singleton
    fun provideTokenRetrofit(
        @TokenInterceptorHttpClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @NoHeaderRetrofit
    @Provides
    @Singleton
    fun provideNoHeaderRetrofit(
        @NoHeaderHttpClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}