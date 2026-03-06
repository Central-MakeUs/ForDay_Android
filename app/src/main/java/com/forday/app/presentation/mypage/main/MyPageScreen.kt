package com.forday.app.presentation.mypage.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.mypage.MyPageViewModel
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.bottomsheet.HintBubble
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import timber.log.Timber

// 색상 정의
object MyPageColors {
    val Neutral900 = Color(0xFF1E1E1E)
    val Neutral800 = Color(0xFF3A3A3A)
    val Neutral600 = Color(0xFF7A7A7A)
    val Neutral500 = Color(0xFF9E9E9E)
    val Neutral400 = Color(0xFFB5B5B5)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Stroke001 = Color(0xFFE5E5E5)
    val Background001 = Color(0xFFFFFFFF)

    // 그라디언트 색상
    val BlueGradientStart = Color(0xFFC9DBFF)
    val BlueGradientMiddle = Color(0xFF8FB3FF)
    val BlueGradientEnd = Color(0xFFC9DBFF)

    val GreenGradientStart = Color(0xFFDDF2D8)
    val GreenGradientMiddle = Color(0xFFA8D8A2)
    val GreenGradientEnd = Color(0xFFDDF2D8)

    val OrangeGradientStart = Color(0xFFFFE6D1)
    val OrangeGradientEnd = Color(0xFFF4A261)

    // 카드 오버레이 그라디언트
    val CardOverlayGradient = listOf(
        Color(0x00000000), // 투명
        Color(0x5C000000), // 36% 검정
        Color(0x99000000)  // 60% 검정
    )
}

data class HobbyCard(
    val title: String,
    val imageUrl: String,
    val rotation: Float = 0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel,
    onProfileSetting: () -> Unit,
    onHobbyPhotoManagement: () -> Unit,
    onAllSettingsClick: () -> Unit,
    onRoutineFeedClick: (Int) -> Unit,
    onAddHobbyClick: () -> Unit,
    onNavigateToRecordRoutine: (Int?) -> Unit,
    onDismiss: () -> Unit,  //바텀시트(로그인) x버튼 눌렀을 때
    onBackClick: () -> Unit = {},
    onNavigateToSosik: () -> Unit = {},
    onReportUserClick: () -> Unit = {},
    userId: String? = null,
    recordAuthor: Boolean = true,
    isUserPageEntry: Boolean = false,
) {
    val context = LocalContext.current.applicationContext
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSettingsMenu by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var settingsButtonBottomPx by remember { mutableFloatStateOf(0f) }
    var containerTopPx by remember { mutableFloatStateOf(0f) }
    var selectedTab by remember { mutableStateOf(0) }
    state.userHobbyTabUiModel?.hobbyItems?.map { it.hobbyId }
    // 선택된 취미 ID들을 상위에서 관리 (무한 스크롤 시에도 같은 필터 유지)
    var selectedHobbyIds by remember { mutableStateOf<Set<Int?>>(emptySet()) }
    state.userHobbyTabUiModel?.hobbyItems?.map { it.hobbyName }
    // 스크롤 상태
    val scrollState = rememberScrollState()

    // 로딩 상태 (추가 로딩 중인지)
    var isLoadingMore by remember { mutableStateOf(false) }
    // 초기 로딩 상태: 핵심 데이터가 모두 도착할 때까지 스켈레톤 표시
    val isInitialLoading = state.userInfo == null || state.userHobbyTabUiModel == null || state.userFeedUiModel == null
    // 차단 확인 다이얼로그
    var showBlockDialog by remember { mutableStateOf(false) }
    // 바텀 시트 상태 - socialType이 "GUEST"일 때만 표시
    var showGuestBottomSheet by remember { mutableStateOf(false) }
    var dismissedByUser by remember { mutableStateOf(false) }

    // 게스트 체크 - 최초 접속 시에만 바텀 시트 표시
    LaunchedEffect(state.socialType, state.hasShownGuestBottomSheet) {
        // 최초 1회만 체크 (ViewModel에서 관리하는 플래그 사용)
        if (!state.hasShownGuestBottomSheet && state.socialType != null) {
            if (state.socialType == "GUEST" && !dismissedByUser) {
                showGuestBottomSheet = true
                viewModel.markGuestBottomSheetShown()  // ViewModel에 표시했음을 기록
            }
        }

        // 이후 게스트가 아니게 되면 닫기 (로그인 완료 시)
        if (state.socialType != null && state.socialType != "GUEST") {
            showGuestBottomSheet = false
        }
    }

    LaunchedEffect(state.blockUserSuccess) {
        if (state.blockUserSuccess) {
            viewModel.resetBlockUserSuccess()
        }
    }

    BackHandler(enabled = state.isBlockedUser && userId != null) {
        onNavigateToSosik()
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 2) {
            viewModel.getUserScrapList(
                lastScrapId = null,
                size = 24,
                userId = userId
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getUserInfo(userId)
        viewModel.getUserLoginInfo()  // 로그인 정보 (소셜 or 게스트)
        viewModel.getUsersProgressHobbyTabs(userId)
        viewModel.getUserFeedList(
            hobbyIds = emptyList(),
            lastRecordId = null,
            feedSize = 24,
            userId = userId
        )
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            viewModel.refresh(selectedTab, selectedHobbyIds.toList(), userId)
        },
        modifier = modifier
            .fillMaxSize()
            .background(MyPageColors.Background001)
            .onGloballyPositioned { coords ->
                containerTopPx = coords.boundsInRoot().top
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            MyPageHeader(
                isMine = recordAuthor,
                isBlockedUser = state.isBlockedUser,
                isUserPageEntry = isUserPageEntry,
                onBackClick = onBackClick,
                onSettingsClick = { showSettingsMenu = !showSettingsMenu },
                onSettingsButtonPositioned = { coords ->
                    settingsButtonBottomPx = coords.boundsInRoot().bottom
                },
                userId = userId,
            )

            if (isInitialLoading) {
                MypageSkeletonContent()
            } else {

                ProfileSection(
                    profileImageUrl = state.userInfo?.profileImageUrl,
                    nickName = state.userInfo?.nickName,
                    totalCollectedStickerCount = state.userInfo?.totalCollectedStickerCount,
                )

                TabSection(
                    inProgressCount = state.userHobbyTabUiModel?.inProgressCount,
                    hobbyCardCount = state.userHobbyTabUiModel?.hobbyCardCount,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    scrapCount = state.scrapCount,
                    socialType = state.socialType
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (state.isBlockedUser) {
                    BlockedUserEmptyState()
                } else when (selectedTab) {
                    0 -> {
                        // ✅ 게스트 여부에 따라 다른 UI 표시
                        if (state.socialType == "GUEST") {
                            GuestInProgressEmptyState(
                                onKakaoLogin = {
                                    viewModel.loginWithKakao(context, "KAKAO")
                                }
                            )
                        } else {
                            // ✅ 로그인 사용자: 활동 기록 여부에 따라 분기
                            if (state.userFeedUiModel?.feedList?.isEmpty() == true) {
                                LoggedInEmptyState(
                                    selectedHobbyIds = selectedHobbyIds,
                                    onHobbySelectionChange = { newSelection ->  // ✅ 전달
                                        selectedHobbyIds = newSelection
                                        viewModel.getUserFeedList(
                                            hobbyIds = newSelection.toList(),
                                            lastRecordId = null,
                                            feedSize = 24,
                                            userId = userId
                                        )
                                    },
                                    hobbyItems = state.userHobbyTabUiModel?.hobbyItems,
                                    onAddHobbyClick = onAddHobbyClick,
                                    onRecordActivity = { hobbyId ->
                                        Timber.d("활동 기록하러 가기 클릭")
                                        onNavigateToRecordRoutine(hobbyId)
                                    },
                                    inProgressCount = state.userHobbyTabUiModel?.inProgressCount
                                )
                            } else {
                                if (state.userFeedUiModel?.totalFeedCount == 0) {
                                    LoggedInEmptyState(
                                        selectedHobbyIds = selectedHobbyIds,
                                        onHobbySelectionChange = { newSelection ->  // ✅ 전달
                                            selectedHobbyIds = newSelection
                                            viewModel.getUserFeedList(
                                                hobbyIds = newSelection.toList(),
                                                lastRecordId = null,
                                                feedSize = 24,
                                                userId = userId
                                            )
                                        },
                                        hobbyItems = state.userHobbyTabUiModel?.hobbyItems,
                                        onAddHobbyClick = onAddHobbyClick,
                                        onRecordActivity = { hobbyId ->
                                            Timber.d("활동 기록하러 가기 클릭")
                                            onNavigateToRecordRoutine(hobbyId)
                                        },
                                        inProgressCount = state.userHobbyTabUiModel?.inProgressCount
                                    )
                                } else {
                                    InProgressTabContent(
                                        hobbyItems = state.userHobbyTabUiModel?.hobbyItems,
                                        feedList = state.userFeedUiModel?.feedList,
                                        viewModel = viewModel,
                                        selectedHobbyIds = selectedHobbyIds,
                                        onHobbySelectionChange = { newSelection ->
                                            selectedHobbyIds = newSelection
                                            viewModel.getUserFeedList(
                                                hobbyIds = newSelection.toList(),
                                                lastRecordId = null,
                                                feedSize = 24,
                                                userId = userId
                                            )
                                        },
                                        onStickerClick = { feedUiModel ->
                                            Timber.d("Sticker clicked: ${feedUiModel.recordId}")
                                            onRoutineFeedClick(feedUiModel.recordId)
                                        },
                                        feedCount = state.userFeedUiModel?.totalFeedCount,
                                        onAddHobbyClick = onAddHobbyClick,
                                        inProgressCount = state.userHobbyTabUiModel?.inProgressCount
                                    )
                                }

                            }
                        }
                    }
                    1 -> HobbyCardTabContent()
                    2 -> ScrapTabContent(
                        scrapListUiModel = state.scrapListUiModel,
                        onScrapItemClick = { scrapItem ->
                            Timber.d("Scrap clicked: ${scrapItem.recordId}")
                            onRoutineFeedClick(scrapItem.recordId)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 로딩 인디케이터
                if (isLoadingMore) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFFFF9447)
                        )
                    }
                }

            } // end else (isInitialLoading)
        }

        // Settings Menu Popup
        if (showSettingsMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showSettingsMenu = false }
                    )
            )
            val popupTopDp = with(density) {
                (settingsButtonBottomPx - containerTopPx).toDp()
            } + 8.dp
            SettingsMenuPopup(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = popupTopDp, end = 20.dp),
                onDismiss = { showSettingsMenu = false },
                onProfileSettingClick = {
                    onProfileSetting()
                    showSettingsMenu = false
                },
                onHobbyPhotoManagementClick = {
                    onHobbyPhotoManagement()
                    showSettingsMenu = false
                },
                onAllSettingsClick = {
                    onAllSettingsClick()
                    showSettingsMenu = false
                },
                socialType = state.socialType,
                isMine = recordAuthor,
                onBlockClick = {
                    showBlockDialog = true
                    showSettingsMenu = false
                },
                onRegisterClick = {
                    showSettingsMenu = false
                    onReportUserClick()
                }
            )
        }
        // 차단 확인 다이얼로그
        if (showBlockDialog) {
            BlockUserConfirmDialog(
                nickname = state.userInfo?.nickName ?: "",
                onDismiss = { showBlockDialog = false },
                onConfirm = {
                    showBlockDialog = false
                    viewModel.blockUser(userId ?: "", state.userInfo?.nickName ?: "")
                }

            )
        }
        // 게스트일 때만 바텀 시트 표시
        if (showGuestBottomSheet) {
            GuestLoginBottomSheet(
                onDismiss = {
                    showGuestBottomSheet = false  // ✅ 먼저 로컬 상태 닫기
                    dismissedByUser = true
                    onDismiss()  // 그 다음 화면 전환
                },
                onKakaoLogin = {
                    showGuestBottomSheet = false  // ✅ 즉시 닫기
                    dismissedByUser = true
                    viewModel.loginWithKakao(context, "KAKAO")
                }
            )
        }
    }
}

@Composable
fun LoggedInEmptyState(
    hobbyItems: List<HobbyUiModel>?,
    selectedHobbyIds: Set<Int?>,
    onHobbySelectionChange: (Set<Int?>) -> Unit,
    onAddHobbyClick: () -> Unit,
    onRecordActivity: (Int?) -> Unit,
    inProgressCount: Int?
) {
    val resolvedHobbyId = if (selectedHobbyIds.isNotEmpty()) {
        selectedHobbyIds.first()
    } else {
        hobbyItems?.firstOrNull { it.status == "IN_PROGRESS" }?.hobbyId
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 취미 카테고리 섹션
        HobbyCategoriesSection(
            feedCount = 0,
            hobbyItems = hobbyItems,
            selectedHobbyIds = selectedHobbyIds,
            onHobbySelectionChange = onHobbySelectionChange,
            onAddHobbyClick = onAddHobbyClick,
            inProgressCount = inProgressCount
        )

        // Empty State UI
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 0개 카운트
            Text(
                text = "0개",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MyPageColors.Neutral500,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            EmptyStateBox(
                title = "활동을 기록해보세요!",
                description = "당신의 활동기록이 궁금해요.",
                topSpacerHeight = 147.dp,
                button = {
                    Button(
                        onClick = { onRecordActivity(resolvedHobbyId) },
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 10.dp)
                            .padding(vertical = 5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9447)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "활동 기록하기",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            color = Color.White,
                            lineHeight = 16.8.sp
                        )
                    }
                }
            )
        }
    }
}




@Composable
fun GuestInProgressEmptyState(
    onKakaoLogin: () -> Unit
) {
    Box(  // ← Column 대신 Box 사용
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp)  // ← 최소 높이 지정으로 충분한 공간 확보
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center  // ← Box의 중앙 정렬
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 말풍선 아이콘
            Image(
                painter = painterResource(id = R.drawable.icon_sad),
                contentDescription = "로그인 필요",
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 메인 메시지
            Text(
                text = "활동 기록은 로그인 이후에\n확인이 가능해요.",
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = MyPageColors.Neutral900,
                textAlign = TextAlign.Center,
                lineHeight = 19.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 서브 메시지
            Text(
                text = "SNS로 시작해보세요!",
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = MyPageColors.Neutral600,
                textAlign = TextAlign.Center,
                lineHeight = 19.6.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // CTA 버튼
            Button(
                onClick = onKakaoLogin,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9447)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "SNS로 시작하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


// 게스트 바텀 시트
// 게스트 바텀 시트 - Dialog 방식으로 변경
@Composable
fun GuestLoginBottomSheet(
    onDismiss: () -> Unit,
    onKakaoLogin: () -> Unit
) {
    Dialog(
        onDismissRequest = { /* 빈 람다 - 여백 클릭 무시 */ },
        properties = DialogProperties(
            dismissOnBackPress = false,  // 뒤로가기 버튼 무시
            dismissOnClickOutside = false,  // 바깥 클릭 무시
            usePlatformDefaultWidth = false  // 전체 너비 사용
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = 564.dp)
                .background(Color.Black.copy(alpha = 0.32f))  // 스크림 배경
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = rememberThrottledClick { onDismiss() }
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            // 바텀 시트 컨텐츠
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = rememberThrottledClick { /* 시트 내부 클릭은 소비만 */ }
                    )
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 헤더
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "닫기",
                            tint = Color(0xFF3A3A3A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 메인 콘텐츠
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 캐릭터 아이콘
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF4A261)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.main_character),
                            contentDescription = "메인 캐릭터",
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 타이틀
                    Text(
                        text = "취미 시작이 어려울 때.\n포데이",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E1E1E),
                        textAlign = TextAlign.Center,
                        lineHeight = 28.8.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 서브타이틀
                    Text(
                        text = "AI 추천으로 쉽게 시작하는 취미생활",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFF25F59),
                        textAlign = TextAlign.Center,
                        lineHeight = 19.6.sp
                    )

                    Spacer(modifier = Modifier.height(100.dp))

                    // 힌트 말풍선
                    HintBubble(text = "SNS로 가볍게 시작하기!")

                    Spacer(modifier = Modifier.height(14.dp))

                    // 카카오 로그인 버튼
                    Button(
                        onClick = onKakaoLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFDE00)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_kakao),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "카카오톡으로 시작하기",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E1E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun MyPageHeader(
    isMine: Boolean = true,
    isBlockedUser: Boolean = false,
    isUserPageEntry: Boolean = false,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSettingsButtonPositioned: (LayoutCoordinates) -> Unit = {},
    userId: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp).padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isUserPageEntry && !isBlockedUser) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_chevron_left),
                    contentDescription = "뒤로가기",
                    tint = MyPageColors.Neutral900
                )
            }
        } else {
            Text(
                text = "마이페이지",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MyPageColors.Neutral900
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            IconButton(
//                onClick = { /* 알림 */ },
//                modifier = Modifier.size(24.dp)
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.icon_notification),
//                    contentDescription = "알림",
//                    tint = MyPageColors.Neutral800
//                )
//            }

            when {
                !isUserPageEntry -> {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(24.dp)
                            .onGloballyPositioned(onSettingsButtonPositioned)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "설정",
                            tint = MyPageColors.Neutral800
                        )
                    }
                }
                isUserPageEntry && !isMine -> {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(24.dp)
                            .onGloballyPositioned(onSettingsButtonPositioned)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_kebab),
                            contentDescription = "더보기",
                            tint = MyPageColors.Neutral800
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(
    profileImageUrl: String?,
    nickName: String?,
    totalCollectedStickerCount: Int?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp).padding(top = 20.dp).padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MyPageColors.Stroke001),
                contentAlignment = Alignment.Center
            ) {
                if (!profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "사용자 프로필 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_profile_empty),
                        error = painterResource(id = R.drawable.ic_profile_empty)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_empty),
                        contentDescription = "사용자 프로필 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = nickName ?: "사용자",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyPageColors.Neutral900
                )

                Text(
                    text = "${totalCollectedStickerCount ?: 0}개 스티커 수집 중",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MyPageColors.Neutral600
                )
            }
        }
    }
}


@Composable
fun TabSection(
    inProgressCount: Int?,
    hobbyCardCount: Int?,
    selectedTab: Int,
    scrapCount: Int?,
    onTabSelected: (Int) -> Unit,
    socialType: String?,
) {
    val isGuest = socialType == "GUEST"  // 게스트 여부 확인
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 20.dp)
    ) {
        // 진행중 탭
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = rememberThrottledClick { onTabSelected(0) }
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .then(
                        if (selectedTab == 0)
                            Modifier.drawBehind {
                                val strokeWidth = 2.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = MyPageColors.Neutral800,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                        else Modifier
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "진행중",
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 0) MyPageColors.Neutral800 else MyPageColors.Neutral400
                    )
                    Text(
                        text = (inProgressCount ?: 0).toString(),
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 0) MyPageColors.Neutral800 else MyPageColors.Neutral400
                    )
                }
            }
        }

        // 취미카드 탭
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = rememberThrottledClick { onTabSelected(1) }
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .then(
                        if (selectedTab == 1)
                            Modifier.drawBehind {
                                val strokeWidth = 2.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = MyPageColors.Neutral800,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                        else Modifier
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "취미카드",
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 1) MyPageColors.Neutral800 else MyPageColors.Neutral400
                    )
                    Text(
                        text = (hobbyCardCount ?: 0).toString(),
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 1) MyPageColors.Neutral800 else MyPageColors.Neutral400
                    )
                }
            }
        }

        // 스크랩 탭
        if (!isGuest) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = rememberThrottledClick { onTabSelected(2) }
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight()
                        .then(
                            if (selectedTab == 2)
                                Modifier.drawBehind {
                                    val strokeWidth = 2.dp.toPx()
                                    val y = size.height - strokeWidth / 2
                                    drawLine(
                                        color = MyPageColors.Neutral800,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                            else Modifier
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "스크랩",
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == 2) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 2) MyPageColors.Neutral800 else MyPageColors.Neutral400
                    )
                }
            }
        }
    }
}

@Composable
fun ScrapTabContent(
    scrapListUiModel: ScrapListUiModel?,
    onScrapItemClick: (ScrapItemUiModel) -> Unit
) {
    val items = scrapListUiModel?.items ?: emptyList()

    if (items.isEmpty()) {
        Spacer(Modifier.height(92.dp))
        EmptyStateBox(
            modifier = Modifier.padding(horizontal = 20.dp),
            title = "아직 스크랩한 기록이 없어요.",
            description = "마음에 드는 취미활동을 둘러볼까요?"
        )
    } else {
        // 스크랩 목록 UI
        ScrapGridSection(
            scrapCount = scrapListUiModel?.totalCount,
            scrapItems = items,
            onScrapItemClick = onScrapItemClick
        )
    }
}


@Composable
fun ScrapGridSection(
    scrapCount: Int?,
    scrapItems: List<ScrapItemUiModel>,
    onScrapItemClick: (ScrapItemUiModel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (scrapCount == null) "0개" else "${scrapCount}개",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MyPageColors.Neutral500,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        // Grid of scraps - 3개씩 행으로 배치
        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            scrapItems.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    rowItems.forEach { scrapItem ->
                        ScrapCardItem(
                            modifier = Modifier.weight(1f),
                            scrapItemUiModel = scrapItem,
                            onClick = { onScrapItemClick(scrapItem) }
                        )
                    }

                    // 마지막 행이 3개 미만일 경우 빈 공간 채우기
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}




// ==================== 스크랩 카드 아이템 (새로 추가) ====================
@Composable
fun ScrapCardItem(
    modifier: Modifier = Modifier,
    scrapItemUiModel: ScrapItemUiModel,
    onClick: () -> Unit = {}
) {
    val isEmptyUrl = scrapItemUiModel.imageUrl.isEmpty()

    // 스티커 타입에 따른 아이콘 리소스

    // 스티커 아이콘에 따른 그라디언트 색상
    val stickerGradientColors = when (scrapItemUiModel.stickerType) {
        R.drawable.ic_sticker_smile -> listOf(
            Color(0xFFFFE6D1),
            Color(0xFFF4A261)
        )
        R.drawable.ic_sticker_sad -> listOf(
            Color(0xFFDDF2D8),
            Color(0xFFA8D8A2),
            Color(0xFFDDF2D8)
        )
        R.drawable.ic_sticker_laugh -> listOf(
            Color(0xFFC9DBFF),
            Color(0xFF8FB3FF),
            Color(0xFFC9DBFF)
        )
        R.drawable.ic_sticker_angry -> listOf(
            Color(0xFFFFF4CC),
            Color(0xFFFFD966),
            Color(0xFFFFF4CC)
        )
        else -> listOf(
            Color(0xFFDDF2D8),
            Color(0xFFA8D8A2),
            Color(0xFFDDF2D8)
        )
    }

    Box(
        modifier = modifier
            .aspectRatio(106f / 128f)
            .clickable(
                onClick = rememberThrottledClick { onClick() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .then(
                if (isEmptyUrl && stickerGradientColors.isNotEmpty()) {
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = stickerGradientColors,
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                } else {
                    Modifier.background(Color.LightGray)
                }
            )
    ) {
        // URL이 있을 때만 이미지 표시
        if (!isEmptyUrl) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2))
            ) {
                AsyncImage(
                    model = scrapItemUiModel.imageUrl,
                    contentDescription = "스크랩 이미지",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        } else if (scrapItemUiModel.memoPreview.isNotEmpty()) {
            // URL이 비어있고 memo가 있으면 텍스트 표시
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, top = 43.dp)
                    .width(86.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_text_ttaompyo),
                    contentDescription = null,
                )
                Text(
                    text = scrapItemUiModel.memoPreview,
                    style = TextStyle(
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 스티커 아이콘은 항상 표시
        scrapItemUiModel.stickerType?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "스티커 아이콘",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
                    .padding(end = 11.dp, bottom = 11.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

// ==================== 진행중 탭 콘텐츠 ====================
@Composable
fun InProgressTabContent(
    hobbyItems: List<HobbyUiModel>?,
    feedList: List<FeedUiModel>?,
    feedCount: Int?,
    viewModel: MyPageViewModel,
    selectedHobbyIds: Set<Int?>,
    onHobbySelectionChange: (Set<Int?>) -> Unit,
    onStickerClick: (FeedUiModel) -> Unit,
    onAddHobbyClick: () -> Unit,
    inProgressCount: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HobbyCategoriesSection(
            feedCount = feedCount,
            hobbyItems = hobbyItems,
            selectedHobbyIds = selectedHobbyIds,
            onHobbySelectionChange = onHobbySelectionChange,
            onAddHobbyClick = onAddHobbyClick,
            inProgressCount = inProgressCount
        )

        StickerGridSection(
            feedList = feedList,
            onStickerClick = onStickerClick,
            feedCount = feedCount
        )
    }
}

@Composable
fun HobbyCategoriesSection(
    feedCount: Int?,
    hobbyItems: List<HobbyUiModel>?,
    selectedHobbyIds: Set<Int?>,
    onHobbySelectionChange: (Set<Int?>) -> Unit,
    onAddHobbyClick: () -> Unit,
    inProgressCount: Int?
) {
    val displayHobbies = hobbyItems?.filter {
        it.status == "IN_PROGRESS" || it.status == "ARCHIVED"
    } ?: emptyList()

    LaunchedEffect(hobbyItems) {
        Timber.d("hobbyItems: $hobbyItems")
        Timber.d("hobbyItems size: ${hobbyItems?.size}")
    }

    LaunchedEffect(displayHobbies) {
        Timber.d("displayHobbies (IN_PROGRESS + ARCHIVED): $displayHobbies")
        Timber.d("displayHobbies size: ${displayHobbies.size}")
    }

    val showAddButton = (inProgressCount ?: 0) < 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showAddButton) {
            HobbyCategoryItem(
                hobbyName = "취미 추가",
                thumbnail = null,
                isActive = true,
                isSelected = false,
                isAddButton = true,
                onClick = onAddHobbyClick
            )
        }
        displayHobbies.forEach { hobby ->
            HobbyCategoryItem(
                hobbyName = hobby.hobbyName,
                thumbnail = hobby.thumbnail,
                isActive = hobby.status == "IN_PROGRESS",
                isSelected = selectedHobbyIds.contains(hobby.hobbyId),
                isAddButton = false,
                onClick = {
                    val newSelection = if (selectedHobbyIds.contains(hobby.hobbyId)) {
                        selectedHobbyIds - hobby.hobbyId
                    } else {
                        selectedHobbyIds + hobby.hobbyId
                    }
                    onHobbySelectionChange(newSelection)
                }
            )
        }
    }
}

@Composable
fun HobbyCategoryItem(
    hobbyName: String?,
    thumbnail: String?,
    isActive: Boolean,
    isSelected: Boolean = false,
    isAddButton: Boolean = false,  // ✅ 추가
    onClick: () -> Unit = {}
) {
    val hasValidThumbnail = !thumbnail.isNullOrEmpty()
    val hobbyIcon = if (!isAddButton) getHobbyIconByName(hobbyName) else null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = when {
                        isAddButton -> Color(0xFFE5E5E5)  // ✅ 취미 추가 버튼
                        isSelected -> Color(0xFFFF9447)
                        hasValidThumbnail -> MyPageColors.Stroke001
                        hobbyIcon != null -> Color(0xFFE5E5E5)
                        else -> MyPageColors.Stroke001
                    },
                    shape = CircleShape
                )
                .background(
                    color = when {
                        isAddButton -> Color(0xFFFFFFFF)  // ✅ 취미 추가 버튼
                        hasValidThumbnail -> Color.LightGray.copy(alpha = if (isActive) 1f else 0.4f)
                        hobbyIcon != null -> Color.White
                        else -> Color.LightGray.copy(alpha = if (isActive) 1f else 0.4f)
                    },
                    shape = CircleShape
                )
                .clickable(
                    onClick = rememberThrottledClick { onClick() },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                // ✅ 케이스 0: 취미 추가 버튼
                isAddButton -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "취미 추가",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF3A3A3A)
                    )
                }

                // 케이스 1: 실제 thumbnail이 있을 때
                hasValidThumbnail -> {
                    SubcomposeAsyncImage(
                        model = thumbnail,
                        contentDescription = hobbyName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                                contentDescription = "로딩 중",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        },
                        error = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                                contentDescription = "로딩 실패",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    )

                    if (!isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.6f))
                        )
                    }
                }

                // 케이스 2: thumbnail 없지만 hobbyName에 매칭되는 아이콘이 있을 때
                hobbyIcon != null -> {
                    Icon(
                        painter = painterResource(id = hobbyIcon),
                        contentDescription = hobbyName,
                        modifier = Modifier.size(24.dp),
                        tint = if (isActive) Color(0xFFFF9447) else Color(0xFFFF9447).copy(alpha = 0.4f)
                    )

                    if (!isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.6f))
                        )
                    }
                }

                // 케이스 3: 둘 다 없을 때 → placeholder
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "기본 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Text(
            text = hobbyName ?: "",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) MyPageColors.Neutral800 else MyPageColors.Neutral400,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StickerGridSection(
    feedCount: Int?,
    feedList: List<FeedUiModel>?,
    onStickerClick: (FeedUiModel) -> Unit
) {
    val items = feedList ?: emptyList()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (feedCount == null) "0개" else "${feedCount}개",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MyPageColors.Neutral500,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        // Grid of stickers - 3개씩 행으로 배치
        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    rowItems.forEach { feed ->
                        StickerCardItem(
                            modifier = Modifier.weight(1f),
                            feedUiModel = feed,
                            onClick = { onStickerClick(feed) }
                        )
                    }

                    // 마지막 행이 3개 미만일 경우 빈 공간 채우기
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun StickerCardItem(
    modifier: Modifier = Modifier,
    feedUiModel: FeedUiModel? = null,
    hasGradient: Boolean = false,
    gradientColors: List<Color> = emptyList(),
    hasOverlay: Boolean = false,
    onClick: () -> Unit = {}
) {
    val isEmptyUrl = feedUiModel?.url.isNullOrEmpty()

    // 스티커 아이콘에 따른 그라디언트 색상 결정
    val stickerGradientColors = feedUiModel?.let { feed ->
        when (feed.stickerIconRes) {
            R.drawable.ic_sticker_smile -> listOf(
                Color(0xFFFFE6D1),
                Color(0xFFF4A261)
            )
            R.drawable.ic_sticker_sad -> listOf(
                Color(0xFFDDF2D8),
                Color(0xFFA8D8A2),
                Color(0xFFDDF2D8)
            )
            R.drawable.ic_sticker_laugh -> listOf(
                Color(0xFFC9DBFF),
                Color(0xFF8FB3FF),
                Color(0xFFC9DBFF)
            )
            R.drawable.ic_sticker_angry -> listOf(
                Color(0xFFFFF4CC),
                Color(0xFFFFD966),
                Color(0xFFFFF4CC)
            )
            else -> listOf(
                Color(0xFFDDF2D8),
                Color(0xFFA8D8A2),
                Color(0xFFDDF2D8)
            )
        }
    } ?: emptyList()

    Box(
        modifier = modifier
            .aspectRatio(106f / 128f)
            .clickable(
                onClick = rememberThrottledClick { onClick() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .then(
                if (isEmptyUrl && stickerGradientColors.isNotEmpty()) {
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = stickerGradientColors,
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                } else if (hasGradient && gradientColors.isNotEmpty()) {
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
                } else {
                    Modifier.background(Color.LightGray)
                }
            )
            .then(
                if (hasOverlay) {
                    Modifier.background(Color(0xFFBD5757).copy(alpha = 0.5f))
                } else {
                    Modifier
                }
            )
    ) {
        // feedUiModel이 있으면 처리
        feedUiModel?.let { feed ->
            // URL이 있을 때만 이미지 표시
            if (!feed.url.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF2F2F2))
                ) {
                    AsyncImage(
                        model = feed.url,
                        contentDescription = "스티커 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            } else if (!feed.memo.isNullOrEmpty()) {
                // URL이 비어있고 memo가 있으면 텍스트 표시
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 43.dp)
                        .width(86.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_text_ttaompyo),
                        contentDescription = null,
                    )
                    Text(
                        text = feed.memo,
                        style = TextStyle(
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFFFFFFFF)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 스티커 아이콘은 항상 표시
            Image(
                painter = painterResource(id = feed.stickerIconRes),
                contentDescription = "스티커 아이콘",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
                    .padding(end = 11.dp, bottom = 11.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

// ==================== 공통 Empty State ====================
@Composable
fun EmptyStateBox(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    topSpacerHeight: Dp = 77.dp,
    button: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.box_img),
            contentDescription = null,
            modifier = Modifier.size(240.dp),
            contentScale = ContentScale.Fit,
            alpha = 0.3f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(topSpacerHeight))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MyPageColors.Neutral900,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MyPageColors.Neutral600,
                textAlign = TextAlign.Center,
                lineHeight = 19.6.sp
            )

            if (button != null) {
                button()
            }
        }
    }
}

// ==================== 취미카드 탭 콘텐츠 ====================
@Composable
fun HobbyCardTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(92.dp))
        EmptyStateBox(
            title = "취미카드를 준비 중이에요.",
            description = "지금 하고 있는 취미를\n꾸준히 이어가 보세요!"
        )
    }
}



@Composable
fun HobbyCardStack() {
    val hobbyCards = remember {
        listOf(
            HobbyCard(
                title = "주로 아침에 활동한 독서",
                imageUrl = "",
                rotation = 0f  // 이 값은 이제 사용하지 않음
            ),
            HobbyCard(
                title = "친구들과 함께 한 사진촬영",
                imageUrl = "",
                rotation = -5.17f
            ),
            HobbyCard(
                title = "집밥만들기로 채운 요리",
                imageUrl = "",
                rotation = 3.41f
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var offsetX by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val screenWidth = with(density) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    // 드래그 진행률 (0.0 ~ 1.0)
    val dragProgress = (offsetX / screenWidth * -1).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = screenWidth * 0.2f

                        if (offsetX < -threshold) {
                            currentIndex = (currentIndex + 1) % hobbyCards.size
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount < 0) {
                            offsetX += dragAmount
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val nextIndex = (currentIndex + 1) % hobbyCards.size
        val nextNextIndex = (currentIndex + 2) % hobbyCards.size

        // 가장 뒤 카드 (다다음) - 약간 왼쪽으로 기울어진 상태
        HobbyCardItem(
            card = hobbyCards[nextNextIndex],
            modifier = Modifier
                .zIndex(1f)
                .offset(y = 5.dp)
                .graphicsLayer {
                    alpha = 0.5f
                    scaleX = 0.9f
                    scaleY = 0.9f
                    rotationZ = -3f  // 왼쪽으로 3도
                }
        )

        // 중간 카드 (다음) - 드래그 시 오른쪽으로 회전하며 앞으로 나옴
        HobbyCardItem(
            card = hobbyCards[nextIndex],
            modifier = Modifier
                .zIndex(2f)
                .offset(y = 0.dp)
                .graphicsLayer {
                    alpha = 0.7f + (dragProgress * 0.3f)
                    val scale = 0.95f + (dragProgress * 0.05f)
                    scaleX = scale
                    scaleY = scale
                    // 드래그 전: 약간 왼쪽으로 (-2도)
                    // 드래그 중: 오른쪽으로 회전 (최대 5도)
                    rotationZ = -2f + (dragProgress * 7f)
                    translationX = dragProgress * 20f
                }
        )

        // 가장 앞 카드 (현재) - 항상 오른쪽으로 5도 기울어진 상태
        HobbyCardItem(
            card = hobbyCards[currentIndex],
            modifier = Modifier
                .zIndex(3f)
                .offset(
                    x = with(density) { offsetX.toDp() },
                    y = 16.dp
                )
                .graphicsLayer {
                    alpha = 1f - dragProgress
                    val scale = 1f - (dragProgress * 0.2f)
                    scaleX = scale
                    scaleY = scale
                    // 항상 오른쪽으로 5도 + 드래그 시 추가 회전
                    rotationZ = 5f + (dragProgress * -15f)
                }
        )
    }
}


fun getHobbyIconByName(hobbyName: String?): Int? {
    if (hobbyName.isNullOrEmpty()) return null

    return when {
        hobbyName.contains("독서") -> R.drawable.ic_book
        hobbyName.contains("사진") -> R.drawable.ic_camera2
        hobbyName.contains("요리") -> R.drawable.ic_cook
        hobbyName.contains("그림") -> R.drawable.ic_draw
        hobbyName.contains("헬스") -> R.drawable.ic_health
        hobbyName.contains("음악") -> R.drawable.ic_music
        hobbyName.contains("러닝") -> R.drawable.ic_running
        hobbyName.contains("카페") -> R.drawable.ic_cafe
        hobbyName.contains("영화") -> R.drawable.ic_movie
        hobbyName.contains("글쓰기") -> R.drawable.ic_write
        else -> R.drawable.ic_etc_hobby
    }
}

@Composable
fun HobbyCardItem(
    card: HobbyCard,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(320.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray)
    ) {
        // 실제 이미지
//        AsyncImage(
//            model = card.imageUrl,
//            contentDescription = "활동 이미지",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
        Image(
            painter = painterResource(R.drawable.hobbycard_cafe),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 그라디언트 오버레이
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = MyPageColors.CardOverlayGradient,
                        startY = 160f,
                        endY = 320f
                    )
                )
        )

        // 제목
        Text(
            text = card.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MyPageColors.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
        )
    }
}

// ==================== 설정 메뉴 팝업 ====================
@Composable
fun SettingsMenuPopup(
    modifier: Modifier = Modifier,
    socialType: String?,
    onDismiss: () -> Unit,
    onProfileSettingClick: () -> Unit,
    onHobbyPhotoManagementClick: () -> Unit,
    onAllSettingsClick: () -> Unit,
    isMine: Boolean = true,
    onBlockClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val isGuest = socialType == "GUEST"  // ✅ 게스트 여부 확인
    Surface(
        modifier = modifier
            .wrapContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = MyPageColors.Background001,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Timber.e("1@@@@@@@@@@@@"+isMine)
            if (!isMine) {
                Timber.e("2@@@@@@@@@@@@"+isMine)
                SettingsMenuItem(
                    text = "차단하기",
                    onClick = onBlockClick
                )
                SettingsMenuItem(
                    text = "신고하기",
                    onClick = onRegisterClick
                )
            } else {
                if (!isGuest) {
                    SettingsMenuItem(
                        text = "내 프로필 설정",
                        onClick = onProfileSettingClick
                    )
                    SettingsMenuItem(
                        text = "취미 대표사진 관리",
                        onClick = onHobbyPhotoManagementClick
                    )
                }
                SettingsMenuItem(
                    text = "전체설정",
                    onClick = onAllSettingsClick
                )
            }
        }
    }
}

@Composable
fun SettingsMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MyPageColors.Neutral800,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(
                onClick = rememberThrottledClick { onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    )
}

// ==================== 차단된 유저 Empty State ====================
@Composable
fun BlockedUserEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_sad),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "차단한 유저예요.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MyPageColors.Neutral600
        )
    }
}

// ==================== 차단 확인 다이얼로그 ====================
@Composable
fun BlockUserConfirmDialog(
    nickname: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val displayNickname = if (nickname.length > 10) nickname.take(10) else nickname

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            color = MyPageColors.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // 타이틀
                Text(
                    text = "${nickname} 님을 차단하시겠어요?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyPageColors.Neutral900,
                    lineHeight = 25.2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 본문 1
                Text(
                    text = "${displayNickname} 님이 올리는 모든 활동기록은 숨김처리되며, 회원님의 프로필 또는 활동기록은 공개되지 않습니다.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MyPageColors.Neutral800,
                    lineHeight = 19.6.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 본문 2
                Text(
                    text = "상대방에게는 회원님이 차단한 사실은 알려지지 않으며, 언제든지 차단 해지 가능합니다.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MyPageColors.Neutral800,
                    lineHeight = 19.6.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼 행
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 아니오 버튼
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, MyPageColors.Stroke001, RoundedCornerShape(10.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "아니오",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MyPageColors.Neutral600
                        )
                    }

                    // 예 버튼
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFF9447))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onConfirm
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "예",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MyPageColors.White
                        )
                    }
                }
            }
        }
    }
}

// ─── Shimmer Skeleton ────────────────────────────────────────────────────────

// 피그마 모션 스펙: Left to Right / 1.8s / LinearEasing / Infinite / 15°
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val shimmerColors = listOf(
        Color(0xFFF9F9F9),
        Color(0xFFF2F2F2),
        Color(0xFFF9F9F9)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    val offsetY = (1000f * Math.tan(Math.toRadians(15.0))).toFloat()
    return this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 1000f, -offsetY),
            end = Offset(translateAnim, offsetY)
        )
    )
}

@Composable
fun MypageSkeletonContent() {
    Spacer(modifier = Modifier.height(20.dp))
    ProfileSkeletonSection()
    Spacer(modifier = Modifier.height(16.dp))
    TabSkeletonSection()
    Spacer(modifier = Modifier.height(24.dp))
    HobbyListSkeletonSection()
    Spacer(modifier = Modifier.height(8.dp))
    CalendarGridSkeletonSection()
}

@Composable
private fun ProfileSkeletonSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(111.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .width(111.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun TabSkeletonSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        listOf(true, false, false).forEach { isSelected ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .then(
                        if (isSelected) Modifier.drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            drawLine(
                                color = Color(0xFF3A3A3A),
                                start = Offset(0f, size.height - strokeWidth / 2),
                                end = Offset(size.width, size.height - strokeWidth / 2),
                                strokeWidth = strokeWidth
                            )
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 24.dp, height = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(9.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
private fun HobbyListSkeletonSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) { HobbyItemSkeletonItem() }
    }
}

@Composable
private fun HobbyItemSkeletonItem() {
    Column(
        modifier = Modifier.width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}

@Composable
private fun CalendarGridSkeletonSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(144.dp)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MyPageScreenPreview() {
    ForDayTheme {
        MyPageScreen(
            modifier = TODO(),
            viewModel = TODO(),
            onProfileSetting = TODO(),
            onHobbyPhotoManagement = TODO(),
            onAllSettingsClick = TODO(),
            onRoutineFeedClick = TODO(),
            onAddHobbyClick = TODO(),
            onDismiss = TODO(),
            onNavigateToRecordRoutine = TODO()
        )
    }
}