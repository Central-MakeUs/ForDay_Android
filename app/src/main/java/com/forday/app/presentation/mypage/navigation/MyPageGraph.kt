package com.forday.app.presentation.mypage.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.MyPage
import com.forday.app.core.navigation.nonTabEntry
import com.forday.app.core.navigation.tabEntry
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.routinedetail.RoutineDetailViewModel
import com.forday.app.presentation.mypage.hobbyphotosetting.HobbyPhotoManagementScreenRoot
import com.forday.app.presentation.mypage.hobbyphotosetting.navigation.HobbyPhotoSetting
import com.forday.app.presentation.mypage.main.MyPageScreenRoot
import com.forday.app.presentation.mypage.profilesetting.ProfileSettingScreenRoot
import com.forday.app.presentation.mypage.profilesetting.navigation.ProfileSetting
import com.forday.app.presentation.mypage.routinedetail.screen.RoutineDetailScreen
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.mypage.routinedetail.navigation.SaveCard
import com.forday.app.presentation.mypage.routinedetail.screen.ActivityCardData
import com.forday.app.presentation.mypage.routinedetail.screen.SaveCardScreen
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.sosik.navigation.Register
import com.forday.app.presentation.sosik.navigation.Sosik
import com.forday.app.presentation.sosik.screen.ReportScreenRoot

fun EntryProviderScope<NavKey>.myPageGraph(
    navigator: Navigator,
    navigationState: MainNavigationState,
    myPageViewModel: MyPageViewModel,
    onRecordClick: () -> Unit,
) {
    // MyPage 탭 entry
    tabEntry<MyPage>(
        navigationState = navigationState,
        onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
        onRecordClick = onRecordClick,
    ) { _, padding ->
        MyPageScreenRoot(
            modifier = Modifier.padding(padding),
            viewModel = myPageViewModel,
            onProfileSetting = {
                navigator.navigate(
                    ProfileSetting(
                        profileImageUrl = myPageViewModel.uiState.value.profile.userInfo?.profileImageUrl,
                        currentNickname = myPageViewModel.uiState.value.profile.userInfo?.nickName,
                    )
                )
            },
            onHobbyPhotoManagement = { navigator.navigate(HobbyPhotoSetting) },
            onAllSettingsClick = { navigator.navigate(Settings) },
            onRoutineFeedClick = { routineId -> navigator.navigate(RoutineDetail(routineId.toLong())) },
            onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
            onDismiss = { },
            onNavigateToRecordRoutine = { hobbyId -> navigator.navigate(RecordRoutine(hobbyId = hobbyId?.toLong(), entryPoint = "mypage")) },
            onReportUserClick = { navigator.navigate(Register()) },
        )
    }

    // MyPage 하위 화면들 (Bottom Bar 없음)
    nonTabEntry<RoutineDetail> { backStackEntry ->
        val routineDetailViewModel: RoutineDetailViewModel = hiltViewModel()
        val routineId = backStackEntry.routineId
        val isNewRecord = backStackEntry.isNewRecord
        val isUserPageEntry = backStackEntry.isUserPageEntry
        RoutineDetailScreen(
            viewModel = routineDetailViewModel,
            routineId = routineId,
            onBackClick = { navigator.goBack() },
            onNavigateToMyPage = {
                navigator.resetTo(MyPage)
            },
            isNewRecord = isNewRecord,
            isUserPageEntry = isUserPageEntry,
            onNavigateToRecordRoutine = { data, mode -> navigator.navigate(RecordRoutine(modifyData = data, modifyMode = mode)) },
            onNavigateToHome = { navigator.resetTo(Home) },
            onNavigateToUserPage = { userId, recordAuthor -> navigator.navigate(UserPage(userId, recordAuthor)) },
            onReportClick = {
                val details = routineDetailViewModel.uiState.value.detail.routine
                navigator.navigate(
                    Register(
                        recordId = details?.recordId ?: 0,
                        nickname = details?.writerNickname ?: "",
                    )
                )
            },
            onSaveCardClick = {
                val details = routineDetailViewModel.uiState.value.detail.routine
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
        ReportScreenRoot(
            recordId = backStackEntry.recordId,
            nickname = backStackEntry.nickname,
            onBack = { navigator.goBack() },
            onComplete = { navigator.goBack() },
            onNavigateToSosik = { navigator.resetTo(Sosik) },
            userId = backStackEntry.userId,
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

    nonTabEntry<ProfileSetting> { backStackEntry ->
        ProfileSettingScreenRoot(
            initialProfileImageUrl = backStackEntry.profileImageUrl,
            initialNickname = backStackEntry.currentNickname,
            goBack = {
                myPageViewModel.getUserInfo()
                myPageViewModel.getNickname()
                navigator.goBack()
            },
        )
    }

    nonTabEntry<HobbyPhotoSetting> {
        HobbyPhotoManagementScreenRoot(
            onBackClick = { navigator.goBack() },
            onCompleteClick = { navigator.goBack() }
        )
    }

    nonTabEntry<UserPage> { backStackEntry ->
        val userId = backStackEntry.userId
        val recordAuthor = backStackEntry.recordAuthor
        val userPageViewModel: MyPageViewModel = hiltViewModel()
        MyPageScreenRoot(
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
