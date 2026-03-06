package com.forday.app

import android.app.Application
import com.dayn.forday.BuildConfig
import com.forday.app.core.logger.crashlytics.CrashlyticsManager
import com.forday.app.core.logger.timber.TimberInitializer
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import okhttp3.OkHttpClient
import okio.Path.Companion.toOkioPath
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FordayApplication : Application() {

    @Inject
    lateinit var timberInitializer: TimberInitializer

    @Inject
    lateinit var crashlyticsManager: CrashlyticsManager

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)

        timberInitializer.execute()
        crashlyticsManager.setUp()

        // ✅ Coil3 이미지 로더 초기화
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .apply { if (BuildConfig.DEBUG) logger(DebugLogger()) }
                .components {
                    // ✅ OkHttp 설정 (components 블록 안에서)
                    add(
                        OkHttpNetworkFetcherFactory(
                            callFactory = {
                                OkHttpClient.Builder()
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .writeTimeout(30, TimeUnit.SECONDS)
                                    .build()
                            }
                        )
                    )
                }
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, 0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        // ✅ File을 Okio Path로 변환
                        .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                        .maxSizeBytes(50 * 1024 * 1024) // 50MB
                        .build()
                }
                .build()
        }

    }
}