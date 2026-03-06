package com.forday.app.presentation.mypage.routinedetail.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.BottomButtonState
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.domain.model.ReactionDetailDomain
import com.forday.app.domain.model.ReactionUserInfo
import com.forday.app.presentation.mypage.routinedetail.RoutineDetailAction
import com.forday.app.presentation.mypage.routinedetail.RoutineDetailSideEffect
import com.forday.app.presentation.mypage.routinedetail.RoutineDetailUiState
import com.forday.app.presentation.mypage.routinedetail.RoutineDetailViewModel
import com.forday.app.presentation.mypage.routinedetail.RoutineReactionUiModel
import com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel
import com.forday.app.presentation.mypage.routinedetail.RoutineUserReactionUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.tan

// 색상 정의
object ActivityDetailColors {
    val Neutral900 = Color(0xFF1E1E1E)
    val Neutral800 = Color(0xFF3A3A3A)
    val Neutral600 = Color(0xFF7A7A7A)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Background001 = Color(0xFFFFFFFF)
    val Background002 = Color(0xFFF9F9F9)
    val Stroke001 = Color(0xFFE5E5E5)
    val Action001 = Color(0xFFFF9447)  // Primary orange
    val ToastBackground = Color(0xAD000000)  // 68% black
    val ToastSuccess = Color(0xFFD9F7E5)
}

data class ReactionDetailUiModel(
    val isSuccess: Boolean = false,
    val reactionType: String = "",
    val users: List<ReactionUserUiModel> = emptyList(),
    val hasNext: Boolean = false,
    val lastUserId: String = "",
    val message: String = "",
    val errorClassName: String = ""
)

data class ReactionUserUiModel(
    val userId: String = "",
    val nickname: String = "",
    val profileImageUrl: String? = "",
    val reactedAt: String = "",
    val newReactionUser: Boolean = false
)

fun ReactionDetailDomain.toUiModel() =
    ReactionDetailUiModel(
        isSuccess = isSuccess,
        reactionType = reactionType,
        users = users.map { it.toUiModel() },
        hasNext = hasNext,
        lastUserId = lastUserId,
        message = message,
        errorClassName = errorClassName
    )

fun ReactionUserInfo.toUiModel(): ReactionUserUiModel =
    ReactionUserUiModel(
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        reactedAt = reactedAt,
        newReactionUser = newReactionUser
    )

// 반응 타입
enum class ReactionType {
    AWESOME,    // 멋져요
    GREAT,      // 최고예요
    AMAZING,    // 놀라워요
    FIGHTING    // 응원해요
}

@Composable
fun RoutineDetailScreen(
    onBackClick: () -> Unit = {},
    routineId: Long,
    onNavigateToMyPage: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onSaveCardClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onNavigateToUserPage: (userId: String, recordOwner: Boolean) -> Unit = { _, _ -> },
    isNewRecord: Boolean = false,
    isUserPageEntry: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: RoutineDetailViewModel,
    onNavigateToRecordRoutine: (RoutineRecordDetailUiModel?, Boolean) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(RoutineDetailAction.LoadDetail(routineId.toInt()))
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is RoutineDetailSideEffect.DeleteSuccess -> onBackClick()
            }
        }
    }

    RoutineDetailContent(
        state = state,
        onAction = viewModel::onAction,
        routineId = routineId,
        onBackClick = onBackClick,
        onNavigateToMyPage = onNavigateToMyPage,
        onNavigateToHome = onNavigateToHome,
        onSaveCardClick = onSaveCardClick,
        onReportClick = onReportClick,
        onNavigateToUserPage = onNavigateToUserPage,
        isNewRecord = isNewRecord,
        isUserPageEntry = isUserPageEntry,
        modifier = modifier,
        onNavigateToRecordRoutine = onNavigateToRecordRoutine,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailContent(
    state: RoutineDetailUiState,
    onAction: (RoutineDetailAction) -> Unit,
    routineId: Long,
    onBackClick: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onSaveCardClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onNavigateToUserPage: (userId: String, recordOwner: Boolean) -> Unit = { _, _ -> },
    isNewRecord: Boolean = false,
    isUserPageEntry: Boolean = false,
    modifier: Modifier = Modifier,
    onNavigateToRecordRoutine: (RoutineRecordDetailUiModel?, Boolean) -> Unit,
) {
    BackHandler(enabled = isNewRecord) {
        onNavigateToHome()
    }
    val routine = state.detail.routine
    val isBookmarked = state.reaction.isScraped ?: routine?.isScraped ?: false
    // Optimistic Update용 임시 상태
    var selectedReactions by remember { mutableStateOf<Set<ReactionType>>(emptySet()) }
    var canceledReactions by remember { mutableStateOf<Set<ReactionType>>(emptySet()) }
    var pendingReaction by remember { mutableStateOf<ReactionType?>(null) }
    var showReactionUsers by remember { mutableStateOf(false) }
    var displayedReaction by remember { mutableStateOf<ReactionType?>(null) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var moreIconBottomPx by remember { mutableFloatStateOf(0f) }
    var containerTopPx by remember { mutableFloatStateOf(0f) }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var showPrivacyBottomSheet by remember { mutableStateOf(false) }
    var selectedPrivacy by remember { mutableStateOf(routine?.isPublic) }

    var showSuccessAnimation by remember { mutableStateOf(isNewRecord) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(showSuccessAnimation) {
        if (showSuccessAnimation) {
            delay(3000)
            showSuccessAnimation = false
        }
    }

    Timber.e("@@@@@@@@@@@@content " + state.detail.routine?.content)

    val shimmerBrush = rememberShimmerBrush()
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ActivityDetailColors.Background001)
            .onGloballyPositioned { coords ->
                containerTopPx = coords.positionInRoot().y
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoutineDetailHeader(
                title = "내 활동 보기",
                showTitle = !isUserPageEntry,
                onBackClick = onBackClick,
                onDownloadClick = onSaveCardClick,
                onMoreMenuClick = { showMoreMenu = !showMoreMenu },
                isNewRecord = isNewRecord,
                showDownloadButton = !routine?.imageUrl.isNullOrEmpty() && routine?.isMine == true,
                onMoreIconPositioned = { bottomPx -> moreIconBottomPx = bottomPx }
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (routine == null) {
                    RoutineDetailSkeletonContent(shimmerBrush = shimmerBrush)
                } else {
                    ActivityContent(
                        routine = routine,
                        isMine = routine.isMine ?: true,
                        writerNickname = routine.writerNickname ?: "",
                        writerProfileImageUrl = routine.writerProfileImageUrl ?: "",
                        onWriterClick = { onNavigateToUserPage(routine.writerId ?: "", routine.isMine ?: false) },
                        isUserPageEntry = isUserPageEntry,
                        hobbyName = state.detail.routine?.hobbyName ?: ""
                    )
                }
            }

            //
            if (isNewRecord) {
                BottomNextButton(
                    text = "홈으로 가기",
                    state = BottomButtonState.ENABLED,
                    onClick = onNavigateToHome
                )
            } else {
                //
                AnimatedVisibility(
                    visible = showReactionUsers && state.reaction.users.users.isNotEmpty(),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    ReactionUsersList(
                        reactionType = displayedReaction ?: ReactionType.AWESOME,
                        users = state.reaction.users.users
                    )
                }

                // Bottom Reaction Bar
                BottomReactionBar(
                    selectedReactions = selectedReactions,
                    canceledReactions = canceledReactions,
                    myReactions = routine?.myReactions,
                    reactions = routine?.reactions,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = {
                        onAction(RoutineDetailAction.ToggleScrap(routineId.toInt(), isBookmarked))
                    },
                    onReactionTap = { reaction ->
                        if (showReactionUsers) {
                            if (pendingReaction == reaction) {
                                pendingReaction = null
                                showReactionUsers = false
                            } else {
                                pendingReaction = reaction
                                displayedReaction = reaction
                                val reactionString = when (reaction) {
                                    ReactionType.AWESOME -> "AWESOME"
                                    ReactionType.GREAT -> "GREAT"
                                    ReactionType.AMAZING -> "AMAZING"
                                    ReactionType.FIGHTING -> "FIGHTING"
                                }
                                onAction(RoutineDetailAction.GetReactionUsers(routineId.toInt(), reactionString))
                            }
                        } else {
                            pendingReaction = reaction
                            displayedReaction = reaction
                            val reactionString = when (reaction) {
                                ReactionType.AWESOME -> "AWESOME"
                                ReactionType.GREAT -> "GREAT"
                                ReactionType.AMAZING -> "AMAZING"
                                ReactionType.FIGHTING -> "FIGHTING"
                            }
                            onAction(RoutineDetailAction.GetReactionUsers(routineId.toInt(), reactionString))
                            showReactionUsers = true
                        }
                    },
                    onReactionDoubleTap = { reaction ->
                        val isPressed = when (reaction) {
                            ReactionType.AWESOME -> routine?.myReactions?.pressedAwesome
                            ReactionType.GREAT -> routine?.myReactions?.pressedGreat
                            ReactionType.AMAZING -> routine?.myReactions?.pressedAmazing
                            ReactionType.FIGHTING -> routine?.myReactions?.pressedFighting
                        }
                        val isCurrentlySelected = (isPressed == true && !canceledReactions.contains(reaction))
                                || selectedReactions.contains(reaction)
                        val reactionString = when (reaction) {
                            ReactionType.AWESOME -> "AWESOME"
                            ReactionType.GREAT -> "GREAT"
                            ReactionType.AMAZING -> "AMAZING"
                            ReactionType.FIGHTING -> "FIGHTING"
                        }
                        val shouldRefreshUsers = showReactionUsers && displayedReaction == reaction
                        if (isCurrentlySelected) {
                            if (isPressed == true) {
                                onAction(RoutineDetailAction.CancelReaction(routineId.toInt(), reactionString, shouldRefreshUsers))
                                canceledReactions = canceledReactions + reaction
                            }
                            selectedReactions = selectedReactions - reaction
                        } else {
                            onAction(RoutineDetailAction.ReactToPosting(routineId.toInt(), reactionString, shouldRefreshUsers))
                            selectedReactions = selectedReactions + reaction
                            canceledReactions = canceledReactions - reaction
                        }
                    }
                )
            }
        }

        // More Menu Dropdown
        if (showMoreMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showMoreMenu = false }
                    )
            )
            MoreMenuDropdown(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = with(density) { (moreIconBottomPx - containerTopPx).toDp() } + 8.dp,
                        end = 20.dp
                    )
                    .zIndex(10f),
                isMine = routine?.isMine ?: true,
                onModifyPosting = {
                    showMoreMenu = false
                    onNavigateToRecordRoutine(state.detail.routine, true)
                },
                onSetThumbnailClick = {
                    onAction(RoutineDetailAction.SetHobbyMainImage(
                        hobbyId = state.detail.routine?.hobbyId?.toLong(),
                        recordId = state.detail.routine?.recordId?.toLong()
                    ))
                    showMoreMenu = false
                    toastMessage = "대표사진 설정 완료!"
                    showToast = true
                    scope.launch {
                        delay(3000)
                        showToast = false
                    }
                },
                onDeletePosting = {
                    showMoreMenu = false
                    showDeleteConfirmDialog = true
                },
                onReportClick = {
                    showMoreMenu = false
                    onReportClick()
                },
                hasImage = !state.detail.routine?.imageUrl.isNullOrEmpty(),
            )
        }
        // Toast Message
        AnimatedVisibility(
            visible = showToast,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 112.dp)
        ) {
            ToastMessage2(message = toastMessage)
        }

        // ✅ 기록 완료 Lottie 애니메이션 (버튼과 함께 표시)
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = fadeIn(animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(250)),
            modifier = Modifier.fillMaxSize()
        ) {
            RecordSuccessAnimation(state.detail.nickname ?: "포비")
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteConfirmDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteConfirmDialog = false },
            onConfirm = {
                showDeleteConfirmDialog = false
                routine?.recordId?.let { recordId ->
                    onAction(RoutineDetailAction.DeletePosting(recordId.toLong()))
                }
            }
        )
    }

    // Privacy Setting Bottom Sheet
    if (showPrivacyBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPrivacyBottomSheet = false },
            sheetState = sheetState,
            containerColor = ActivityDetailColors.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null
        ) {
            PrivacySettingBottomSheet(
                selectedPrivacy = selectedPrivacy == true,
                onPrivacySelected = { privacy ->
                    selectedPrivacy = privacy
                },
                onConfirmClick = {
                    showPrivacyBottomSheet = false
                    toastMessage = "공개범위가 변경되었습니다"
                    showToast = true
                    scope.launch {
                        delay(3000)
                        showToast = false
                    }
                }
            )
        }
    }
}

@Composable
fun ReactionUserItem(user: ReactionUserUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.width(48.dp)
    ) {
        // 프로필 이미지
        Box(modifier = Modifier.size(40.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(ActivityDetailColors.Stroke001),
                contentAlignment = Alignment.Center
            ) {
                if (!user.profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = user.profileImageUrl,
                        contentDescription = user.nickname,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_profile_empty),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )
                }
            }
            if (user.newReactionUser) {
                Image(
                    painter = painterResource(R.drawable.icon_new),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 3.dp)
                )
            }
        }

        // 닉네임
        Text(
            text = user.nickname,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = ActivityDetailColors.Neutral600,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ReactionUsersList(
    reactionType: ReactionType,
    users: List<ReactionUserUiModel>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ActivityDetailColors.Background001,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            users.forEach { user ->
                ReactionUserItem(user = user)
            }
        }
    }
}

@Composable
fun RoutineDetailHeader(
    title: String,
    showTitle: Boolean = true,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit = {},
    onMoreMenuClick: () -> Unit,
    isNewRecord: Boolean = false,
    showDownloadButton: Boolean = true,
    onMoreIconPositioned: (Float) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showTitle) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ActivityDetailColors.Neutral800
            )
        }

        // ✅ isNewRecord가 true면 뒤로가기 버튼 숨김
        if (!isNewRecord) {
            IconButton(
                onClick = rememberThrottledClick(onClick = onBackClick),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_chevron_left),
                    contentDescription = "뒤로가기",
                    tint = ActivityDetailColors.Neutral800
                )
            }
        }

        // isNewRecord가 true면 더보기 버튼도 숨김
        if (!isNewRecord) {
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showDownloadButton) {
                    IconButton(
                        onClick = rememberThrottledClick(onClick = onDownloadClick),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_download),
                            contentDescription = "갤러리 저장",
                            tint = ActivityDetailColors.Neutral800
                        )
                    }
                }

                IconButton(
                    onClick = rememberThrottledClick(onClick = onMoreMenuClick),
                    modifier = Modifier
                        .size(24.dp)
                        .onGloballyPositioned { coords ->
                            onMoreIconPositioned(coords.positionInRoot().y + coords.size.height)
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "더보기",
                        tint = ActivityDetailColors.Neutral800
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityContent(
    routine: RoutineRecordDetailUiModel?,
    isMine: Boolean = true,
    writerNickname: String = "",
    writerProfileImageUrl: String = "",
    onWriterClick: () -> Unit = {},
    isUserPageEntry: Boolean,
    hobbyName: String = "",
) {
    Timber.e("@@@@@@@@@@@@@@isUserPageEntry@@@@@@ "+isUserPageEntry)
    val hasNoImageAndMemo = routine?.imageUrl.isNullOrEmpty() && routine?.memo.isNullOrEmpty()

    // stickerUrl을 drawable 리소스로 변환
    val stickerDrawableRes = remember(routine?.stickerUrl) {
        when {
            routine?.stickerUrl?.contains("smile", ignoreCase = true) == true -> R.drawable.ic_sticker_smile
            routine?.stickerUrl?.contains("laugh", ignoreCase = true) == true -> R.drawable.ic_sticker_laugh
            routine?.stickerUrl?.contains("sad", ignoreCase = true) == true -> R.drawable.ic_sticker_sad
            routine?.stickerUrl?.contains("angry", ignoreCase = true) == true -> R.drawable.ic_sticker_angry
            else -> null
        }
    }

    val painter = rememberAsyncImagePainter(model = routine?.imageUrl)
    val imageState by painter.state.collectAsState()  // ✅ collectAsState로 상태 관찰 → recomposition 트리거
    val shimmerBrush = rememberShimmerBrush()
    val imageAspectRatio = if (imageState is AsyncImagePainter.State.Success) {
        val image = (imageState as AsyncImagePainter.State.Success).result.image
        val width = image.width.toFloat()
        val height = image.height.toFloat()
        if (height > 0f) width / height else 1f
    } else {
        1f
    }

    if (hasNoImageAndMemo) {
        // 이미지와 메모가 모두 없을 때 - 스티커만 표시
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // 타이틀과 날짜
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (isUserPageEntry) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                onClick = rememberThrottledClick { onWriterClick() },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                        ) {
                            ProfileImageWithSkeleton(imageUrl = writerProfileImageUrl)
                            Text(
                                text = writerNickname,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (hobbyName.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF1E6), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = hobbyName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFFFF9447),
                                        lineHeight = (12 * 1.4).sp
                                    )
                                }
                            }
                            routine?.content?.let {
                                Text(
                                    text = it,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ActivityDetailColors.Neutral900,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else {
                    routine?.content?.let {
                        Text(
                            text = it,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ActivityDetailColors.Neutral900,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                routine?.date?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ActivityDetailColors.Neutral600
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 스티커 - 우측 정렬 (drawable 리소스 사용)
            stickerDrawableRes?.let { drawableRes ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 40.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = "스티커",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    } else {
        // 기존 레이아웃 (이미지 또는 메모가 있을 때)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (isUserPageEntry) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                onClick = rememberThrottledClick { onWriterClick() },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                        ) {
                            ProfileImageWithSkeleton(imageUrl = writerProfileImageUrl)
                            Text(
                                text = writerNickname,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (hobbyName.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF1E6), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = hobbyName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFFFF9447),
                                        lineHeight = (12 * 1.4).sp
                                    )
                                }
                            }
                            Text(
                                text = routine.content,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActivityDetailColors.Neutral900,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    Text(
                        text = routine.content,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ActivityDetailColors.Neutral900,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (routine.imageUrl.isNullOrEmpty()) {
                    Text(
                        text = routine.date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ActivityDetailColors.Neutral600
                    )
                }
            }

            // Image and Details
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image - imageUrl이 있을 때만 표시
                if (!routine.imageUrl.isNullOrEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(imageAspectRatio)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.dp,
                                    ActivityDetailColors.Stroke001,
                                    RoundedCornerShape(16.dp)
                                )
                                .background(ActivityDetailColors.White)
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "활동 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            if (imageState is AsyncImagePainter.State.Loading || imageState is AsyncImagePainter.State.Empty) {
                                SkeletonBox(
                                    modifier = Modifier.fillMaxSize(),
                                    brush = shimmerBrush,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }

                            if (imageState is AsyncImagePainter.State.Error) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFF2F2F2), RoundedCornerShape(16.dp))
                                )
                            }

                            if (imageState is AsyncImagePainter.State.Success) {
                                stickerDrawableRes?.let { drawableRes ->
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 21.dp, bottom = 21.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = drawableRes),
                                            contentDescription = "스티커",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }

                        }

                        // Timestamp
                        Text(
                            text = routine.date,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = ActivityDetailColors.Neutral600
                        )
                    }
                }
                Timber.e("@@@@@@@@@@@@@@############# @#@#@#@ " + routine.memo)
                // Memo Content
                if (!routine.memo.isNullOrEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(12.dp),
                        color = ActivityDetailColors.Background002
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = routine.memo,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 19.6.sp,
                                color = Color(0xFF3A3A3A),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        bottom = if (stickerDrawableRes != null) 96.dp else 0.dp
                                    ) // 스티커가 있으면 공간 확보
                            )
                            if (routine.imageUrl.isNullOrEmpty()) {  // TODO 여기 테스트 이미지, 텍스트 둘 다 올리면 텍스트 안나오는 오류
                                // 스티커 - 우측 하단
                                stickerDrawableRes?.let { drawableRes ->
                                    Image(
                                        painter = painterResource(id = drawableRes),
                                        contentDescription = "스티커",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .align(Alignment.BottomEnd),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordSuccessAnimation(
    nickName: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lottie 애니메이션
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.record_complete)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1,
                isPlaying = true,  // ✅ 명시적으로 재생
                restartOnPlay = true  // ✅ 재시작 허용
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )

            // "기록 완료!" 텍스트
            Text(
                text = "수고했어요, ${nickName}님\n 오늘의 포데이 완료!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
fun BottomReactionBar(
    selectedReactions: Set<ReactionType>,
    canceledReactions: Set<ReactionType>,
    myReactions: RoutineUserReactionUiModel?,
    reactions: RoutineReactionUiModel?,
    onReactionTap: (ReactionType) -> Unit,
    onReactionDoubleTap: (ReactionType) -> Unit,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color(0xFFE5E5E5),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            },
        color = ActivityDetailColors.Background001,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,  // ✅ SpaceBetween으로 변경
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ 왼쪽: 리액션 버튼들
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Awesome Reaction (멋져요)
                ReactionButton(
                    reactionType = ReactionType.AWESOME,
                    isSelected = selectedReactions.contains(ReactionType.AWESOME),
                    isPressed = myReactions?.pressedAwesome,
                    isCanceled = canceledReactions.contains(ReactionType.AWESOME),
                    hasNewReaction = reactions?.awesome,
                    onTap = { onReactionTap(ReactionType.AWESOME) },
                    onDoubleTap = { onReactionDoubleTap(ReactionType.AWESOME) },
                )

                // Great Reaction (최고예요)
                ReactionButton(
                    reactionType = ReactionType.GREAT,
                    isSelected = selectedReactions.contains(ReactionType.GREAT),
                    isPressed = myReactions?.pressedGreat,
                    isCanceled = canceledReactions.contains(ReactionType.GREAT),
                    hasNewReaction = reactions?.great,
                    onTap = { onReactionTap(ReactionType.GREAT) },
                    onDoubleTap = { onReactionDoubleTap(ReactionType.GREAT) },
                )

                // Amazing Reaction (놀라워요)
                ReactionButton(
                    reactionType = ReactionType.AMAZING,
                    isSelected = selectedReactions.contains(ReactionType.AMAZING),
                    isPressed = myReactions?.pressedAmazing,
                    isCanceled = canceledReactions.contains(ReactionType.AMAZING),
                    hasNewReaction = reactions?.amazing,
                    onTap = { onReactionTap(ReactionType.AMAZING) },
                    onDoubleTap = { onReactionDoubleTap(ReactionType.AMAZING) },
                )

                // Fighting Reaction (응원해요)
                ReactionButton(
                    reactionType = ReactionType.FIGHTING,
                    isSelected = selectedReactions.contains(ReactionType.FIGHTING),
                    isPressed = myReactions?.pressedFighting,
                    isCanceled = canceledReactions.contains(ReactionType.FIGHTING),
                    hasNewReaction = reactions?.fighting,
                    onTap = { onReactionTap(ReactionType.FIGHTING) },
                    onDoubleTap = { onReactionDoubleTap(ReactionType.FIGHTING) },
                )
            }

            // ✅ 오른쪽: 북마크 아이콘 (end에 고정)
            IconButton(
                onClick = rememberThrottledClick(onClick = onBookmarkClick),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isBookmarked) {
                            R.drawable.icon_bookmark_selected
                        } else {
                            R.drawable.icon_bookmark_unselected
                        }
                    ),
                    contentDescription = if (isBookmarked) "북마크 해제" else "북마크",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ReactionButton(
    reactionType: ReactionType,
    isSelected: Boolean,
    isPressed: Boolean?,
    isCanceled: Boolean,
    hasNewReaction: Boolean?,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit
) {
    val isActive = (isPressed == true && !isCanceled) || isSelected

    val iconRes = when (reactionType) {
        ReactionType.AWESOME -> if (isActive) {
            R.drawable.ic_cool_selected
        } else {
            R.drawable.ic_cool_unselected
        }

        ReactionType.GREAT -> if (isActive) {
            R.drawable.ic_good_selected
        } else {
            R.drawable.ic_good_unselected
        }

        ReactionType.AMAZING -> if (isActive) {
            R.drawable.ic_excellent_selected
        } else {
            R.drawable.ic_excellent_unselected
        }

        ReactionType.FIGHTING -> if (isActive) {
            R.drawable.ic_fire_selected
        } else {
            R.drawable.ic_fire_unselected
        }
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(ActivityDetailColors.Background002)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onDoubleTap = { onDoubleTap() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = when (reactionType) {
                ReactionType.AWESOME -> "멋져요"
                ReactionType.GREAT -> "최고예요"
                ReactionType.AMAZING -> "놀라워요"
                ReactionType.FIGHTING -> "응원해요"
            },
            modifier = Modifier.fillMaxSize(),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun MoreMenuDropdown(
    modifier: Modifier = Modifier,
    isMine: Boolean = true,
    onModifyPosting: () -> Unit,
    onSetThumbnailClick: () -> Unit,
    onDeletePosting: () -> Unit,
    onReportClick: () -> Unit = {},
    hasImage: Boolean = false,
) {
    Surface(
        modifier = modifier.wrapContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = ActivityDetailColors.Background001,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (isMine) {
                if (hasImage) {
                    MoreMenuItem(
                        icon = painterResource(R.drawable.ic_profile_main),
                        text = "대표사진 설정",
                        onClick = onSetThumbnailClick
                    )
                }
                //신고하기 화면 전환 구현해놓음. 신고하기 ui 더 다듬기
                MoreMenuItem(
                    icon = painterResource(R.drawable.ic_pencil_bold),
                    text = "수정하기",
                    onClick = onModifyPosting
                )

                MoreMenuItem(
                    icon = painterResource(R.drawable.ic_trash_bold),
                    text = "삭제하기",
                    onClick = onDeletePosting
                )
            } else {
                MoreMenuItem(
                    icon = painterResource(R.drawable.icon_register),
                    text = "신고하기",
                    onClick = onReportClick
                )
            }
        }
    }
}

@Composable
fun MoreMenuItem(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = rememberThrottledClick { onClick() }
            )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = ActivityDetailColors.Neutral800
            )

            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ActivityDetailColors.Neutral800
            )
        }
    }
}

@Composable
fun ToastMessage2(message: String) {
    Surface(
        modifier = Modifier
            .width(320.dp)
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = ActivityDetailColors.ToastBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(ActivityDetailColors.ToastSuccess),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFF00C853)
                )
            }

            // Message
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ActivityDetailColors.White
            )
        }
    }
}

// 공개범위 설정 바텀시트
@Composable
fun PrivacySettingBottomSheet(
    selectedPrivacy: Boolean, // isPublic
    onPrivacySelected: (Boolean) -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.width(320.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = "활동 공개범위를 설정해주세요.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ActivityDetailColors.Neutral900,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Privacy Options
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 전체공개
                PrivacyOptionCard(
                    icon = Icons.Default.Lock,
                    title = "전체공개",
                    isSelected = selectedPrivacy == true,
                    onClick = { onPrivacySelected(true) }
                )

                // 비공개
                PrivacyOptionCard(
                    icon = Icons.Default.Lock,
                    title = "비공개",
                    isSelected = selectedPrivacy == false,
                    onClick = { onPrivacySelected(false) }
                )
            }
        }

        // Bottom Button Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
        ) {
            // Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ActivityDetailColors.White
                            ),
                            startY = 0f,
                            endY = 88f * 0.61648f
                        )
                    )
            )

            // Confirm Button
            Button(
                onClick = rememberThrottledClick { onConfirmClick() },
                modifier = Modifier
                    .width(328.dp)
                    .height(56.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ActivityDetailColors.Action001
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "설정완료",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActivityDetailColors.White
                )
            }
        }
    }
}

@Composable
fun PrivacyOptionCard(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = rememberThrottledClick { onClick() }),
        shape = RoundedCornerShape(12.dp),
        color = ActivityDetailColors.White,
        border = if (isSelected) {
            BorderStroke(1.dp, ActivityDetailColors.Action001)
        } else {
            BorderStroke(1.dp, ActivityDetailColors.Stroke001)
        },
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected) ActivityDetailColors.Action001 else ActivityDetailColors.Neutral600
                )

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ActivityDetailColors.Neutral800
                )
            }

            // Check Icon
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "선택됨",
                    modifier = Modifier.size(24.dp),
                    tint = ActivityDetailColors.Action001
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ActivityDetailColors.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "이 활동 기록을 삭제하시겠어요?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActivityDetailColors.Neutral900,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "삭제 시 복구는 안돼요!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ActivityDetailColors.Neutral600,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = rememberThrottledClick { onDismiss() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = "닫기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ActivityDetailColors.Neutral600
                        )
                    }
                    Button(
                        onClick = rememberThrottledClick { onConfirm() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ActivityDetailColors.Action001),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = "삭제하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ActivityDetailColors.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslateX"
    )
    val tanAngle = tan((15.0 * PI / 180.0).toFloat())
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFF9F9F9),
            Color(0xFFF2F2F2),
            Color(0xFFEAEAEA),
            Color(0xFFF2F2F2),
            Color(0xFFF9F9F9),
        ),
        start = Offset(translateX * 1000f, translateX * 1000f * tanAngle),
        end = Offset(translateX * 1000f + 600f, translateX * 1000f * tanAngle + 600f * tanAngle),
    )
}

@Composable
private fun SkeletonBox(
    modifier: Modifier = Modifier,
    brush: Brush,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
private fun RoutineDetailSkeletonContent(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 제목/날짜 skeleton
        SkeletonBox(
            modifier = Modifier
                .width(160.dp)
                .height(20.dp),
            brush = shimmerBrush,
            shape = RoundedCornerShape(8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 이미지 skeleton (1:1 비율)
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    brush = shimmerBrush,
                    shape = RoundedCornerShape(16.dp)
                )
                // 이미지 하단 소형 텍스트 skeleton
                SkeletonBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp),
                    brush = shimmerBrush,
                    shape = RoundedCornerShape(8.dp)
                )
            }
            // 메모/내용 skeleton
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                brush = shimmerBrush,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun ProfileImageWithSkeleton(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    val shimmerBrush = rememberShimmerBrush()
    var isImageLoading by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_profile_empty),
            onSuccess = { isImageLoading = false },
            onError = { isImageLoading = false },
            modifier = Modifier.fillMaxSize()
        )
        if (isImageLoading) {
            SkeletonBox(
                modifier = Modifier.fillMaxSize(),
                brush = shimmerBrush,
                shape = CircleShape
            )
        }
    }
}

@Preview
@Composable
fun PreviewActivityDetailScreen() {
    ForDayTheme {
        RoutineDetailContent(
            state = RoutineDetailUiState(),
            onAction = {},
            routineId = 1L,
            onNavigateToRecordRoutine = { _, _ -> },
        )
    }
}