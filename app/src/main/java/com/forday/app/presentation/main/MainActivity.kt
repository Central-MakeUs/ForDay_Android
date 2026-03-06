package com.forday.app.presentation.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.splash.SplashViewModel
import java.security.MessageDigest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            onboardingViewModel.uiState.value.isSplashLoading ||
                onboardingViewModel.uiState.value.initialRoute == null ||
                splashViewModel.uiState.value.isLoading ||
                splashViewModel.uiState.value.effectiveRoute == null
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            ),
        )
        Log.e("@@@@@@@@@@@@KeyHash", "getReleaseKeyHash")
        getReleaseKeyHash()
        setContent {
            Log.e("@@@@@@@@@@@@KeyHash", "getReleaseKeyHash")
            AppEntryPoint(
                onboardingViewModel = onboardingViewModel,
                splashViewModel = splashViewModel
            )
        }
    }

    fun getReleaseKeyHash() {
        Log.e("@@@@@@@@@@@@KeyHash", "getReleaseKeyHash")
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = info.signingInfo?.apkContentsSigners

            if (signatures != null) {
                for (signature in signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                    Log.e("@@@@@@@@@@@@KeyHash", "현재 릴리스 키해시: $keyHash")
                }
            }
        } catch (e: Exception) {
            Log.e("@@@@@@@@@@@@KeyHash", "키해시를 가져올 수 없습니다.", e)
        }
    }
}