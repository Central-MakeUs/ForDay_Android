package com.forday.app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.designsystem.component.layout.ForDayScreenWrapper
import com.forday.app.core.designsystem.component.navigationbar.BottomBar
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.toBottomBarTab

inline fun <reified T : NavKey> EntryProviderScope<NavKey>.nonTabEntry(
    backgroundColor: Color = Color.White,
    noinline content: @Composable (T) -> Unit
) {
    entry<T> { key ->
        ForDayScreenWrapper(backgroundColor = backgroundColor) {
            content(key)
        }
    }
}

inline fun <reified T : NavKey> EntryProviderScope<NavKey>.tabEntry(
    navigationState: MainNavigationState,
    noinline onTabSelected: (BottomBarTab) -> Unit,
    noinline onRecordClick: () -> Unit,
    backgroundColor: Color = Color.White,
    noinline content: @Composable (T, PaddingValues) -> Unit
) {
    entry<T> { key ->
        MainTabScaffold(
            navigationState = navigationState,
            onTabSelected = onTabSelected,
            onRecordClick = onRecordClick,
            backgroundColor = backgroundColor,
        ) { padding ->
            content(key, padding)
        }
    }
}

@Composable
fun MainTabScaffold(
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
