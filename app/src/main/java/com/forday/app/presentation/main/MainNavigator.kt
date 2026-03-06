package com.forday.app.presentation.main

import androidx.navigation3.runtime.NavKey
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.core.navigation.MyPage
import com.forday.app.presentation.discovery.navigation.Discovery
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.sosik.navigation.Sosik
import timber.log.Timber

/**
 * 네비게이션 로직을 처리하는 클래스
 */
class Navigator(val state: MainNavigationState) {

    /**
     * 주어진 route로 이동합니다.
     *
     * - route가 Top-level destination이면 → 탭 전환
     * - 그렇지 않으면 → 현재 탭의 백스택에 추가
     */
    fun navigate(route: NavKey) {
        val currentTop = state.topLevelRoute
        val currentStackBefore = state.backStacks[currentTop]
        Timber.e(
            "Navigator.navigate(route=$route) currentTop=$currentTop stackSizeBefore=${currentStackBefore?.size} lastBefore=${currentStackBefore?.lastOrNull()}"
        )
        if (route in TOP_LEVEL_DESTINATIONS.keys) {
            // 탭 전환
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }

        state.notifyNavChanged()

        val currentTopAfter = state.topLevelRoute
        val currentStackAfter = state.backStacks[currentTopAfter]
        Timber.e(
            "Navigator.navigate(route=$route) currentTopAfter=$currentTopAfter stackSizeAfter=${currentStackAfter?.size} lastAfter=${currentStackAfter?.lastOrNull()}"
        )
    }

    /**
     * 뒤로가기 처리
     *
     * - 현재 화면이 탭의 첫 화면이면 → 시작 탭으로 이동
     * - 그렇지 않으면 → 백스택에서 pop
     */
    fun goBack() {
        handleBack()
    }

    /**
     * 뒤로가기를 실제로 처리했는지 반환합니다.
     *
     * @return true면 네비게이션이 처리되어 소비됨, false면 더 이상 처리할 게 없음(예: Home 루트)
     */
    fun handleBack(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Back stack for ${state.topLevelRoute} doesn't exist")

        val currentRoute = currentStack.last()

        return if (currentStack.size <= 1) {
            // 스택에 루트만 남아있으면 더 이상 뒤로 갈 수 없음
            if (state.topLevelRoute == Login) {
                false  // Login에서는 처리하지 않음 → 전역 BackHandler에서 앱 종료 처리
            } else {
                // startRoute가 탭(Home/Discovery/Story/MyPage)인 경우에만 startRoute로 이동
                // startRoute가 Login/온보딩이면 여기서 더 이상 처리하지 않음(상위에서 앱 종료 UX 처리)
                if (state.startRoute in TOP_LEVEL_DESTINATIONS.keys) {
                    // 이미 startRoute와 현재 탭이 동일하면 더 이상 이동할 곳이 없으므로 처리하지 않았다고 반환
                    if (state.startRoute == state.topLevelRoute) {
                        return false
                    }
                    state.topLevelRoute = state.startRoute
                    state.notifyNavChanged()
                    true
                } else {
                    false
                }
            }
        } else {
            // 백스택에서 제거
            currentStack.removeLastOrNull()
            state.notifyNavChanged()
            true
        }
    }

    /**
     * 모든 백스택을 초기화한 뒤, 지정 route로 이동합니다.
     * 뒤로가기를 눌러도 이전 화면으로 돌아가지 않도록 하기 위해 사용합니다.
     *
     * @param preloadStack 루트 위에 미리 쌓을 화면 목록 (순서대로 추가, 뒤로가기 지원용)
     */
    fun resetTo(route: NavKey, preloadStack: List<NavKey> = emptyList()) {
        state.backStacks.forEach { (key, stack) ->
            while (stack.removeLastOrNull() != null) { }
            stack.add(key)
        }

        state.topLevelRoute = route
        // Home 또는 탭이 아닌 경로(Login, 온보딩 등)에만 startRoute 갱신
        // 비Home 탭(MyPage, Sosik 등)으로 resetTo 시엔 startRoute를 유지해
        // 뒤로가기 시 원래 홈(Home)으로 돌아갈 수 있도록 함
        if (route == Home || route !in TOP_LEVEL_DESTINATIONS.keys) {
            state.startRoute = route
        }

        val targetStack = state.backStacks[route]
            ?: error("Back stack for $route doesn't exist")

        if (targetStack.lastOrNull() != route) {
            targetStack.add(route)
        }

        preloadStack.forEach { targetStack.add(it) }

        state.notifyNavChanged()
    }

    /**
     * 현재 route를 다른 route로 교체합니다.
     * (LoadingRoutines → RoutineAiRecommend 같은 경우에 사용)
     */
    fun replaceWith(route: NavKey) {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Back stack for ${state.topLevelRoute} doesn't exist")

        currentStack.removeLastOrNull()
        currentStack.add(route)

        state.notifyNavChanged()
    }
}

/**
 * 하단 네비게이션 바의 아이템 데이터
 */
data class BottomNavItem(
    val icon: Int,  // Drawable resource ID
    val iconSelected: Int,  // Drawable resource ID (선택 시)
    val title: String,
)

/**
 * Top-level destinations (하단 네비게이션 바의 탭들)
 */
val TOP_LEVEL_DESTINATIONS = mapOf(
    Home to BottomNavItem(
        icon = com.dayn.forday.R.drawable.home,
        iconSelected = com.dayn.forday.R.drawable.home_selected,
        title = "홈"
    ),
    Discovery to BottomNavItem(
        icon = com.dayn.forday.R.drawable.discovery,
        iconSelected = com.dayn.forday.R.drawable.discovery_selected,
        title = "발견"
    ),
    Sosik to BottomNavItem(
        icon = com.dayn.forday.R.drawable.ic_story_unselected,
        iconSelected = com.dayn.forday.R.drawable.ic_story_unselected,  //selected로 수정해야 함
        title = "소식"
    ),
    MyPage to BottomNavItem(
        icon = com.dayn.forday.R.drawable.ic_my,
        iconSelected = com.dayn.forday.R.drawable.ic_my_selected,
        title = "마이"
    ),
)

/**
 * NavKey를 BottomBarTab으로 변환
 */
fun NavKey.toBottomBarTab(): BottomBarTab = when(this) {
    Home -> BottomBarTab.HOME
    Discovery -> BottomBarTab.DISCOVERY
    Sosik -> BottomBarTab.STORY
    MyPage -> BottomBarTab.MYPAGE
    else -> BottomBarTab.HOME
}

/**
 * BottomBarTab을 NavKey로 변환
 */
fun BottomBarTab.toNavKey(): NavKey = when(this) {
    BottomBarTab.HOME -> Home
    BottomBarTab.DISCOVERY -> Discovery
    BottomBarTab.STORY -> Sosik
    BottomBarTab.MYPAGE -> MyPage
}