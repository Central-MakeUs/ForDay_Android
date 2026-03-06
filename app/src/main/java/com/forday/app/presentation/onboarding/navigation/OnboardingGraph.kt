package com.forday.app.presentation.onboarding.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.nonTabEntry
import com.forday.app.core.session.AuthManager
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.frequencyselect.SelectFrequencyScreenRoot
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.hobbyselect.SelectHobbyScreenRoot
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.onboarding.login.LoginScreenRoot
import com.forday.app.presentation.onboarding.login.LoginViewModel
import com.forday.app.presentation.onboarding.login.LoginSideEffect
import com.forday.app.presentation.onboarding.login.LoginDestination
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.swipeintro.SwipeIntroScreen
import com.forday.app.presentation.onboarding.swipeintro.navigation.SwipeIntro
import com.forday.app.presentation.onboarding.nicknameinput.InputNicknameScreenRoot
import com.forday.app.presentation.onboarding.nicknameinput.NicknameViewModel
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
import com.forday.app.presentation.main.findActivity
import kotlinx.coroutines.launch

fun EntryProviderScope<NavKey>.onboardingGraph(
    navigator: Navigator,
    authManager: AuthManager,
    onboardingViewModel: OnboardingViewModel,
) {
    nonTabEntry<SwipeIntro>(backgroundColor = Color(0xFFFFF5EE)) {
        val introScope = rememberCoroutineScope()
        SwipeIntroScreen(
            onNavigateToLogin = {
                introScope.launch {
                    authManager.saveHasSeenIntro(true)
                }
                navigator.resetTo(Login)
            }
        )
    }

    nonTabEntry<Login> {
        val loginViewModel: LoginViewModel = hiltViewModel()

        LaunchedEffect(Unit) {
            loginViewModel.sideEffect.collect { effect ->
                when (effect) {
                    is LoginSideEffect.NavigateTo -> {
                        when (effect.destination) {
                            LoginDestination.Home -> navigator.resetTo(Home)
                            LoginDestination.Onboarding -> navigator.navigate(SelectHobby)
                            LoginDestination.Nickname -> navigator.resetTo(InputNickname)
                        }
                    }
                }
            }
        }

        LoginScreenRoot(
            viewModel = loginViewModel,
        )
    }

    nonTabEntry<SelectHobby>(backgroundColor = Color(0xFFF9F9F9)) {
        val onboardingBackScope = rememberCoroutineScope()
        val onLogoutFromOnboarding: () -> Unit = {
            onboardingBackScope.launch {
                authManager.resetForNewSession()
                navigator.resetTo(Login)
            }
        }
        BackHandler(enabled = true) {
            onLogoutFromOnboarding()
        }
        SelectHobbyScreenRoot(
            onNext = { navigator.navigate(SelectPerTime(mode = ScreenMode.ONBOARDING)) },
            onBack = { onLogoutFromOnboarding() },
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
                if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(SelectPurpose)
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
                if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(OnboardingSuccess)
                else navigator.goBack()
            },
            onBack = { navigator.goBack() },
            viewModel = onboardingViewModel,
            authManager = authManager,
            goHome = { navigator.resetTo(Home) }
        )
    }

    nonTabEntry<OnboardingSuccess> {
        BackHandler(enabled = true) { }
        OnboardingSuccessScreen(
            onNext = { navigator.navigate(ShowPobbies) },
            onDirectHome = { navigator.resetTo(Home) },
        )
    }

    nonTabEntry<ShowPobbies> {
        val context = LocalContext.current
        BackHandler(enabled = true) {
            context.findActivity()?.finish()
        }
        ShowPobbiesScreen(
            onNext = { navigator.navigate(InputNickname) },
        )
    }

    nonTabEntry<InputNickname> {
        val nicknameViewModel: NicknameViewModel = hiltViewModel()
        InputNicknameScreenRoot(
            onNext = { navigator.resetTo(Home) },
            viewModel = nicknameViewModel,
        )
    }
}
