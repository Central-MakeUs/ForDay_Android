package com.forday.app.core.session

import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.swipeintro.navigation.SwipeIntro
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import javax.inject.Inject

class RouteResolver @Inject constructor() {

    fun resolve(state: SessionState): NavKey = when {
        state.accessToken == null && !state.hasSeenIntro -> SwipeIntro
        state.accessToken == null -> Login
        !state.isOnboardingCompleted -> SelectHobby
        state.isOnboardingCompleted && state.isNicknameSet -> Home
        else -> SelectPeriod(mode = ScreenMode.ONBOARDING)
    }
}
