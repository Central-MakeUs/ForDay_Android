package com.forday.app.presentation.sosik.screen

import com.forday.app.core.logger.analytics.AnalyticsEvents
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.forday.app.presentation.mypage.main.GuestLoginBottomSheet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dayn.forday.R
import com.forday.app.core.util.getStickerDrawableResId
import com.forday.app.presentation.sosik.SosikViewModel
import com.forday.app.presentation.sosik.model.SosikRecordUiModel
import com.forday.app.presentation.sosik.model.SosikTabUiModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Button
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.material3.ButtonDefaults
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

private val ColorBackground        = Color(0xFFFFFFFF)
private val ColorTextPrimary       = Color(0xFF1E1E1E)
private val ColorTextSecond        = Color(0xFF9E9E9E)
private val ColorTabActive         = Color(0xFF3A3A3A)
private val ColorTabInactive       = Color(0xFFB5B5B5)
private val ColorDivider           = Color(0xFFE5E5E5)
private val ColorSkeletonBase      = Color(0xFFF9F9F9)
private val ColorSkeletonHighlight = Color(0xFFF2F2F2)


// ─────────────────────────────────────────────────────────────
// Shimmer Brush — linear-gradient(90deg, #F9F9F9 → #F2F2F2 → #F9F9F9)
// ─────────────────────────────────────────────────────────────

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    return Brush.linearGradient(
        colors = listOf(ColorSkeletonBase, ColorSkeletonHighlight, ColorSkeletonBase),
        start = Offset(translateAnim - 300f, 0f),
        end = Offset(translateAnim, 0f)
    )
}

// ─────────────────────────────────────────────────────────────
// SosikSkeletonContent — 그리드 영역만 대체하는 스켈레톤
// ─────────────────────────────────────────────────────────────

@Composable
private fun SosikSkeletonContent() {
    val skeletonHeights = listOf(156.dp, 208.dp, 117.dp, 183.dp, 156.dp, 106.dp)
    val shimmer = shimmerBrush()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 20.dp,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        itemsIndexed(skeletonHeights) { _, imageHeight ->
            SosikSkeletonCard(imageHeight = imageHeight, shimmer = shimmer)
        }
    }
}

@Composable
private fun SosikSkeletonCard(imageHeight: Dp, shimmer: Brush) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 이미지 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(shimmer)
        )
        // 제목 텍스트
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmer)
        )
        // 프로필 + 좋아요
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(shimmer)
                )
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmer)
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(shimmer)
            )
        }
    }
}

@Composable
fun SosikScreenRoot(
    modifier: Modifier = Modifier,
    onAddHobbyClick: () -> Unit = {},
    onCardClick: (Long) -> Unit = {},
    onProfileClick: (String, Boolean) -> Unit = { _, _ -> },
    viewModel: SosikViewModel = hiltViewModel()
) {
    val context = LocalContext.current.applicationContext
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showGuestBottomSheet by remember { mutableStateOf(false) }
    var dismissedByUser by remember { mutableStateOf(false) }
    val listState = remember(state.selectedTabIndex) { LazyStaggeredGridState() }

    val isNearBottom by remember(state.selectedTabIndex) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.maxByOrNull { it.index }?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(isNearBottom) {
        if (isNearBottom && state.content.hasNext && !state.isLoadingMore) {
            viewModel.loadMorePeopleRoutineList()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.logEvent(AnalyticsEvents.SOSIK_SCREEN)
        viewModel.fetchPeopleRoutineList()
        viewModel.getUserLoginInfo()
    }

    LaunchedEffect(state.socialType, state.hasShownGuestBottomSheet) {
        if (!state.hasShownGuestBottomSheet && state.socialType != null) {
            if (state.socialType == "GUEST" && !dismissedByUser) {
                showGuestBottomSheet = true
                viewModel.markGuestBottomSheetShown()
            }
        }
        if (state.socialType != null && state.socialType != "GUEST") {
            showGuestBottomSheet = false
        }
    }

    SosikScreen(
        modifier = modifier,
        isLoading = state.isLoading,
        isLoadingMore = state.isLoadingMore,
        recordList = if (state.isLoading) emptyList() else state.content.recordList,
        tabList = state.content.tabList,
        selectedHobbyTab = state.selectedTabIndex,
        onHobbyTabSelected = { index ->
            if (state.socialType == "KAKAO") viewModel.selectTab(index)
            else showGuestBottomSheet = true
        },
        listState = listState,
        onLikeClick = { recordId, isPressedAwesome ->
            if (state.socialType == "KAKAO") {
                viewModel.toggleAwesome(recordId)
                if (isPressedAwesome) {
                    viewModel.cancelReaction(recordId.toInt(), "GREAT")
                } else {
                    viewModel.reactionToPosting(recordId.toInt(), "GREAT")
                }
            } else showGuestBottomSheet = true
        },
        onCardClick = { recordId ->
            if (state.socialType == "KAKAO") onCardClick(recordId)
            else showGuestBottomSheet = true
        },
        onProfileClick = { userId, recordAuthor ->
            if (state.socialType == "KAKAO") {
                onProfileClick(userId, recordAuthor)
            } else showGuestBottomSheet = true
        }
    )

    if (showGuestBottomSheet) {
        GuestLoginBottomSheet(
            onDismiss = {
                showGuestBottomSheet = false
                dismissedByUser = true
            },
            onKakaoLogin = {
                showGuestBottomSheet = false
                dismissedByUser = true
                viewModel.loginWithKakao(context, "KAKAO")
            }
        )
    }
}

@Composable
fun SosikScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isLoadingMore: Boolean = false,
    recordList: List<SosikRecordUiModel> = emptyList(),
    tabList: List<SosikTabUiModel> = emptyList(),
    selectedHobbyTab: Int = 0,
    onHobbyTabSelected: (Int) -> Unit = {},
    onLikeClick: (Long, Boolean) -> Unit = { _, _ -> },
    onCardClick: (Long) -> Unit = {},
    onProfileClick: (String, Boolean) -> Unit = { _, _ -> },
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        SosikTopBar(
            tabs = listOf("전체") + tabList.map { it.hobbyName },
            selectedTab = selectedHobbyTab,
            onTabSelected = onHobbyTabSelected
        )

        if (isLoading) {
            SosikSkeletonContent()
        } else {
            if (recordList.isEmpty()) {
                SosikNoContentEmpty()
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 20.dp,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    itemsIndexed(recordList, key = { _, item -> item.recordId }) { _, item ->
                        SosikCard(item = item, onLikeClick = onLikeClick, onCardClick = onCardClick, onProfileClick = onProfileClick)
                    }
                    if (isLoadingMore) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFFFF9447),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Top Bar  — 헤더 "소식" + 취미 탭
// ─────────────────────────────────────────────────────────────

@Composable
private fun SosikTopBar(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBackground)
    ) {
        // FIX 1: 헤더 영역 — 피그마 스펙 54dp 유지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "소식",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary
            )
        }

        // FIX 2: 탭 구분선 — Row 전체에 회색 1dp 선을 깔고,
        //         선택 탭에서 진한 2dp 선으로 덮는 구조로 분리
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 배경 구분선 (전체 너비, 회색 1dp)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ColorDivider)
                    .align(Alignment.BottomCenter)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                tabs.forEachIndexed { index, label ->
                    val isSelected = index == selectedTab

                    // FIX 3: 탭 높이 54dp → 40dp (피그마 스펙)
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .then(
                                // 선택된 탭만 진한 2dp 선으로 덮어 회색 선을 가림
                                if (isSelected)
                                    Modifier.drawBottomBorder(ColorTabActive, strokeWidth = 2.dp)
                                else
                                    Modifier
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(index) }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) ColorTabActive else ColorTabInactive
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Empty State — 기록 없음
// ─────────────────────────────────────────────────────────────

@Composable
private fun SosikNoContentEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Box(
            modifier = Modifier.size(width = 160.dp, height = 162.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.icon_sad),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp)
            )
            Image(
                painter = painterResource(R.drawable.box_img),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 160.dp, height = 140.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        Text(
            text = "아직 활동기록이 존재하지 않아요",
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                color = Color(0xFF7A7A7A),
                textAlign = TextAlign.Center
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Empty State — 진행 중인 취미 없음
// ─────────────────────────────────────────────────────────────

@Composable
private fun SosikEmptyContent(onAddHobbyClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 144.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Box(
            modifier = Modifier.size(width = 160.dp, height = 162.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.icon_sad),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp)
            )
            Image(
                painter = painterResource(R.drawable.box_img),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 160.dp, height = 140.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "진행 중인 취미가 없어요.",
                style = TextStyle(
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp,
                    color = Color(0xFF7A7A7A),
                    textAlign = TextAlign.Center
                )
            )
            Button(
                onClick = onAddHobbyClick,
                interactionSource = remember { NoRippleInteractionSource() },
                modifier = Modifier
                    .width(288.dp)
                    .heightIn(min = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF1E6)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(
                    horizontal = 40.dp,
                    vertical = 11.5.dp
                )
            ) {
                Text(
                    text = "취미 추가하기",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 16.8.sp,
                        color = Color(0xFFFF9447)
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Card — 활동 카드 (Staggered 2열 그리드)
// ─────────────────────────────────────────────────────────────

@Composable
private fun SosikCard(item: SosikRecordUiModel, onLikeClick: (Long, Boolean) -> Unit, onCardClick: (Long) -> Unit, onProfileClick: (String, Boolean) -> Unit) {
    // FIX 5: 그라디언트 방향 — 피그마 134° 방향으로 보정
    //         134° ≈ 우상단 → 좌하단 방향
    val stickerGradientColors = when (getStickerDrawableResId(item.sticker)) {
        R.drawable.ic_sticker_smile -> listOf(Color(0xFFFFE6D1), Color(0xFFF4A261))
        R.drawable.ic_sticker_sad   -> listOf(Color(0xFFDDF2D8), Color(0xFFA8D8A2))
        R.drawable.ic_sticker_laugh -> listOf(Color(0xFFC9DBFF), Color(0xFF8FB3FF))
        R.drawable.ic_sticker_angry -> listOf(Color(0xFFFFF4CC), Color(0xFFFFD966))
        else                        -> listOf(Color(0xFFDDF2D8), Color(0xFFA8D8A2))
    }

    var isImageLoading by remember(item.imageUrl) { mutableStateOf(item.imageUrl.isNotEmpty()) }
    var isImageFailed by remember(item.imageUrl) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCardClick(item.recordId) }
                .then(
                    when {
                        item.imageUrl.isNotEmpty() && !isImageFailed -> Modifier.fillMaxWidth().wrapContentHeight().heightIn(min = 120.dp)
                        item.imageUrl.isNotEmpty() && isImageFailed -> Modifier.fillMaxWidth().aspectRatio(1f)
                        item.memo.isNotEmpty() -> Modifier.fillMaxWidth().aspectRatio(1f)
                        else -> Modifier.fillMaxWidth().aspectRatio(4f / 3f)
                    }
                )
                .clip(RoundedCornerShape(8.dp))
        ) {
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.FillWidth,
                    onSuccess = { isImageLoading = false },
                    onError = { isImageLoading = false; isImageFailed = true },
                    modifier = Modifier.fillMaxWidth()
                )
                if (isImageLoading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(shimmerBrush())
                    )
                } else if (isImageFailed) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0xFFF2F2F2))
                    )
                }
            } else {
                // FIX 5 적용: 134° 방향 그라디언트
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = stickerGradientColors,
                                start = Offset(Float.POSITIVE_INFINITY, 0f),   // 우상단
                                end = Offset(0f, Float.POSITIVE_INFINITY)      // 좌하단
                            )
                        )
                        .padding(12.dp)
                ) {
                    // FIX 6: 따옴표 아이콘 + 메모 텍스트 겹침 방지 — Column으로 정렬
                    if (item.memo.isNotEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_text_ttaompyo),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.memo,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 17.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (item.sticker.isNotEmpty()) {
                Icon(
                    painter = painterResource(getStickerDrawableResId(item.sticker)),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
                        .size(36.dp)
                )
            }
        }

        Text(
            text = item.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = ColorTextPrimary,
            lineHeight = 19.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCardClick(item.recordId) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FIX 7: 프로필 이미지 — placeholder/error 사용 시 background 중복 제거
                AsyncImage(
                    model = item.profileImageUrl,
                    contentDescription = "${item.nickname} 프로필",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_profile_empty),
                    error = painterResource(R.drawable.ic_profile_empty),
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.dp, ColorDivider, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onProfileClick(item.userId, item.recordAuthor) }
                )
                Text(
                    text = item.nickname,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorTextSecond,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onProfileClick(item.userId, item.recordAuthor) }
                )
            }

            // FIX 8: 좋아요 아이콘 크기 28dp → 24dp (피그마 스펙)
            Icon(
                painter = painterResource(
                    if (item.pressedAweSome) R.drawable.ic_good_selected
                    else R.drawable.ic_good_unselected2
                ),
                contentDescription = "좋아요",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onLikeClick(item.recordId, item.pressedAweSome) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Modifier Extensions
// ─────────────────────────────────────────────────────────────

private fun Modifier.drawBottomBorder(color: Color, strokeWidth: Dp = 1.dp): Modifier =
    drawBehind {
        val y = size.height - strokeWidth.toPx() / 2
        drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth.toPx())
    }

// ─────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────

private fun previewRecordList() = listOf(
    SosikRecordUiModel(
        1L,
        "",
        "미라클 모닝 독서 활동명이 들어갑니다",
        "유저닉네임",
        "",
        false,
        "smile",
        "오늘은 어쩌고 저쩌고 어쩌고 저쩌고 어쩌고 저쩌고...",
        hobbyName = TODO(),
        userId = TODO(),
        recordAuthor = TODO()
    ),
    SosikRecordUiModel(
        2L, "", "야외에서 책 읽기", "유저닉네임", "", false, "angry", "오늘은 어쩌고 저쩌고 어쩌고 저쩌고 어쩌고 저쩌고...",
        hobbyName = TODO(),
        userId = TODO(),
        recordAuthor = TODO()
    ),
    SosikRecordUiModel(
        3L, "", "도서관에서 책 빌리기", "유저닉네임", "", true, "sad", "오늘은 어쩌고 저쩌고 어쩌고 저쩌고 어쩌고 저쩌고...",
        hobbyName = TODO(),
        userId = TODO(),
        recordAuthor = TODO()
    ),
    SosikRecordUiModel(
        4L, "", "내 기분과 비슷한 문장 찾기", "유저닉네임", "", false, "laugh", "오늘은 어쩌고 저쩌고 어쩌고 저쩌고 어쩌고 저쩌고...",
        hobbyName = TODO(),
        userId = TODO(),
        recordAuthor = TODO()
    ),
)

private fun previewTabList() = listOf(
    SosikTabUiModel(1L, "독서", true),
    SosikTabUiModel(2L, "헬스", false),
)

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun SosikScreenPreview() {
    SosikScreen(
        recordList = previewRecordList(),
        tabList = previewTabList(),
        selectedHobbyTab = 0
    )
}