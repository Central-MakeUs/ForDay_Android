package com.forday.app.presentation.sosik.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.tabEntry
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey
import com.forday.app.presentation.mypage.navigation.UserPage
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.sosik.screen.SosikScreenRoot

fun EntryProviderScope<NavKey>.sosikGraph(
    navigator: Navigator,
    navigationState: MainNavigationState,
    onRecordClick: () -> Unit,
) {
    tabEntry<Sosik>(
        navigationState = navigationState,
        onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
        onRecordClick = onRecordClick,
    ) { _, padding ->
        SosikScreenRoot(
            modifier = Modifier.padding(padding),
            onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
            onCardClick = { recordId -> navigator.navigate(RoutineDetail(recordId, isUserPageEntry = true)) },
            onProfileClick = { userId, recordAuthor -> navigator.navigate(UserPage(userId, recordAuthor)) }
        )
    }
}
