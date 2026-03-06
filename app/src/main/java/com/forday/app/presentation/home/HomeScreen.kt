@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)

package com.forday.app.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.bottomsheet.AiRecommendationBottomSheet
import com.forday.app.core.designsystem.component.dropdown.DropdownItem
import com.forday.app.core.designsystem.component.dropdown.RoutineDropdown
import com.forday.app.core.designsystem.component.dropdown.SettingsDropdown
import com.forday.app.core.designsystem.dialog.RoutineOnlyOneHaveDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.home.component.FloatingMenuPopup
import com.forday.app.presentation.home.model.HomeState
import kotlinx.coroutines.delay
import timber.log.Timber

import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState

enum class SettingsMenuItem(val label: String) {
    MY_HOBBY_MANAGEMENT("내 취미관리"),
    ADD_HOBBY("취미 추가"),  // 활동리스트 관리 -> 취미 추가로 변경
    ALL_SETTINGS("전체설정")
}

fun getStickerDrawable(stickerUrl: String?): Int {
    return when {
        stickerUrl?.contains("smile.jpg") == true -> R.drawable.ic_sticker_smile
        stickerUrl?.contains("sad.jpg") == true -> R.drawable.ic_sticker_sad
        stickerUrl?.contains("laugh.jpg") == true -> R.drawable.ic_sticker_laugh
        stickerUrl?.contains("angry.jpg") == true -> R.drawable.ic_sticker_angry
        stickerUrl == null -> R.drawable.ic_main_character2
        else -> R.drawable.ic_home_icon
    }
}

@Composable
fun HomeScreenRoot(
    onRoutineCreate: (Long?, Boolean?, String?) -> Unit,
    onModifyRoutine: (Long?, String?) -> Unit,
    onRecordRoutine: (Long?, String, String?, String?) -> Unit,
    onMoveRecordedRoutine: (Int) -> Unit,
    onModifyHobby: () -> Unit,
    onSelectHobby: () -> Unit,
    onAllSettingsClick: () -> Unit,
    onAddHobbyClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCurrentHobbyIdChanged: (Long?) -> Unit = {},
    onCurrentHobbyInfoChanged: (hobbyName: String?, activityName: String?) -> Unit = { _, _ -> },
    onRecordStateChanged: (isRecordedToday: Boolean, todayRecordId: Int?) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentHobbyId = state.inProgressHobbies.find { it.isCurrent }?.hobbyId
    var showAlreadyRecordedDialog by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var errorToastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.fetchHomeHobbyData()
            viewModel.logEvent(AnalyticsEvents.HOME_SCREEN)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is HomeSideEffect.CreateRoutinesSuccess -> {
                        toastMessage = effect.message
                    }
                    is HomeSideEffect.CreateRoutinesError -> {
                        errorToastMessage = effect.message
                    }
                }
            }
        }
    }
    Timber.e("@@@@@@@@@@@@@@@ aiCallRemainingCount : "+state.aiCallRemainingCount+", "+state.aiCallRemaining)
    LaunchedEffect(currentHobbyId) {
        onCurrentHobbyIdChanged(currentHobbyId)
        if (currentHobbyId != null) {
            Timber.e("@#@#@#@#@###@#@ "+currentHobbyId)
            viewModel.fetchSpecificRoutineList(currentHobbyId, 5)
            viewModel.fetchStickerHistory(currentHobbyId, 28, null)
        }
        viewModel.getUserNickname()
    }

    val isRecordedToday = state.stickerInfo?.activityRecordedToday == true &&
            state.stickers.lastOrNull()?.deleted != true
    val todayRecordId = state.stickers.lastOrNull()?.activityRecordId

    LaunchedEffect(isRecordedToday, todayRecordId) {
        onRecordStateChanged(isRecordedToday, todayRecordId)
    }

    LaunchedEffect(state.routineId) {
        viewModel.fetchHomeHobbyData(currentHobbyId)
        viewModel.fetchStickerHistory(hobbyId = currentHobbyId, size = 28, page = null)
    }

    val currentHobbyName = state.inProgressHobbies.find { it.isCurrent }?.name
    val currentActivityName = state.routinePreview?.content

    LaunchedEffect(currentHobbyName, currentActivityName) {
        onCurrentHobbyInfoChanged(currentHobbyName, currentActivityName)
    }

    Box(modifier = modifier.fillMaxSize()) {
        HomeScreen(
            onRoutineCreate = { aiCallRemaining ->
                viewModel.logEvent(AnalyticsEvents.HOME_ADD_HOBBY_ACTIVITY)
                onRoutineCreate(currentHobbyId, aiCallRemaining, currentHobbyName)
            },
            onShowRoutineList = {
                viewModel.logEvent(AnalyticsEvents.HOME_SHOW_HOBBY_ACTIVITY_LIST)
                onModifyRoutine(currentHobbyId, currentHobbyName)
            },
            onRoutineSelected = { routineId ->
                viewModel.selectRoutine(routineId)
            },
            onRecordRoutine = { entryPoint ->
                viewModel.logEvent(AnalyticsEvents.HOME_CLICK_EMPTY_STICKER)
                val lastSticker = state.stickers.lastOrNull()
                val isLastStickerDeleted = lastSticker?.deleted == true
                if (state.stickerInfo?.activityRecordedToday == true && !isLastStickerDeleted) {
                    showAlreadyRecordedDialog = true // 이미 활동 기록했다는 팝업 표시
                } else {
                    onRecordRoutine(currentHobbyId, entryPoint, currentHobbyName, state.routinePreview?.content)
                }

            },
            onMoveRecordedRoutine = onMoveRecordedRoutine,
            onSettingsItemClick = { menuItem ->
                when (menuItem) {
                    SettingsMenuItem.MY_HOBBY_MANAGEMENT -> onModifyHobby()
                    SettingsMenuItem.ADD_HOBBY -> onSelectHobby()
                    SettingsMenuItem.ALL_SETTINGS -> onAllSettingsClick()
                }
            },
            onStickerPageNext = viewModel::nextStickerPage,
            onStickerPagePrevious = viewModel::previousStickerPage,
            onAddHobbyClick = onAddHobbyClick,
            onCurrentHobbyClick = { hobbyId ->
                viewModel.fetchHomeHobbyData(hobbyId)
                viewModel.fetchStickerHistory(hobbyId, 28, null)
            },
            onOtherHobbyClick = { hobbyId ->
                viewModel.fetchHomeHobbyData(hobbyId)
                viewModel.fetchStickerHistory(hobbyId, 28, null)
            },
            modifier = Modifier.fillMaxSize(),
            toastMessage = toastMessage,
            onDismissToast = { toastMessage = null },
            onToastAction = { onModifyRoutine(currentHobbyId, currentHobbyName) },
            errorToastMessage = errorToastMessage,
            onDismissErrorToast = { errorToastMessage = null },
            state = state,
            currentHobbyId = currentHobbyId,
            onCreateRoutine = {
                viewModel.logEvent(AnalyticsEvents.activityAddEntryClicked("home_fab", currentHobbyName))
                onRoutineCreate(currentHobbyId, state.aiCallRemaining, currentHobbyName)
            },
            viewModel = viewModel
        )
        if (showAlreadyRecordedDialog) {
            RoutineOnlyOneHaveDialog(
                onDismiss = { showAlreadyRecordedDialog = false },
                onViewRecords = {
                    showAlreadyRecordedDialog = false
                    // ✅ 오늘 기록한 내용으로 이동 (마지막 스티커 또는 오늘 날짜의 기록)
                    val todayRecordId = state.stickers.lastOrNull()?.activityRecordId
                    if (todayRecordId != null) {
                        onMoveRecordedRoutine(todayRecordId)
                    }
                }
            )
        }
    } // Box
}

@Composable
fun HomeScreen(
    onRoutineCreate: (Boolean?) -> Unit,
    onShowRoutineList: () -> Unit,
    onRoutineSelected: (Int) -> Unit,
    onRecordRoutine: (String) -> Unit,
    onMoveRecordedRoutine: (Int) -> Unit,
    onSettingsItemClick: (SettingsMenuItem) -> Unit,
    onStickerPageNext: () -> Unit,
    onStickerPagePrevious: () -> Unit,
    onAddHobbyClick: () -> Unit,
    onCurrentHobbyClick: (Long) -> Unit,
    onOtherHobbyClick: (Long?) -> Unit,
    onCreateRoutine: () -> Unit,
    toastMessage: String?,
    onDismissToast: () -> Unit,
    onToastAction: () -> Unit,
    errorToastMessage: String? = null,
    onDismissErrorToast: () -> Unit = {},
    modifier: Modifier = Modifier,
    state: HomeState,
    currentHobbyId: Long?,
    viewModel: HomeViewModel,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    var refreshTriggered by remember { mutableStateOf(false) }
    var refreshStartStickerSize by remember { mutableStateOf(0) }
    var refreshStartMillis by remember { mutableStateOf(0L) }

    val listState = rememberLazyListState()

    var settingsIconBottomPx by remember { mutableFloatStateOf(0f) }
    var containerTopPx by remember { mutableFloatStateOf(0f) }

    var showFloatingMenu by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }
    var showSettingsDropdown by remember { mutableStateOf(false) }
    var showAiBottomSheet by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    var routineDropdownAnchorBottomPx by remember { mutableFloatStateOf(0f) }
    var routineActionButtonBottomPx by remember { mutableFloatStateOf(0f) }
    var homeHeaderBottomPx by remember { mutableFloatStateOf(0f) }

    val currentHobbyName = state.inProgressHobbies.find { it.isCurrent }?.name ?: ""

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
        }
    )

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            refreshTriggered = true
            refreshStartStickerSize = state.stickers.size
            refreshStartMillis = System.currentTimeMillis()
            viewModel.fetchHomeHobbyData(currentHobbyId)
            viewModel.fetchStickerHistory(currentHobbyId, 28, null)

            // Safety: ensure the indicator never spins forever
            delay(4_000L)
            if (isRefreshing) {
                isRefreshing = false
                refreshTriggered = false
                refreshStartMillis = 0L
            }
        }
    }

    // When refresh ends, return the list to the top so the UI snaps back naturally.
    LaunchedEffect(isRefreshing, refreshTriggered) {
        if (!isRefreshing && !refreshTriggered) {
            listState.animateScrollToItem(0)
        }
    }

    // Home 화면은 기본적으로 고정 레이아웃 UX라, 사용자가 아래→위로 드래그해도
    // 뷰가 위로 올라간 채로 남지 않도록 스크롤 종료 시 항상 상단으로 복귀시킵니다.
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && !isRefreshing) {
            val shouldSnapBack = listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
            if (shouldSnapBack) {
                listState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(state.isLoading, state.stickers.size, refreshTriggered) {
        val stickerUpdated = state.stickers.size != refreshStartStickerSize
        val timedOut = refreshStartMillis != 0L && (System.currentTimeMillis() - refreshStartMillis) > 3_000L

        if (refreshTriggered && ((!state.isLoading && stickerUpdated) || timedOut)) {
            isRefreshing = false
            refreshTriggered = false
            refreshStartMillis = 0L
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .onGloballyPositioned { containerTopPx = it.positionInRoot().y }
    ) {
        Image(
            painter = painterResource(id = R.drawable.mainframe),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        HomeHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .onGloballyPositioned { coordinates ->
                                    homeHeaderBottomPx = coordinates.boundsInRoot().bottom
                                }
                                .padding(horizontal = 20.dp),
                            state = state,
                            onSettingsClick = {
                                showSettingsDropdown = !showSettingsDropdown
                                if (showSettingsDropdown) showDropdown = false
                            },
                            onAddHobbyClick = onAddHobbyClick,
                            onCurrentHobbyClick = onCurrentHobbyClick,
                            onOtherHobbyClick = onOtherHobbyClick,
                            onSettingsIconBottomChanged = { bottomPx ->
                                settingsIconBottomPx = bottomPx
                            }
                        )

                        Spacer(modifier = Modifier.height(68.dp))

                        MyHobbySection(
                            state = state,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            onRoutineCreate = {
                                viewModel.logEvent(AnalyticsEvents.activityAddEntryClicked("home_fab", state.inProgressHobbies.find { it.isCurrent }?.name))
                                onRoutineCreate(state.aiCallRemaining)
                            },
                            onRoutineSelected = onRoutineSelected,
                            onRecordRoutine = onRecordRoutine,
                            showDropdown = showDropdown,
                            onDropdownToggle = {
                                showDropdown = !showDropdown
                                if (showDropdown) showSettingsDropdown = false
                            },
                            onShowRoutineList = onShowRoutineList,
                            onRoutineDropdownAnchorBottomChanged = { bottomPx ->
                                routineDropdownAnchorBottomPx = bottomPx
                            },
                            onRoutineActionButtonBottomChanged = { bottomPx ->
                                routineActionButtonBottomPx = bottomPx
                            },
                            onAddHobbyClick = onAddHobbyClick
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        StickerBottomSheet(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            state = state,
                            onCreateRoutine = onCreateRoutine,
                            onStickerClick = onMoveRecordedRoutine,
                            onPageNext = onStickerPageNext,
                            onPagePrevious = onStickerPagePrevious,
                            onRecordRoutine = onRecordRoutine
                        )
                    }

                    FloatingSettingsButton(
                        onShowAiRecommendBottomSheet = { showAiBottomSheet = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 20.dp)
                            .padding(top = 62.dp),
                        state = state
                    )

                    FloatingBottomButton(
                        isExpanded = showFloatingMenu,
                        onClick = { showFloatingMenu = !showFloatingMenu },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 16.dp),
                        isHobbyEmpty = state.inProgressHobbies.isEmpty()
                    )
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (showDropdown && state.routineList.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = rememberThrottledClick { showDropdown = false }
                    )
            ) {
                val gapPx = with(density) { 8.dp.toPx() }
                val dropdownTopPx = routineDropdownAnchorBottomPx + gapPx
                val minHeightDp = with(density) {
                    (routineActionButtonBottomPx - dropdownTopPx).coerceAtLeast(0f).toDp()
                }
                val dropdownMaxHeightDp = if (minHeightDp > 197.dp) minHeightDp else 197.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = with(density) { routineDropdownAnchorBottomPx.toDp() } + 8.dp)
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.TopCenter
                ) {
                    RoutineDropdown(
                        items = state.routineList.map { routine ->
                            DropdownItem(
                                text = routine.content,
                                hasAiIcon = routine.isAiRecommended,
                                isSelected = routine.routineId == state.routinePreview?.routineId
                            )
                        },
                        onItem = { index ->
                            val selectedRoutine = state.routineList[index]
                            onRoutineSelected(selectedRoutine.routineId)
                            showDropdown = false
                        },
                        modifier = Modifier
                            .shadow(
                                elevation = 12.dp,
                                spotColor = Color(0x1F000000),
                                ambientColor = Color(0x1F000000)
                            )
                            .width(210.dp)
                            .heightIn(max = dropdownMaxHeightDp)
                    )
                }
            }
        }

        if (showSettingsDropdown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = rememberThrottledClick { showSettingsDropdown = false }
                    )
            )

            val inProgressHobbyCount = state.inProgressHobbies.size
            val settingsMenuItems = remember(inProgressHobbyCount) {
                SettingsMenuItem.values().filter { item ->
                    item != SettingsMenuItem.ADD_HOBBY || inProgressHobbyCount <= 1
                }
            }
            val dropdownTopDp = with(density) { (settingsIconBottomPx - containerTopPx).toDp() } + 8.dp
            SettingsDropdown(
                items = settingsMenuItems,
                onItemClick = { menuItem ->
                    onSettingsItemClick(menuItem)
                    showSettingsDropdown = false
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp)
                    .offset(y = dropdownTopDp)
            )
        }

        if (showFloatingMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = rememberThrottledClick { showFloatingMenu = false }
                    )
            ) {
                FloatingMenuPopup(
                    onAddActivity = {
                        showFloatingMenu = false
                        viewModel.logEvent(AnalyticsEvents.activityAddEntryClicked("home_fab", state.inProgressHobbies.find { it.isCurrent }?.name))
                        onRoutineCreate(state.aiCallRemaining)
                    },
                    onShowActivityList = {
                        showFloatingMenu = false
                        onShowRoutineList()
                    },
                    onDismiss = { showFloatingMenu = false },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 84.dp)
                )
            }
        }

        if (showAiBottomSheet && !state.inProgressHobbies.isEmpty()) {
            AiRecommendationBottomSheet(
                showBottomSheet = showAiBottomSheet,
                userName = state.nickName ?: "포비",
                hobbyName = currentHobbyName,
                onDismiss = {
                    showAiBottomSheet = false
                    viewModel.fetchHomeHobbyData(currentHobbyId)
                },
                onAiRecommendButtonClick = { viewModel.getAiRecommendedRoutines(currentHobbyId) },
                aiRecommendData = state.aiRoutineList,
                aiRoutineLoaded = state.aiRoutineLoaded,
                aiCallCount = state.aiCallCount,
                aiCallRemainingCount = state.aiCallRemainingCount,
                userSummaryText = state.userSummaryText,
                recommendedText = state.recommendedText,
                toastMessage = toastMessage,
                onDismissToast = onDismissToast,
                onToastAction = onToastAction,
                errorToastMessage = errorToastMessage,
                onDismissErrorToast = onDismissErrorToast,
                onPreviousRecommendClick = { viewModel.getAiRecommendedRoutinesAgain(currentHobbyId) },
                onRecommendationsSelected = { routines ->
                    val routinesList = routines.filter { it.title.isNotBlank() }
                        .map { Pair(true, it.title) }
                    viewModel.createRoutines(currentHobbyId, routinesList, currentHobbyName)
                },
                onAiRecommendationShown = {
                    viewModel.logEvent(AnalyticsEvents.aiRecommendationShown(currentHobbyName, state.aiCallCount))
                },
                onAiRecommendationClicked = { activityName, position ->
                    viewModel.logEvent(AnalyticsEvents.aiRecommendationClicked(currentHobbyName, activityName, position))
                }
            )
        }
    }
}

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
    state: HomeState,
    onSettingsClick: () -> Unit,
    onAddHobbyClick: () -> Unit,
    onCurrentHobbyClick: (Long) -> Unit,
    onOtherHobbyClick: (Long?) -> Unit,
    onSettingsIconBottomChanged: (Float) -> Unit = {}
) {
    val currentHobby = state.inProgressHobbies.find { it.isCurrent }
    val otherHobby = state.inProgressHobbies.find { !it.isCurrent }

    Row(
        modifier = modifier.padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.inProgressHobbies.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        onClick = rememberThrottledClick { onAddHobbyClick() },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                ) {
                    Text(
                        text = "취미 추가",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForDayTheme.color.Gray800
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.icon_chevron_right),
                        contentDescription = "취미 추가",
                        modifier = Modifier.size(24.dp),
                        tint = ForDayTheme.color.Gray800
                    )
                }
            } else {
                Text(
                    text = state.inProgressHobbies.getOrNull(0)?.name ?: "",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForDayTheme.color.Gray800,
                    modifier = Modifier.clickable(
                        onClick = rememberThrottledClick { currentHobby?.hobbyId?.let { onCurrentHobbyClick(it) } },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                )

                if (otherHobby != null) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(12.dp)
                            .background(ForDayTheme.color.Gray500)
                            .rotate(90f)
                    )

                    Text(
                        text = state.inProgressHobbies.getOrNull(1)?.name ?: "",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForDayTheme.color.Gray500,
                        modifier = Modifier.clickable(
                            onClick = rememberThrottledClick { onOtherHobbyClick(otherHobby.hobbyId) },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Box {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_notification),
//                    contentDescription = "알림",
//                    modifier = Modifier.size(24.dp),
//                    tint = Color(0xFF1E1E1E)
//                )
//                Box(
//                    modifier = Modifier
//                        .size(4.dp)
//                        .align(Alignment.TopEnd)
//                        .background(Color(0xFFEE5D50), CircleShape)
//                )
//            }

            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "설정",
                modifier = Modifier
                    .size(24.dp)
                    .onGloballyPositioned { coordinates ->
                        onSettingsIconBottomChanged(coordinates.boundsInRoot().bottom)
                    }
                    .clickable(
                        onClick = rememberThrottledClick { onSettingsClick() },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ),
                tint = Color(0xFF1E1E1E)
            )
        }
    }
}

@Composable
fun MyHobbySection(
    state: HomeState,
    onRoutineCreate: () -> Unit,
    onRoutineSelected: (Int) -> Unit,
    onRecordRoutine: (String) -> Unit,
    showDropdown: Boolean,
    onDropdownToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onShowRoutineList: () -> Unit,
    onRoutineDropdownAnchorBottomChanged: (Float) -> Unit,
    onRoutineActionButtonBottomChanged: (Float) -> Unit,
    onAddHobbyClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = rememberThrottledClick {
                        if (!state.inProgressHobbies.isEmpty()) {
                            onShowRoutineList()
                        }
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "나의 취미활동",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )
            Icon(
                painter = painterResource(id = R.drawable.icon_chevron_right),
                contentDescription = "더보기",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF3A3A3A)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(
                                onClick = rememberThrottledClick { onDropdownToggle() },
                                enabled = state.routineList.isNotEmpty(),
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.routinePreview?.isAiRecommended == true) {
                            Icon(
                                painter = painterResource(R.drawable.ic_ai_list),
                                contentDescription = "AI",
                                modifier = Modifier.size(14.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = when {
                                state.inProgressHobbies.isEmpty() -> "등록된 취미가 없어요."
                                state.routinePreview?.routineId == null -> "등록된 취미활동이 없어요."
                                else -> state.routinePreview?.content.orEmpty()
                            },
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                onRoutineDropdownAnchorBottomChanged(coordinates.boundsInRoot().bottom)
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            lineHeight = 19.6.sp,
                            color = if (state.inProgressHobbies.isEmpty() || state.routinePreview?.routineId == null) ForDayTheme.color.Neutral600 else ForDayTheme.color.Neutral900
                        )

                        if (state.routineList.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_down),
                                contentDescription = "드롭다운",
                                modifier = Modifier.size(16.dp),
                                tint = ForDayTheme.color.Neutral600
                            )
                        }
                    }

                    Button(
                        onClick = {
                            when {
                                state.inProgressHobbies.isEmpty() -> onAddHobbyClick()
                                state.routinePreview?.routineId == null -> onRoutineCreate()
                                else -> onRecordRoutine("sticker_cta")
                            }
                        },
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                onRoutineActionButtonBottomChanged(coordinates.boundsInRoot().bottom)
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                            .then(
                                if (!state.inProgressHobbies.isEmpty() && state.routinePreview?.routineId != null) {
                                    Modifier.background(
                                        brush = ForDayTheme.gradients.gradient002,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Modifier.background(
                                        color = ForDayTheme.color.Primary03,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 40.dp)
                    ) {
                        Text(
                            text = when {
                                state.inProgressHobbies.isEmpty() -> "취미 추가하기"
                                state.routinePreview?.routineId == null -> "취미활동 추가하기"
                                else -> "오늘의 스티커 붙이기"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700,
                            lineHeight = 16.8.sp,
                            color = if (state.inProgressHobbies.isEmpty() || state.routinePreview?.routineId == null) Color(0xFFFF9447) else Color(0xFFFFFFFF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StickerBottomSheet(
    modifier: Modifier = Modifier,
    state: HomeState,
    onCreateRoutine: () -> Unit,
    onStickerClick: (Int) -> Unit,
    onRecordRoutine: (String) -> Unit,
    onPageNext: () -> Unit,
    onPagePrevious: () -> Unit
) {
    val currentPage = state.currentStickerPage
    val canGoPrevious = state.stickerInfo?.hasPrevious == true
    val canGoNext = state.stickerInfo?.hasNext == true

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp)
                .padding(top = 40.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "현재까지 ${state.stickerInfo?.totalStickerNum ?: 0}개의 스티커 수집",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                onClick = rememberThrottledClick { onPagePrevious() },
                                enabled = canGoPrevious,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_chevron_right),
                            contentDescription = "이전 페이지",
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(180f),
                            tint = if (canGoPrevious) ForDayTheme.color.Neutral600 else Color(0xFFB5B5B5)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                onClick = rememberThrottledClick { onPageNext() },
                                enabled = canGoNext,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_chevron_right),
                            contentDescription = "다음 페이지",
                            modifier = Modifier.size(16.dp),
                            tint = if (canGoNext) ForDayTheme.color.Neutral600 else Color(0xFFB5B5B5)
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E5E5))
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "sticker_page_animation"
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        repeat(4) { rowIndex ->
                            StickerRow(
                                rowIndex = rowIndex,
                                currentPage = page,
                                stickers = state.stickers,
                                activityRecordedToday = state.stickerInfo?.activityRecordedToday ?: false,
                                onCreateRoutine = onCreateRoutine,
                                onStickerClick = onStickerClick,
                                state = state,
                                onRecordRoutine = onRecordRoutine
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StickerRow(
    rowIndex: Int,
    state: HomeState,
    currentPage: Int,
    stickers: List<StickerUiModel>,
    activityRecordedToday: Boolean,
    onCreateRoutine: () -> Unit,
    onRecordRoutine: (String) -> Unit,
    onStickerClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(7) { columnIndex ->
            val localIndex = rowIndex * 7 + columnIndex
            val sticker = stickers.getOrNull(localIndex)

            val isFilledSticker = sticker != null
            val isEmptySticker = sticker == null

            val imageRes = when {
                state.inProgressHobbies.isEmpty() -> R.drawable.ic_main_character2
                sticker != null -> getStickerDrawable(sticker.sticker)
                localIndex == stickers.size -> {
                    if (activityRecordedToday) R.drawable.ic_main_character2 else R.drawable.ic_empty_sticker
                }
                else -> R.drawable.ic_main_character2
            }

            // ic_main_character2인 경우 클릭 불가능
            val isClickable = imageRes != R.drawable.ic_main_character2 && (isFilledSticker || isEmptySticker)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "스티커 $localIndex",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = rememberThrottledClick {
                                if (isFilledSticker) {
                                    onStickerClick(sticker?.activityRecordId ?: return@rememberThrottledClick)
                                } else if (isEmptySticker && state.routinePreview != null) {
                                    onRecordRoutine("empty_sticker")
//                                    onStickerClick(state.routinePreview.routineId)
                                    Timber.e("@#@#@#@#@# routineId : "+state.routinePreview?.routineId+", "+state.routinePreview?.content)
                                }
                                else {
                                    Timber.e("@#@#@#@#@#"+isEmptySticker)
                                    onCreateRoutine()
                                }
                            },
                            enabled = isClickable,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun FloatingBottomButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHobbyEmpty: Boolean = false
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        label = "rotation"
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0x26000000),
                ambientColor = Color(0x26000000),
                shape = CircleShape
            )
            .padding(1.dp)
            .size(52.dp)
            .background(color = if (isHobbyEmpty) Color(0xCCB5B5B5) else Color(0xCC000000), shape = CircleShape)
            .clickable(
                onClick = rememberThrottledClick { onClick() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation)
                .drawBehind {
                    val strokeWidth = 4.dp.toPx()
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val halfLength = size.minDimension / 2 - strokeWidth

                    drawLine(
                        color = Color.White,
                        start = Offset(centerX - halfLength, centerY),
                        end = Offset(centerX + halfLength, centerY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(centerX, centerY - halfLength),
                        end = Offset(centerX, centerY + halfLength),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
        )
    }
}

@Composable
fun FloatingSettingsButton(
    onShowAiRecommendBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
    state: HomeState
) {
    var isExpanded by remember { mutableStateOf(false) }
    var currentMessageIndex by remember { mutableStateOf(0) }
    var resumeTrigger by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                resumeTrigger = !resumeTrigger
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val maxWidth = (LocalConfiguration.current.screenWidthDp.dp - 40.dp).coerceAtLeast(40.dp)

    val animatedWidth by animateDpAsState(
        targetValue = if (isExpanded) maxWidth else 40.dp,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "floating_settings_width"
    )
    val animatedHorizontalPadding by animateDpAsState(
        targetValue = if (isExpanded) 16.dp else 8.dp,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "floating_settings_padding"
    )

    val messages = remember(state.greetingMessage, state.userSummaryText, state.recommendMessage) {
        buildList {
            add(state.greetingMessage.ifBlank { "반가워요!" })
            if (state.userSummaryText.isNotBlank()) add(state.userSummaryText)
            add(state.recommendMessage.ifBlank { "AI 추천을 받아보세요!" })
        }
    }

    LaunchedEffect(messages, resumeTrigger) {
        currentMessageIndex = 0
        isExpanded = false
        delay(2000)
        isExpanded = true
        repeat(messages.size - 1) { index ->
            delay(2000)
            currentMessageIndex = index + 1
        }
    }

    Box(
        modifier = modifier
            .height(40.dp)
            .width(animatedWidth)
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .border(width = 1.dp, brush = ForDayTheme.gradients.gradient002, shape = RoundedCornerShape(20.dp))
            .clickable(
                onClick = rememberThrottledClick { onShowAiRecommendBottomSheet() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(horizontal = animatedHorizontalPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(220)) +
                        slideInHorizontally(animationSpec = tween(320, easing = FastOutSlowInEasing)) { it / 3 },
                exit = fadeOut(animationSpec = tween(160)) +
                        slideOutHorizontally(animationSpec = tween(240, easing = FastOutSlowInEasing)) { it / 3 }
            ) {
                AnimatedContent(
                    targetState = messages.getOrNull(currentMessageIndex) ?: messages.firstOrNull() ?: "반가워요!",
                    transitionSpec = { fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300)) },
                    label = "message_transition"
                ) { message ->
                    var allowAutoSize by remember(message) { mutableStateOf(false) }

                    LaunchedEffect(message, isExpanded) {
                        allowAutoSize = false
                        if (isExpanded) {
                            delay(360)
                            allowAutoSize = true
                        }
                    }

                    val minFontSize = if (message.length <= 12) 14.sp else 8.sp
                    var autoFontSize by remember(message, allowAutoSize) { mutableStateOf(14.sp) }

                    Text(
                        text = message,
                        fontSize = autoFontSize,
                        lineHeight = (autoFontSize.value * 1.2f).sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF3A3A3A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        onTextLayout = { result ->
                            if (!allowAutoSize) return@Text
                            if ((result.didOverflowWidth || result.didOverflowHeight) && autoFontSize > minFontSize) {
                                autoFontSize = (autoFontSize.value - 1f).sp
                            }
                        }
                    )
                }
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.icon_ai),
            contentDescription = "AI",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp),
            tint = Color.Unspecified
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenRoot(
        onRoutineCreate = { _, _, _ -> },
        onModifyRoutine = {} as (Long?, String?) -> Unit,
        onRecordRoutine = { _, _, _, _ -> },
        onMoveRecordedRoutine = {},
        onModifyHobby = {},
        onAllSettingsClick = {},
        onAddHobbyClick = {},
        onSelectHobby = TODO(),
        viewModel = TODO(),
        modifier = TODO()
    )
}
