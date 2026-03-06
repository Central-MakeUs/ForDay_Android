package com.forday.app.presentation.main

import androidx.activity.compose.BackHandler
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.forday.app.core.designsystem.dialog.RoutineOnlyOneHaveDialog
import com.forday.app.core.designsystem.toast.ErrorToast
import com.forday.app.core.session.AuthManager
import com.forday.app.core.session.AuthManagerEvent
import com.forday.app.presentation.allsettings.SettingsSideEffect
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.navigation.settingsGraph
import com.forday.app.presentation.common.AppSideEffect
import com.forday.app.presentation.common.SnackbarHostViewModel
import com.forday.app.presentation.discovery.navigation.discoveryGraph
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.home.navigation.homeGraph
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.navigation.myPageGraph
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.navigation.onboardingGraph
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.record.navigation.recordGraph
import com.forday.app.presentation.sosik.navigation.sosikGraph
import android.widget.Toast as AndroidToast
import timber.log.Timber

internal fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun MainFlow(initialRoute: NavKey, authManager: AuthManager) {

    val navigationState = rememberMainNavigationState(
        startRoute = initialRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys as Set<NavKey>
    )

    LaunchedEffect(Unit) {
        if (initialRoute is SelectPeriod) {
            val stack = navigationState.backStacks[initialRoute]
            if (stack != null && stack.size == 1) {
                stack.add(SelectHobby)
                stack.add(SelectPerTime(mode = ScreenMode.ONBOARDING))
                stack.add(SelectPurpose)
                stack.add(SelectPerWeek(mode = ScreenMode.ONBOARDING))
                stack.add(SelectPeriod(mode = ScreenMode.ONBOARDING))
                navigationState.notifyNavChanged()
            }
        }
    }

    val navigator = remember(navigationState) { Navigator(navigationState) }

    // MainFlow 스코프 ViewModel들
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val inputRoutinesAndAiRecommendViewModel: InputRoutinesAndAiRecommendViewModel = hiltViewModel()
    val myPageViewModel: MyPageViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val snackbarHostViewModel: SnackbarHostViewModel = hiltViewModel()

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var currentHobbyId by remember { mutableStateOf<Long?>(null) }
    var currentHobbyName by remember { mutableStateOf<String?>(null) }
    var currentActivityName by remember { mutableStateOf<String?>(null) }
    var isRecordedToday by remember { mutableStateOf(false) }
    var todayRecordId by remember { mutableStateOf<Int?>(null) }
    var showAlreadyRecordedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope {
            // GlobalEventBus: 스낵바 + 네비게이션 이벤트 통합 처리
            launch {
                snackbarHostViewModel.sideEffects.collect { effect ->
                    when (effect) {
                        is AppSideEffect.ShowSnackbar -> {
                            toastMessage = effect.message
                        }
                        is AppSideEffect.NavigateToLogin -> {
                            authManager.resetForNewSession()
                            navigator.resetTo(Login)
                        }
                    }
                }
            }

            // AuthManager 이벤트 (토큰 만료, 게스트 재로그인 등)
            launch {
                authManager.events.collect { event ->
                    when (event) {
                        is AuthManagerEvent.GuestReLoginSuccess -> Unit
                        is AuthManagerEvent.GuestReLoginFailed,
                        is AuthManagerEvent.SessionExpired -> {
                            snackbarHostViewModel.show("로그인이 만료되었어요. 다시 로그인해주세요.")
                            snackbarHostViewModel.navigateToLogin()
                        }
                    }
                }
            }

            // 설정 사이드이펙트 (로그아웃, 탈퇴)
            launch {
                settingsViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        is SettingsSideEffect.LoggedOut,
                        is SettingsSideEffect.AccountCancelled -> {
                            snackbarHostViewModel.navigateToLogin()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    val onRecordClick: () -> Unit = {
        if (isRecordedToday) {
            showAlreadyRecordedDialog = true
        } else {
            navigator.navigate(
                RecordRoutine(
                    currentHobbyId,
                    entryPoint = "gnb_record",
                    hobbyName = currentHobbyName,
                    activityName = currentActivityName
                )
            )
        }
    }

    val entryProvider = entryProvider<NavKey> {
        // 온보딩
        onboardingGraph(navigator, authManager, onboardingViewModel)

        // 탭 화면들
        homeGraph(
            navigator = navigator,
            navigationState = navigationState,
            onRecordClick = onRecordClick,
            onCurrentHobbyIdChanged = { currentHobbyId = it },
            onCurrentHobbyInfoChanged = { hobbyName, activityName ->
                currentHobbyName = hobbyName
                currentActivityName = activityName
            },
            onRecordStateChanged = { recorded, recordId ->
                isRecordedToday = recorded
                todayRecordId = recordId
            },
        )
        discoveryGraph(navigator, navigationState, onRecordClick)
        sosikGraph(navigator, navigationState, onRecordClick)
        myPageGraph(navigator, navigationState, myPageViewModel, onRecordClick)

        // 비-탭 화면들
        recordGraph(navigator, navigationState, inputRoutinesAndAiRecommendViewModel)
        settingsGraph(navigator, settingsViewModel)
    }

    val context = LocalContext.current
    var lastBackPressedAt by remember { mutableLongStateOf(0L) }
    var exitToast by remember { mutableStateOf<AndroidToast?>(null) }

    LaunchedEffect(navigationState.changeId) {
        val activeKeys = navigationState.stacksInUse
        val lastRoutes = activeKeys.associateWith { key ->
            navigationState.backStacks[key]?.lastOrNull()
        }
        Timber.e(
            "NavState(changeId=${navigationState.changeId}) startRoute=${navigationState.startRoute} topLevelRoute=${navigationState.topLevelRoute} stacksInUse=$activeKeys lastRoutes=$lastRoutes"
        )
    }

    BackHandler(enabled = true) {
        val handled = navigator.handleBack()
        if (!handled) {
            if (navigationState.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys && navigationState.topLevelRoute != Home) {
                navigator.navigate(Home)
                return@BackHandler
            }

            val now = System.currentTimeMillis()
            if (now - lastBackPressedAt <= 2_000L) {
                val activity = context.findActivity()
                exitToast?.view?.animate()?.cancel()
                val animator = exitToast?.view?.animate()?.alpha(0f)?.setDuration(200L)?.withEndAction {
                    exitToast?.cancel()
                    exitToast = null
                    activity?.finish()
                }
                if (animator == null) {
                    exitToast?.cancel()
                    exitToast = null
                    activity?.finish()
                } else {
                    animator.start()
                }
            } else {
                lastBackPressedAt = now
                exitToast?.view?.animate()?.cancel()
                exitToast?.cancel()
                exitToast = AndroidToast.makeText(context, "한 번 더 누르면 종료됩니다", AndroidToast.LENGTH_SHORT).also { toast ->
                    toast.view?.alpha = 0f
                    toast.show()
                    toast.view?.animate()?.alpha(1f)?.setDuration(1000L)?.start()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            modifier = Modifier.fillMaxSize(),
        )

        val isTabRootScreen = navigationState.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys &&
                navigationState.backStacks[navigationState.topLevelRoute]?.lastOrNull() == navigationState.topLevelRoute
        val toastBottomPadding = if (isTabRootScreen) 72.dp else 20.dp

        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = toastBottomPadding)
        ) {
            ErrorToast(message = toastMessage.orEmpty())
        }

        LaunchedEffect(toastMessage) {
            if (toastMessage != null) {
                delay(2000L)
                toastMessage = null
            }
        }
    }

    if (showAlreadyRecordedDialog) {
        RoutineOnlyOneHaveDialog(
            onDismiss = { showAlreadyRecordedDialog = false },
            onViewRecords = {
                showAlreadyRecordedDialog = false
                todayRecordId?.let { recordId ->
                    navigator.navigate(RoutineDetail(recordId.toLong()))
                }
            }
        )
    }
}

