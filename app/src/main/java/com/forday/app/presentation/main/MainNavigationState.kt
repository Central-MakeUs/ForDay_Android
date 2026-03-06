package com.forday.app.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.forday.app.core.navigation.LoadingRoutines
import com.forday.app.core.navigation.MyPage
import com.forday.app.core.navigation.RoutineAiRecommend
import com.forday.app.presentation.allsettings.cancelaccount.navigation.CancelAccount
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.allsettings.privacypolicy.navigation.PrivacyPolicy
import com.forday.app.presentation.allsettings.termsofservice.navigation.TermsOfService
import com.forday.app.presentation.discovery.navigation.Discovery
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.modifyhobby.navigation.ModifyHobby
import com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine
import com.forday.app.presentation.mypage.hobbyphotosetting.navigation.HobbyPhotoSetting
import com.forday.app.presentation.mypage.profilesetting.navigation.ProfileSetting
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.mypage.routinedetail.navigation.SaveCard
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.swipeintro.navigation.SwipeIntro
import com.forday.app.presentation.onboarding.nicknameinput.navigation.InputNickname
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.showpobbies.navigation.OnboardingSuccess
import com.forday.app.presentation.onboarding.showpobbies.navigation.ShowPobbies
import com.forday.app.presentation.onboarding.splash.navigation.Splash
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.mypage.navigation.UserPage
import com.forday.app.presentation.sosik.navigation.Sosik
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * 네비게이션 상태 관리 클래스
 * 각 탭마다 독립적인 백스택을 유지
 */
class MainNavigationState(
    var startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute by topLevelRoute

    var changeId by mutableIntStateOf(0)
        private set

    fun notifyNavChanged() {
        changeId += 1
    }

    /**
     * 현재 사용 중인 스택들
     * startRoute는 항상 백그라운드에 유지되고,
     * 현재 선택된 탭의 스택만 추가로 표시
     */
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

/**
 * MainNavigationState를 생성하고 remember
 *
 * @param startRoute 시작 화면 (예: Home, Login, SelectHobby 등)
 * @param topLevelRoutes 하단 네비게이션 바의 탭들 (Home, Discovery, Story, MyPage)
 */
@Composable
fun rememberMainNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): MainNavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = serializersConfig,
        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class))
    ) {
        mutableStateOf(startRoute)
    }

    // ✅ startRoute가 topLevelRoutes에 없으면 추가
    // ✅ Login도 항상 포함시켜, 전역 로그아웃 시 backstack reset이 가능하게 함
    // ✅ SelectHobby도 항상 포함시켜, 로그인 후 온보딩 진입 시 resetTo(SelectHobby)가 동작하도록 함
    val allRoutes = buildSet {
        addAll(topLevelRoutes)
        add(startRoute)
        add(Login)
        add(SelectHobby)
        add(SelectPeriod(mode = ScreenMode.ONBOARDING))
    }

    // ✅ 모든 route에 대한 백스택 생성
    val backStacks = allRoutes.associateWith { key ->
        rememberNavBackStack(
            configuration = serializersConfig,
            key
        )
    }

    return remember(startRoute, topLevelRoutes) {
        MainNavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}

/**
 * Serialization 설정
 * 모든 NavKey를 여기에 등록해야 합니다.
 */
val serializersConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            // Bottom Bar 탭들
            subclass(Home::class, Home.serializer())
            subclass(Discovery::class, Discovery.serializer())
            subclass(Sosik::class, Sosik.serializer())
            subclass(MyPage::class, MyPage.serializer())

            // 기타 화면들
            subclass(RecordRoutine::class, RecordRoutine.serializer())
            subclass(Splash::class, Splash.serializer())
            subclass(Login::class, Login.serializer())
            subclass(SwipeIntro::class, SwipeIntro.serializer())
            subclass(Settings::class, Settings.serializer())
            subclass(TermsOfService::class, TermsOfService.serializer())
            subclass(PrivacyPolicy::class, PrivacyPolicy.serializer())
            subclass(CancelAccount::class, CancelAccount.serializer())


            // 온보딩 화면들
            subclass(SelectHobby::class, SelectHobby.serializer())
            subclass(SelectHobbyFromModify::class, SelectHobbyFromModify.serializer())
            subclass(SelectPerTime::class, SelectPerTime.serializer())
            subclass(SelectPurpose::class, SelectPurpose.serializer())
            subclass(SelectPerWeek::class, SelectPerWeek.serializer())
            subclass(SelectPeriod::class, SelectPeriod.serializer())
            subclass(OnboardingSuccess::class, OnboardingSuccess.serializer())
            subclass(ShowPobbies::class, ShowPobbies.serializer())
            subclass(InputNickname::class, InputNickname.serializer())

            // 메인 앱 화면들
            subclass(LoadingRoutines::class, LoadingRoutines.serializer())
            subclass(RoutineAiRecommend::class, RoutineAiRecommend.serializer())
            subclass(ModifyRoutine::class, ModifyRoutine.serializer())
            subclass(InputRoutine::class, InputRoutine.serializer())
            subclass(RoutineDetail::class, RoutineDetail.serializer())
            subclass(ModifyHobby::class, ModifyHobby.serializer())
            subclass(ProfileSetting::class, ProfileSetting.serializer())
            subclass(HobbyPhotoSetting::class, HobbyPhotoSetting.serializer())
            subclass(SaveCard::class, SaveCard.serializer())
            subclass(UserPage::class, UserPage.serializer())

        }
    }
}

/**
 * MainNavigationState를 NavEntry 리스트로 변환합니다.
 *
 * 이 함수는 현재 사용 중인 스택들의 entries만 합쳐서 반환하며,
 * SaveableStateHolder와 ViewModelStore decorator를 적용합니다.
 */
@Composable
fun MainNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}