package com.forday.app.presentation.record.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.MyPage
import com.forday.app.core.navigation.LoadingRoutines
import com.forday.app.core.navigation.RoutineAiRecommend
import com.forday.app.core.navigation.nonTabEntry
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.inputhobbyroutines.screen.AIRecommendationRoutinesScreenRoot
import com.forday.app.presentation.inputhobbyroutines.screen.InputRoutineScreenRoot
import com.forday.app.presentation.inputhobbyroutines.screen.LoadingRoutinesScreen
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.modifyhobby.navigation.ModifyHobby
import com.forday.app.presentation.modifyhobby.screen.ModifyHobbyScreenRoot
import com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine
import com.forday.app.presentation.modifyroutine.screen.ModifyRoutineScreenRoot
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.record.RecordRoutineViewModel
import com.forday.app.presentation.record.screen.RecordRoutineScreenRoot

fun EntryProviderScope<NavKey>.recordGraph(
    navigator: Navigator,
    navigationState: MainNavigationState,
    inputRoutinesAndAiRecommendViewModel: InputRoutinesAndAiRecommendViewModel,
) {
    nonTabEntry<RecordRoutine> { backStackEntry ->
        val hobbyId = backStackEntry.hobbyId
        val modifyData = backStackEntry.modifyData
        val modifyMode = backStackEntry.modifyMode
        val shouldResetToMyPage = backStackEntry.shouldResetToMyPage
        val entryPoint = backStackEntry.entryPoint
        val hobbyName = backStackEntry.hobbyName
        val activityName = backStackEntry.activityName
        val recordRoutineViewModel: RecordRoutineViewModel = hiltViewModel()

        RecordRoutineScreenRoot(
            hobbyId = hobbyId,
            modifyData = modifyData,
            modifyMode = modifyMode,
            onComplete = { routineId ->
                if (shouldResetToMyPage) {
                    navigator.resetTo(MyPage)
                } else {
                    navigator.goBack()
                    navigator.goBack()
                }
                navigator.navigate(RoutineDetail(routineId, !modifyMode))
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
            onAIRecommendationRoutines = { id -> navigator.navigate(LoadingRoutines(id)) },
            viewModel = inputRoutinesAndAiRecommendViewModel,
            onExit = { navigator.goBack() }
        )
    }

    nonTabEntry<LoadingRoutines> { backStackEntry ->
        val hobbyId = backStackEntry.hobbyId
        BackHandler(enabled = true) { }

        LoadingRoutinesScreen(
            hobbyId = hobbyId,
            onNext = { id ->
                navigator.replaceWith(RoutineAiRecommend(id))
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

    nonTabEntry<ModifyHobby>(backgroundColor = Color(0xFFF9F9F9)) {
        ModifyHobbyScreenRoot(
            onAddHobby = { navigator.navigate(SelectHobbyFromModify) },
            onChangeDuration = { params -> navigator.navigate(SelectPerTime(params = params, mode = ScreenMode.DEFAULT)) },
            onChangeFrequency = { params -> navigator.navigate(SelectPerWeek(params = params, mode = ScreenMode.DEFAULT)) },
            onChangeJourneyDays = { params -> navigator.navigate(SelectPeriod(params = params, mode = ScreenMode.DEFAULT)) },
            onBack = { navigator.goBack() },
        )
    }
}
