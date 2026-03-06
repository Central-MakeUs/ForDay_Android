package com.forday.app.presentation.home.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.tabEntry
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.home.HomeScreenRoot
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey
import com.forday.app.presentation.modifyhobby.navigation.ModifyHobby
import com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.record.navigation.RecordRoutine

fun EntryProviderScope<NavKey>.homeGraph(
    navigator: Navigator,
    navigationState: MainNavigationState,
    onRecordClick: () -> Unit,
    onCurrentHobbyIdChanged: (Long?) -> Unit,
    onCurrentHobbyInfoChanged: (String?, String?) -> Unit,
    onRecordStateChanged: (Boolean, Int?) -> Unit,
) {
    tabEntry<Home>(
        navigationState = navigationState,
        onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
        onRecordClick = onRecordClick,
    ) { _, padding ->
        HomeScreenRoot(
            modifier = Modifier.padding(padding),
            onRoutineCreate = { hobbyId, aiCallRemaining, hobbyName ->
                navigator.navigate(InputRoutine(hobbyId, aiCallRemaining, hobbyName))
            },
            onModifyRoutine = { hobbyId, hobbyName -> navigator.navigate(ModifyRoutine(hobbyId, hobbyName)) },
            onRecordRoutine = { hobbyId, entryPoint, hobbyName, activityName ->
                navigator.navigate(RecordRoutine(hobbyId, entryPoint = entryPoint, hobbyName = hobbyName, activityName = activityName))
            },
            onModifyHobby = { navigator.navigate(ModifyHobby) },
            onAllSettingsClick = { navigator.navigate(Settings) },
            onMoveRecordedRoutine = { recordId ->
                navigator.navigate(RoutineDetail(recordId.toLong()))
            },
            onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
            onSelectHobby = { navigator.navigate(SelectHobbyFromModify) },
            onCurrentHobbyIdChanged = onCurrentHobbyIdChanged,
            onCurrentHobbyInfoChanged = onCurrentHobbyInfoChanged,
            onRecordStateChanged = onRecordStateChanged,
        )
    }
}
