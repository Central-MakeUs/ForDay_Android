package com.forday.app.presentation.discovery.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.tabEntry
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.presentation.discovery.DiscoveryScreen
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey

fun EntryProviderScope<NavKey>.discoveryGraph(
    navigator: Navigator,
    navigationState: MainNavigationState,
    onRecordClick: () -> Unit,
) {
    tabEntry<Discovery>(
        navigationState = navigationState,
        onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
        onRecordClick = onRecordClick,
    ) { _, _ ->
        DiscoveryScreen()
    }
}
