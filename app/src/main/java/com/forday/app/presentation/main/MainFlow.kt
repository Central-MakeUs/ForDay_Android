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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import com.forday.app.core.designsystem.component.layout.ForDayScreenWrapper
import com.forday.app.core.designsystem.component.navigationbar.BottomBar
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.core.designsystem.dialog.RoutineOnlyOneHaveDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.navigation.*
import com.forday.app.presentation.discovery.DiscoveryScreen
import com.forday.app.presentation.discovery.navigation.Discovery
import com.forday.app.presentation.home.HomeScreenRoot
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.inputhobbyroutines.screen.AIRecommendationRoutinesScreenRoot
import com.forday.app.presentation.inputhobbyroutines.screen.InputRoutineScreenRoot
import com.forday.app.presentation.inputhobbyroutines.screen.LoadingRoutinesScreen
import com.forday.app.presentation.modifyhobby.navigation.ModifyHobby
import com.forday.app.presentation.modifyhobby.screen.ModifyHobbyScreenRoot
import com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine
import com.forday.app.presentation.modifyroutine.screen.ModifyRoutineScreenRoot
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.navigation.UserPage
import com.forday.app.presentation.mypage.hobbyphotosetting.HobbyPhotoManagementScreenRoot
import com.forday.app.presentation.mypage.hobbyphotosetting.navigation.HobbyPhotoSetting
import com.forday.app.presentation.mypage.main.MyPageScreen
import com.forday.app.presentation.mypage.profilesetting.ProfileSettingScreenRoot
import com.forday.app.presentation.mypage.profilesetting.navigation.ProfileSetting
import com.forday.app.presentation.mypage.routinedetail.screen.RoutineDetailScreen
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.mypage.routinedetail.navigation.SaveCard
import com.forday.app.presentation.mypage.routinedetail.screen.ActivityCardData
import com.forday.app.presentation.mypage.routinedetail.screen.SaveCardScreen
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.splash.SplashViewModel
import com.forday.app.presentation.onboarding.frequencyselect.SelectFrequencyScreenRoot
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.hobbyselect.SelectHobbyScreenRoot
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.onboarding.login.LoginScreenRoot
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.splash.SplashScreenRoot
import com.forday.app.presentation.onboarding.splash.navigation.Splash
import com.forday.app.presentation.onboarding.swipeintro.SwipeIntroScreen
import com.forday.app.presentation.onboarding.swipeintro.navigation.SwipeIntro
import com.forday.app.presentation.onboarding.nicknameinput.InputNicknameScreenRoot
import com.forday.app.presentation.onboarding.nicknameinput.navigation.InputNickname
import com.forday.app.presentation.onboarding.periodselect.SelectJourneyDaysScreenRoot
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.purposeselect.SelectPurposeScreenRoot
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.showpobbies.OnboardingSuccessScreen
import com.forday.app.presentation.onboarding.showpobbies.ShowPobbiesScreen
import com.forday.app.presentation.onboarding.showpobbies.navigation.OnboardingSuccess
import com.forday.app.presentation.onboarding.showpobbies.navigation.ShowPobbies
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.SelectTimeScreenRoot
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.record.screen.RecordRoutineScreenRoot
import com.forday.app.presentation.sosik.screen.SosikScreenRoot
import com.forday.app.presentation.sosik.navigation.Sosik
import com.forday.app.presentation.sosik.navigation.Register
import com.forday.app.presentation.sosik.screen.ReportScreenRoot
import android.widget.Toast as AndroidToast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.forday.app.core.session.AuthEvent
import com.forday.app.presentation.allsettings.SettingsSideEffect
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.onboarding.OnboardingSideEffect
import com.forday.app.presentation.allsettings.cancelaccount.navigation.CancelAccount
import com.forday.app.presentation.allsettings.cancelaccount.screen.CancelAccountScreen
import com.forday.app.presentation.record.RecordRoutineViewModel
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.allsettings.settings.screen.SettingsScreen
import com.forday.app.presentation.allsettings.privacypolicy.navigation.PrivacyPolicy
import com.forday.app.presentation.allsettings.privacypolicy.screen.PrivacyPolicyScreen
import com.forday.app.presentation.allsettings.termsofservice.navigation.TermsOfService
import com.forday.app.presentation.allsettings.termsofservice.screen.TermsOfServiceScreen
import com.forday.app.presentation.common.AppSideEffect
import com.forday.app.presentation.common.SnackbarHostViewModel
import com.forday.app.core.designsystem.toast.ErrorToast
import timber.log.Timber

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun MainFlow(initialRoute: NavKey, onboardingViewModel: OnboardingViewModel, splashViewModel: SplashViewModel) {

    val navigationState = rememberMainNavigationState(
        startRoute = initialRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys as Set<NavKey>
    )

    // SelectPeriod로 시작하는 경우, 이전 온보딩 화면들을 백스택에 미리 채움
    LaunchedEffect(Unit) {
        if (initialRoute is SelectPeriod) {
            val stack = navigationState.backStacks[initialRoute]
            if (stack != null && stack.size == 1) {
                // 온보딩 순서: SelectHobby → SelectPerTime → SelectPurpose → SelectPerWeek → SelectPeriod
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

    val inputRoutinesAndAiRecommendViewModel: InputRoutinesAndAiRecommendViewModel = hiltViewModel()
    val myPageViewModel: MyPageViewModel = hiltViewModel()
    val mainEventViewModel: MainEventViewModel = hiltViewModel()
    val recordRoutineViewModel: RecordRoutineViewModel = hiltViewModel()
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
        snackbarHostViewModel.sideEffects.collect { effect ->
            when (effect) {
                is AppSideEffect.ShowSnackbar -> {
                    toastMessage = effect.message
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        onboardingViewModel.sideEffect.collect { effect ->
            when (effect) {
                is OnboardingSideEffect.GuestAutoReLoginSuccess -> {
                    // 토큰 재발급 완료 - 현재 화면 유지
                }
                is OnboardingSideEffect.GuestAutoReLoginFailure -> {
                    snackbarHostViewModel.show("로그인이 만료되었어요. 다시 로그인해주세요.")
                    onboardingViewModel.resetForNewSession()
                    navigator.resetTo(Login)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        mainEventViewModel.authEvents.collect { event ->
            when (event) {
                AuthEvent.Expired -> {
                    if (onboardingViewModel.uiState.value.socialType == "GUEST") {
                        onboardingViewModel.guestAutoReLogin()
                    } else {
                        snackbarHostViewModel.show("로그인이 만료되었어요. 다시 로그인해주세요.")
                        onboardingViewModel.resetForNewSession()
                        navigator.resetTo(Login)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        settingsViewModel.sideEffect.collect { effect ->
            when (effect) {
                is SettingsSideEffect.LoggedOut,
                is SettingsSideEffect.AccountCancelled -> {
                    onboardingViewModel.resetForNewSession()
                    navigator.resetTo(Login)
                }
                else -> Unit
            }
        }
    }

    val entryProvider = entryProvider<NavKey> {
        entry<Splash> {
            SplashScreenRoot(splashViewModel = splashViewModel)
        }

        nonTabEntry<SwipeIntro>(backgroundColor = Color(0xFFFFF5EE)) {
            SwipeIntroScreen(
                onNavigateToLogin = {
                    onboardingViewModel.saveHasSeenIntro()
                    navigator.resetTo(Login)
                }
            )
        }

        nonTabEntry<Login> {
            LoginScreenRoot(
                onNavigateToHome = {
                    Timber.e("MainFlow(Login) - navigate to Home requested")
                    navigator.resetTo(Home)
                },
                onNavigateToOnboarding = {
                    Timber.e("MainFlow(Login) - navigate to SelectHobby requested")
                    navigator.navigate(SelectHobby)
                },
                onNavigateToNickname = {
                    Timber.e("MainFlow(Login) - navigate to SelectPeriod requested")
                    navigator.resetTo(
                        route = SelectPeriod(mode = ScreenMode.ONBOARDING),
                        preloadStack = listOf(
                            SelectHobby,
                            SelectPerTime(mode = ScreenMode.ONBOARDING),
                            SelectPurpose,
                            SelectPerWeek(mode = ScreenMode.ONBOARDING),
                            SelectPeriod(mode = ScreenMode.ONBOARDING),
                        )
                    )
                },
                viewModel = onboardingViewModel
            )
        }

        // ==================== 온보딩 플로우 ====================

        nonTabEntry<SelectHobby>(backgroundColor = Color(0xFFF9F9F9)) {
            BackHandler(enabled = true) {
                settingsViewModel.logout()
            }
            SelectHobbyScreenRoot(
                onNext = { navigator.navigate(SelectPerTime(mode = ScreenMode.ONBOARDING)) },
                onBack = { settingsViewModel.logout() },
                viewModel = onboardingViewModel
            )
        }

        nonTabEntry<SelectHobbyFromModify>(backgroundColor = Color(0xFFF9F9F9)) {
            SelectHobbyScreenRoot(
                onNext = { navigator.navigate(SelectPerTime(mode = ScreenMode.ONBOARDING)) },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel,
                fromModifyHobbyOrHome = true
            )
        }

        nonTabEntry<SelectPerTime>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
            SelectTimeScreenRoot(
                params = backStackEntry.params,
                onNext = {
                    if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(
                        SelectPurpose
                    )
                    else navigator.goBack()
                },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel,
                mode = backStackEntry.mode
            )
        }

        nonTabEntry<SelectPurpose>(backgroundColor = Color(0xFFF9F9F9)) {
            SelectPurposeScreenRoot(
                onNext = { navigator.navigate(SelectPerWeek(mode = ScreenMode.ONBOARDING)) },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel
            )
        }

        nonTabEntry<SelectPerWeek>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
            SelectFrequencyScreenRoot(
                params = backStackEntry.params,
                onNext = {
                    if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(
                        SelectPeriod(mode = ScreenMode.ONBOARDING)
                    )
                    else navigator.goBack()
                },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel,
                mode = backStackEntry.mode
            )
        }

        nonTabEntry<SelectPeriod>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
            SelectJourneyDaysScreenRoot(
                params = backStackEntry.params,
                mode = backStackEntry.mode,
                onNext = {
                    if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(
                        OnboardingSuccess
                    )
                    else navigator.goBack()
                },
                onBack = {
                    Timber.e("@@@@@@@@@@@@@@ onBack")
                    navigator.goBack()
                },
                viewModel = onboardingViewModel,
                goHome = { navigator.resetTo(Home) }
            )
        }

        nonTabEntry<OnboardingSuccess> {
            BackHandler(enabled = true) { }
            OnboardingSuccessScreen(
                onNext = { navigator.navigate(ShowPobbies) },
                onDirectHome = { navigator.resetTo(Home) },
                viewModel = onboardingViewModel
            )
        }

        nonTabEntry<ShowPobbies> {
            val context = LocalContext.current
            BackHandler(enabled = true) {
                context.findActivity()?.finish()
            }
            ShowPobbiesScreen(
                onNext = { navigator.navigate(InputNickname) },
                viewModel = onboardingViewModel
            )
        }

        nonTabEntry<InputNickname> {
            InputNicknameScreenRoot(
                onNext = { navigator.resetTo(Home) },
                viewModel = onboardingViewModel
            )
        }

        // ==================== 메인 앱 (Bottom Bar 탭들) ====================

        entry<Home> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = {
                    if (isRecordedToday) {
                        showAlreadyRecordedDialog = true
                    } else {
                        navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
                    }
                },
            ) { padding ->
                HomeScreenRoot(
                    modifier = Modifier.padding(padding),
                    onRoutineCreate = { hobbyId, aiCallRemaining, hobbyName ->
                        Timber.e("@@@@@@#######" + hobbyId)
                        navigator.navigate(InputRoutine(hobbyId, aiCallRemaining, hobbyName))
                    },
                    onModifyRoutine = { hobbyId, hobbyName -> navigator.navigate(ModifyRoutine(hobbyId, hobbyName)) },
                    onRecordRoutine = { hobbyId, entryPoint, hobbyName, activityName ->
                        Timber.e("@@@@@@#######routineId : " + hobbyId)
                        navigator.navigate(RecordRoutine(hobbyId, entryPoint = entryPoint, hobbyName = hobbyName, activityName = activityName))
                    },
                    onModifyHobby = { navigator.navigate(ModifyHobby) },
                    onAllSettingsClick = { navigator.navigate(Settings) },
                    onMoveRecordedRoutine = { recordId ->
                        navigator.navigate(RoutineDetail(recordId.toLong()))
                    },
                    onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
                    onSelectHobby = { navigator.navigate(SelectHobbyFromModify) },
                    onCurrentHobbyIdChanged = { hobbyId -> currentHobbyId = hobbyId },
                    onCurrentHobbyInfoChanged = { hobbyName, activityName ->
                        currentHobbyName = hobbyName
                        currentActivityName = activityName
                    },
                    onRecordStateChanged = { recorded, recordId ->
                        isRecordedToday = recorded
                        todayRecordId = recordId
                    },
                )
            }
        }

        entry<Discovery> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = {
                    if (isRecordedToday) {
                        showAlreadyRecordedDialog = true
                    } else {
                        navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
                    }
                },
            ) { _ ->
                DiscoveryScreen()
            }
        }

        entry<Sosik> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = {
                    if (isRecordedToday) {
                        showAlreadyRecordedDialog = true
                    } else {
                        navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
                    }
                },
            ) { padding ->
                SosikScreenRoot(
                    modifier = Modifier.padding(padding),
                    onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
                    onCardClick = { recordId -> navigator.navigate(RoutineDetail(recordId, isUserPageEntry = true)) },
                    onProfileClick = { userId, recordAuthor -> navigator.navigate(UserPage(userId, recordAuthor)) }
                )
            }
        }

        entry<MyPage> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = {
                    if (isRecordedToday) {
                        showAlreadyRecordedDialog = true
                    } else {
                        navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
                    }
                },
            ) { padding ->
                MyPageScreen(
                    modifier = Modifier.padding(padding),
                    viewModel = myPageViewModel,
                    onProfileSetting = { navigator.navigate(ProfileSetting) },  // 내 프로필 설정으로 이동
                    onHobbyPhotoManagement = { navigator.navigate(HobbyPhotoSetting) },  // 취미 대표사진 관리로 이동
                    onAllSettingsClick = { navigator.navigate(Settings) }, // 전체설정
                    onRoutineFeedClick = { routineId -> navigator.navigate(RoutineDetail(routineId.toLong())) },
                    onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
                    onDismiss = { },
                    onNavigateToRecordRoutine = { hobbyId -> navigator.navigate(RecordRoutine(hobbyId = hobbyId?.toLong(), entryPoint = "mypage")) },
                    onReportUserClick = { navigator.navigate(Register()) },
                )
            }
        }

        // ==================== 기타 화면들 (Bottom Bar 없음) ====================

        nonTabEntry<RecordRoutine> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            val modifyData = backStackEntry.modifyData
            val modifyMode = backStackEntry.modifyMode
            val shouldResetToMyPage = backStackEntry.shouldResetToMyPage
            val entryPoint = backStackEntry.entryPoint
            val hobbyName = backStackEntry.hobbyName
            val activityName = backStackEntry.activityName

            RecordRoutineScreenRoot(
                hobbyId = hobbyId,
                modifyData = modifyData,
                modifyMode = modifyMode,
                onComplete = {
                        routineId ->
                    Timber.e("@#@#@#@#@##@#routineId "+routineId)
                    if (shouldResetToMyPage) {
                        navigator.resetTo(MyPage)
                    } else {
                        navigator.goBack()
                        navigator.goBack()
                    }
                    navigator.navigate(RoutineDetail(routineId, !modifyMode))
                    Timber.e("routineId@@@@@@@@@@@@@ : "+routineId)
                },
                onClose = { navigator.goBack() },
                viewModel = recordRoutineViewModel,
                onRoutineCreate = {
                    navigator.goBack()
                    navigator.navigate(InputRoutine(hobbyId, null))
                },
                onAddHobbyClick = {
                    navigator.goBack()
                    navigator.navigate(SelectHobbyFromModify)
                },
                entryPoint = entryPoint,
                hobbyName = hobbyName,
                activityName = activityName
            )
        }

        nonTabEntry<InputRoutine> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            val hobbyName = backStackEntry.hobbyName
            Timber.e("@@@@@@@########@@@@@@ " + hobbyId)
            InputRoutineScreenRoot(
                hobbyId = hobbyId,
                hobbyName = hobbyName,
                aiCallRemaining = backStackEntry.aiCallRemaining,
                onNavigateToModifyRoutine = {
                    val stack = navigationState.backStacks[navigationState.topLevelRoute]
                    val secondToLast = stack?.let { if (it.size >= 2) it[it.size - 2] else null }
                    if (secondToLast is ModifyRoutine) {
                        navigator.goBack()
                    } else {
                        navigator.replaceWith(ModifyRoutine(hobbyId = hobbyId, hobbyName = hobbyName))
                    }
                },
                onAIRecommendationRoutines = { hobbyId -> navigator.navigate(LoadingRoutines(hobbyId)) },
                viewModel = inputRoutinesAndAiRecommendViewModel,
                onExit = { navigator.goBack() }
            )
        }

        nonTabEntry<LoadingRoutines> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            BackHandler(enabled = true) { }

            LoadingRoutinesScreen(
                hobbyId = hobbyId,
                onNext = { hobbyId ->
                    navigator.replaceWith(RoutineAiRecommend(hobbyId))
                },
                viewModel = inputRoutinesAndAiRecommendViewModel
            )
        }

        nonTabEntry<RoutineAiRecommend>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            AIRecommendationRoutinesScreenRoot(
                hobbyId = hobbyId,
                onBackClick = { navigator.goBack() },
                onNextClick = { navigator.goBack() },
                viewModel = inputRoutinesAndAiRecommendViewModel
            )
        }

        nonTabEntry<ModifyRoutine>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            val hobbyName = backStackEntry.hobbyName
            ModifyRoutineScreenRoot(
                hobbyId = hobbyId,
                onBack = { navigator.goBack() },
                onAddRoutine = { navigator.navigate(InputRoutine(hobbyId, hobbyName = hobbyName)) },
                onEditRoutine = {},
            )
        }

        nonTabEntry<ModifyHobby>(backgroundColor = Color(0xFFF9F9F9)) {  // 내 취미정보를 수정하고 추가하기
            ModifyHobbyScreenRoot(
                onAddHobby = { navigator.navigate(SelectHobbyFromModify) },   // 취미 추가 (취미 생성)
                onChangeDuration = { params -> navigator.navigate(SelectPerTime(params = params, mode = ScreenMode.DEFAULT)) },
                onChangeFrequency = { params -> navigator.navigate(SelectPerWeek(params = params, mode = ScreenMode.DEFAULT)) },  // 취미횟수
                onChangeJourneyDays = { params -> navigator.navigate(SelectPeriod(params = params, mode = ScreenMode.DEFAULT)) },  // 여정일
                onBack = { navigator.goBack() },
            )
        }

        nonTabEntry<RoutineDetail> { backStackEntry ->
            val routineId = backStackEntry.routineId
            val isNewRecord = backStackEntry.isNewRecord
            val isUserPageEntry = backStackEntry.isUserPageEntry
            RoutineDetailScreen(
                viewModel = myPageViewModel,
                routineId = routineId,
                onBackClick = {navigator.goBack()},
                onNavigateToMyPage = {
                    navigator.resetTo(MyPage)
                },
                isNewRecord = isNewRecord,
                isUserPageEntry = isUserPageEntry,
                onNavigateToRecordRoutine = { data, mode -> navigator.navigate(RecordRoutine(modifyData = data, modifyMode = mode)) },
                onNavigateToHome = { navigator.resetTo(Home) },
                onNavigateToUserPage = { userId, recordAuthor -> navigator.navigate(UserPage(userId, recordAuthor)) },
                onReportClick = { navigator.navigate(Register()) },
                onSaveCardClick = {
                    val details = myPageViewModel.uiState.value.myRoutineDetails
                    navigator.navigate(
                        SaveCard(
                            imageUrl = details?.imageUrl ?: "",
                            title = details?.content ?: "",
                            dateTime = details?.date ?: "",
                            memo = details?.memo ?: "",
                            stickerUrl = details?.stickerUrl ?: ""
                        )
                    )
                },
            )
        }

        nonTabEntry<Register> { backStackEntry ->
            val userId = backStackEntry.userId
            ReportScreenRoot(
                recordId = myPageViewModel.uiState.value.myRoutineDetails?.recordId ?: 0,
                nickname = myPageViewModel.uiState.value.myRoutineDetails?.writerNickname ?: "",
                onBack = { navigator.goBack() },
                onComplete = { navigator.goBack() },
                onNavigateToSosik = { navigator.resetTo(Sosik) },
                viewModel = myPageViewModel,
                userId = userId,
            )
        }

        nonTabEntry<SaveCard> { backStackEntry ->
            SaveCardScreen(
                cardData = ActivityCardData(
                    imageUrl = backStackEntry.imageUrl,
                    title = backStackEntry.title,
                    dateTime = backStackEntry.dateTime,
                    memo = backStackEntry.memo,
                    dateFormatted = backStackEntry.dateFormatted,
                    stickerUrl = backStackEntry.stickerUrl
                ),
                onBack = { navigator.goBack() },
            )
        }

        nonTabEntry<ProfileSetting> {
            ProfileSettingScreenRoot(
                viewModel = myPageViewModel,
                goBack = { navigator.goBack() },
            )
        }

        nonTabEntry<HobbyPhotoSetting> {
            HobbyPhotoManagementScreenRoot(
                viewModel = myPageViewModel,
                onBackClick = { navigator.goBack() },
                onCompleteClick = { navigator.goBack() }
            )
        }

        nonTabEntry<Settings> {
            SettingsScreen(
                onBackClick = { navigator.goBack() },
                onTermsOfServiceClick = { navigator.navigate(TermsOfService) },
                onPrivacyPolicyClick = { navigator.navigate(PrivacyPolicy) },
                onCancelAccountClick = { navigator.navigate(CancelAccount) },
                viewModel = settingsViewModel
            )
        }

        nonTabEntry<TermsOfService>(backgroundColor = Color(0xFFF9F9F9)) {
            TermsOfServiceScreen(
                onCloseClick = { navigator.goBack() }
            )
        }

        nonTabEntry<PrivacyPolicy>(backgroundColor = Color(0xFFF9F9F9)) {
            PrivacyPolicyScreen(
                onCloseClick = { navigator.goBack() }
            )
        }

        nonTabEntry<CancelAccount> {
            CancelAccountScreen(
                onBackClick = { navigator.goBack() },
                viewModel = settingsViewModel
            )
        }

        nonTabEntry<UserPage> { backStackEntry ->
            val userId = backStackEntry.userId
            val recordAuthor = backStackEntry.recordAuthor
            val userPageViewModel: MyPageViewModel = hiltViewModel()
            Timber.e("@@@@@@@@@@@@@@@@@@@@@@ userId : "+userId)
            MyPageScreen(
                viewModel = userPageViewModel,
                onProfileSetting = { },
                onHobbyPhotoManagement = { },
                onAllSettingsClick = { navigator.navigate(Settings) },
                onRoutineFeedClick = { recordId -> navigator.navigate(RoutineDetail(recordId.toLong(), isUserPageEntry = true)) },
                onAddHobbyClick = { },
                onNavigateToRecordRoutine = { },
                onDismiss = { },
                onBackClick = { navigator.goBack() },
                onNavigateToSosik = { navigator.resetTo(Sosik) },
                onReportUserClick = { navigator.navigate(Register(userId = userId)) },
                userId = userId,
                recordAuthor = recordAuthor,
                isUserPageEntry = true,
            )
        }
    }

    ForDayTheme {
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
            Timber.e("@@@@@@@@@@@@@@@@@route: " + initialRoute)
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

private inline fun <reified T : NavKey> EntryProviderScope<NavKey>.nonTabEntry(
    backgroundColor: Color = Color.White,
    noinline content: @Composable (T) -> Unit
) {
    entry<T> { key ->
        ForDayScreenWrapper(backgroundColor = backgroundColor) {
            content(key)
        }
    }
}

@Composable
private fun MainTabScaffold(
    navigationState: MainNavigationState,
    onTabSelected: (BottomBarTab) -> Unit,
    onRecordClick: () -> Unit,
    backgroundColor: Color = Color.White,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                BottomBar(
                    modifier = Modifier,
                    selectedTab = navigationState.topLevelRoute.toBottomBarTab(),
                    onTabSelected = onTabSelected,
                    onRecordClick = onRecordClick,
                )
            },
            content = content,
        )
    }
}