package com.forday.app.core.logger.di

import android.content.Context
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.logger.analytics.AnalyticsManagerImpl
import com.forday.app.core.logger.crashlytics.CrashlyticsManager
import com.forday.app.core.logger.crashlytics.CrashlyticsManagerImpl
import com.forday.app.core.logger.timber.CrashlyticsTree
import com.forday.app.core.logger.timber.DebugLogTree
import com.forday.app.core.logger.timber.TimberInitializer
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

//    @Provides
//    @Singleton
//    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
//        return FirebaseCrashlytics.getInstance()
//    }

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(
        @ApplicationContext context: Context
    ): FirebaseCrashlytics {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    @Singleton
    fun provideCrashlyticsManager(
        firebaseCrashlytics: FirebaseCrashlytics
    ): CrashlyticsManager {
        return CrashlyticsManagerImpl(firebaseCrashlytics)
    }

    @Provides
    @Singleton
    fun provideCrashlyticsTree(
        crashlyticsManager: CrashlyticsManager
    ): CrashlyticsTree {
        return CrashlyticsTree(crashlyticsManager)
    }

    @Provides
    @Singleton
    fun provideTimberInitializer(
        crashlyticsTree: CrashlyticsTree
    ): TimberInitializer {
        val trees = mutableListOf<Timber.Tree>()

        // Logcat 출력 (Debug/Release 공통)
        trees.add(DebugLogTree())

        // Crashlytics 전송 (WARN/ERROR만)
        trees.add(crashlyticsTree)

        return TimberInitializer(*trees.toTypedArray())
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(
        firebaseAnalytics: FirebaseAnalytics
    ): AnalyticsManager {
        return AnalyticsManagerImpl(firebaseAnalytics)
    }
}