package com.forday.app.presentation.modifyhobby.screen

import com.forday.app.core.logger.analytics.AnalyticsEvents
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.state.ErrorContent
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.designsystem.toast.ErrorToast
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.modifyhobby.ModifyHobbyUiState
import com.forday.app.presentation.modifyhobby.ModifyHobbyViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import timber.log.Timber

// HobbyStatus enum 추가
enum class HobbyStatus {
    IN_PROGRESS,  // 진행중
    ARCHIVED      // 보관함
}

@Serializable
data class HobbyModifyParams(
    val hobbyId: Int,
    val hobbyInfoId: Int? = null,
    val hobbyName: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val goalDays: Int
)

// 화면 크기에 따른 반응형 값을 계산하는 클래스
@Composable
fun rememberResponsiveDimensions(): ResponsiveDimensions {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    return remember(screenWidth) {
        ResponsiveDimensions(
            screenWidth = screenWidth,
            horizontalPadding = when {
                screenWidth < 360.dp -> 16.dp
                screenWidth > 600.dp -> 32.dp
                else -> 20.dp
            },
            cardPadding = when {
                screenWidth < 360.dp -> 12.dp
                else -> 16.dp
            },
            spacing = when {
                screenWidth < 360.dp -> 16.dp
                else -> 20.dp
            }
        )
    }
}

data class ResponsiveDimensions(
    val screenWidth: Dp,
    val horizontalPadding: Dp,
    val cardPadding: Dp,
    val spacing: Dp
)

@Composable
fun ModifyHobbyScreenRoot(
    viewModel: ModifyHobbyViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onStorage: (Int) -> Unit = {},
    onAddHobby: () -> Unit = {},
    onChangeDuration: (HobbyModifyParams) -> Unit,
    onChangeFrequency: (HobbyModifyParams) -> Unit = {},
    onChangeJourneyDays: (HobbyModifyParams) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.logEvent(AnalyticsEvents.MODIFY_HOBBY_SCREEN)

    LaunchedEffect(Unit) {
        viewModel.fetchMyHobbyList()
    }

    val errorData = state.errorData
    if (errorData != null) {
        ErrorContent(
            errorData = errorData,
            onAction = {
                when (errorData.errorType) {
                    ErrorDataUiState.ErrorType.TYPE_BACK -> onBack()
                    ErrorDataUiState.ErrorType.TYPE_RETRY -> viewModel.fetchMyHobbyList()
                }
            }
        )
    } else {
        ModifyHobbyScreen(
            state = state,
            onBackClick = onBack,
            onStorageClick = onStorage,
            onAddHobbyClick = onAddHobby,
            onChangeDuration = { params ->
                onChangeDuration(params)
            },
            onChangeFrequency = { params ->
                onChangeFrequency(params)
            },
            onChangeJourneyDays = { params ->
                onChangeJourneyDays(params)
            },
            onTabChange = { status ->
                viewModel.fetchMyHobbyList(status.name)
            },
            onConfirmStorage = { hobbyId, status, hobbyName ->
                viewModel.modifyHobbyStatus(hobbyId.toLong(), status.name)
            },
            onDismissHobbyLimitDialog = {
                viewModel.dismissHobbyLimitDialog()
            },
            onClearToast = {
                viewModel.clearToast()
            }
        )
    }

}

@Composable
fun ModifyHobbyScreen(
    onBackClick: () -> Unit = {},
    onStorageClick: (Int) -> Unit = {},
    onAddHobbyClick: () -> Unit = {},
    onChangeDuration: (HobbyModifyParams) -> Unit,
    onChangeFrequency: (HobbyModifyParams) -> Unit = {},
    onChangeJourneyDays: (HobbyModifyParams) -> Unit = {},
    onTabChange: (HobbyStatus) -> Unit = {},
    onConfirmStorage: (Int, HobbyStatus, String) -> Unit = { _, _, _ -> },
    onDismissHobbyLimitDialog: () -> Unit = {},
    onClearToast: () -> Unit = {},
    state: ModifyHobbyUiState
) {
    val dimensions = rememberResponsiveDimensions()
    var selectedStatus by remember { mutableStateOf(HobbyStatus.IN_PROGRESS) }
    var showStorageDialog by remember { mutableStateOf(false) }
    var selectedHobby by remember { mutableStateOf<Pair<Int, String>?>(null) }

    Scaffold(
        topBar = {
            HobbyManagementTopBar(onBackClick = onBackClick)
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 타이틀 섹션
                TitleSection(
                    modifier = Modifier.padding(
                        horizontal = dimensions.horizontalPadding,
                        vertical = dimensions.spacing
                    )
                )

                TabMenu(
                    inProgressCount = state.inProgressHobbyCount,
                    archivedCount = state.archivedHobbyCount,
                    selectedStatus = selectedStatus,
                    onTabClick = { status ->
                        selectedStatus = status
                        onTabChange(status)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // LazyColumn으로 변경하여 스크롤 최적화
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = dimensions.horizontalPadding,
                        vertical = dimensions.spacing
                    ),
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacing)
                ) {
                    items(state.hobbies) { hobby ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HobbyCard(
                                hobbyName = hobby.hobbyName,
                                hobbyInfoId = hobby.hobbyInfoId,
                                duration = "${hobby.hobbyTimeMinutes}분",
                                frequency = "주 ${hobby.executionCount}회",
                                journeyDays = if (hobby.goalDays == 0) "기간 미지정" else "${hobby.goalDays}일",
                                onStorageClick = {
                                    selectedHobby = Pair(hobby.hobbyId, hobby.hobbyName)
                                    showStorageDialog = true
                                },
                                dimensions = dimensions,
                                isArchived = selectedStatus == HobbyStatus.ARCHIVED
                            )

                            // 편집 버튼들
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EditButton(
                                    text = "취미 시간 변경",
                                    onClick = {
                                        onChangeDuration(
                                            HobbyModifyParams(
                                                hobbyId = hobby.hobbyId,
                                                hobbyInfoId = hobby.hobbyInfoId,
                                                hobbyName = hobby.hobbyName,
                                                hobbyTimeMinutes = hobby.hobbyTimeMinutes,
                                                executionCount = hobby.executionCount,
                                                goalDays = hobby.goalDays
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                EditButton(
                                    text = "실행 횟수 변경",
                                    onClick = {
                                        onChangeFrequency(
                                            HobbyModifyParams(
                                                hobbyId = hobby.hobbyId,
                                                hobbyInfoId = hobby.hobbyInfoId,
                                                hobbyName = hobby.hobbyName,
                                                hobbyTimeMinutes = hobby.hobbyTimeMinutes,
                                                executionCount = hobby.executionCount,
                                                goalDays = hobby.goalDays
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                EditButton(
                                    text = "여정일 변경",
                                    onClick = {
                                        onChangeJourneyDays(
                                            HobbyModifyParams(
                                                hobbyId = hobby.hobbyId,
                                                hobbyInfoId = hobby.hobbyInfoId,
                                                hobbyName = hobby.hobbyName,
                                                hobbyTimeMinutes = hobby.hobbyTimeMinutes,
                                                executionCount = hobby.executionCount,
                                                goalDays = hobby.goalDays
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // 취미가 2개 미만일 때만 추가 버튼 표시 (진행중 탭일 때만)
                    if (state.hobbies.size < 2 && selectedStatus == HobbyStatus.IN_PROGRESS) {
                        item {
                            AddHobbyButton(
                                onClick = onAddHobbyClick,
                                dimensions = dimensions
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = state.toastTargetTab != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 42.dp)
            ) {
                ErrorToast(
                    message = state.toastMessage.orEmpty(),
                    iconVisible = true,
                    actionLabel = "이동하기",
                    onActionClick = {
                        val target = state.toastTargetTab
                        if (target != null) {
                            val hobbyStatus = HobbyStatus.valueOf(target)
                            selectedStatus = hobbyStatus
                            onTabChange(hobbyStatus)
                        }
                        onClearToast()
                    }
                )
            }

            LaunchedEffect(state.toastTargetTab) {
                if (state.toastTargetTab != null) {
                    delay(2000L)
                    onClearToast()
                }
            }
        }
    }

    // 진행중 취미 최대 초과 다이얼로그
    if (state.showHobbyLimitDialog) {
        HobbyLimitDialog(
            onDismiss = onDismissHobbyLimitDialog,
            onNavigateToInProgress = {
                onDismissHobbyLimitDialog()
                selectedStatus = HobbyStatus.IN_PROGRESS
                onTabChange(HobbyStatus.IN_PROGRESS)
            },
            dimensions = rememberResponsiveDimensions(),
            warningMessage = state.toastMessage.orEmpty(),
        )
    }

    // 다이얼로그 표시
    if (showStorageDialog && selectedHobby != null) {
        StorageConfirmDialog(
            hobbyName = selectedHobby!!.second,
            currentStatus = selectedStatus,
            onDismiss = {
                showStorageDialog = false
                selectedHobby = null
            },
            onConfirm = {
                val targetStatus = when (selectedStatus) {
                    HobbyStatus.IN_PROGRESS -> HobbyStatus.ARCHIVED
                    HobbyStatus.ARCHIVED -> HobbyStatus.IN_PROGRESS
                }
                onConfirmStorage(selectedHobby!!.first, targetStatus, selectedHobby!!.second)
                showStorageDialog = false
                selectedHobby = null
            },
            dimensions = rememberResponsiveDimensions()
        )
    }
}

@Composable
private fun HobbyLimitDialog(
    onDismiss: () -> Unit,
    onNavigateToInProgress: () -> Unit,
    dimensions: ResponsiveDimensions,
    warningMessage: String,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(
                    when {
                        dimensions.screenWidth < 360.dp -> 0.95f
                        dimensions.screenWidth > 600.dp -> 0.7f
                        else -> 0.9f
                    }
                )
                .wrapContentHeight(),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = warningMessage,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "다른 취미를 보관한 후 다시 시도해주세요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF7A7A7A),
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF2F2F2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "닫기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7A7A7A)
                        )
                    }

                    Button(
                        onClick = onNavigateToInProgress,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForDayTheme.color.Primary001
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "보관하러가기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageConfirmDialog(
    hobbyName: String,
    currentStatus: HobbyStatus,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dimensions: ResponsiveDimensions
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(
                    when {
                        dimensions.screenWidth < 360.dp -> 0.95f
                        dimensions.screenWidth > 600.dp -> 0.7f
                        else -> 0.9f
                    }
                )
                .wrapContentHeight(),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // 제목
                Text(
                    text = if (currentStatus == HobbyStatus.IN_PROGRESS) {
                        "'${hobbyName}' 취미를 보관하시겠어요?"
                    } else {
                        "'${hobbyName}' 취미를 꺼내시겠어요?"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 설명
                Text(
                    text = if (currentStatus == HobbyStatus.IN_PROGRESS) {
                        "기록이 저장되어 언제든 다시 시작할 수 있어요."
                    } else {
                        "보관함에서 취미를 꺼내 다시 시작할 수 있어요."
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF7A7A7A),
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 닫기 버튼
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF2F2F2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "닫기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7A7A7A)
                        )
                    }

                    // 보관하기/꺼내기 버튼
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForDayTheme.color.Primary001
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (currentStatus == HobbyStatus.IN_PROGRESS) {
                                "보관하기"
                            } else {
                                "꺼내기"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HobbyManagementTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .background(Color(0xFFF9F9F9))
            .padding(vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "뒤로가기",
            tint = Color(0xFF3A3A3A),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 11.dp)
                .clickable(
                    onClick = onBackClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        Text(
            text = "취미 설정",
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A3A3A)
            )
        )
    }
}

@Composable
private fun TitleSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "내 취미정보를 수정하고 추가하기",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF3A3A3A)
                    )
                ) {
                    append("마음에 드는 취미는 최대 ")
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        color = Color(0xFFF25F59)
                    )
                ) {
                    append("2개")
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF3A3A3A)
                    )
                ) {
                    append("까지 선택할 수 있어요.")
                }
            },
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 19.6.sp
            )
        )
    }
}

@Composable
private fun TabMenu(
    inProgressCount: Int,
    archivedCount: Int,
    selectedStatus: HobbyStatus,
    onTabClick: (HobbyStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            TabItem(
                label = "진행중",
                count = inProgressCount,
                isSelected = selectedStatus == HobbyStatus.IN_PROGRESS,
                onClick = { onTabClick(HobbyStatus.IN_PROGRESS) },
                modifier = Modifier.weight(1f)
            )
            TabItem(
                label = "보관함",
                count = archivedCount,
                isSelected = selectedStatus == HobbyStatus.ARCHIVED,
                onClick = { onTabClick(HobbyStatus.ARCHIVED) },
                modifier = Modifier.weight(1f)
            )
        }

        // 전체 하단 얇은 회색 선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E5E5))
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TabItem(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = rememberThrottledClick { onClick() }
            )
            .then(
                if (isSelected)
                    Modifier.drawBehind {
                        val strokeWidth = 4.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = Color(0xFF3A3A3A),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF3A3A3A) else Color(0xFFB5B5B5)
                )
            )
            Text(
                text = count.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF3A3A3A) else Color(0xFFB5B5B5)
                )
            )
        }
    }
}

@Composable
private fun HobbyCard(
    hobbyName: String,
    hobbyInfoId: Int?,
    duration: String,
    frequency: String,
    journeyDays: String,
    onStorageClick: () -> Unit,
    dimensions: ResponsiveDimensions,
    isArchived: Boolean = false
) {
    val iconRes = when (hobbyInfoId) {
        1 -> R.drawable.ic_draw
        2 -> R.drawable.ic_health
        3 -> R.drawable.ic_book
        4 -> R.drawable.ic_music
        5 -> R.drawable.ic_running
        6 -> R.drawable.ic_cook
        7 -> R.drawable.ic_cafe
        8 -> R.drawable.ic_movie
        9 -> R.drawable.ic_camera2
        10 -> R.drawable.ic_write
        else -> R.drawable.ic_etc_hobby // null이거나 다른 값일 때 기본값
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(
                    horizontal = dimensions.cardPadding,
                    vertical = 12.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // 메타 정보
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        MetaText(duration)
                        DotSeparator()
                        MetaText(frequency)
                        DotSeparator()
                        MetaText(journeyDays)
                    }

                    // 취미 이름
                    Text(
                        text = hobbyName,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3A3A3A)
                        ),
                        maxLines = 1
                    )
                }
            }

            // 보관 버튼
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                IconButton(
                    onClick = onStorageClick,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        painter = painterResource(if (isArchived) R.drawable.ic_out else R.drawable.icon_archive),
                        contentDescription = if (isArchived) "꺼내기" else "보관",
                        tint = Color(0xFF3A3A3A),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = if (isArchived) "꺼내기" else "보관",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0xFF3A3A3A)
                    )
                )
            }
        }
    }
}

@Composable
private fun MetaText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 10.sp,
            color = Color(0xFF7A7A7A)
        )
    )
}

@Composable
private fun DotSeparator() {
    Box(
        modifier = Modifier
            .size(2.dp)
            .background(Color(0xFF7A7A7A), CircleShape)
    )
}

@Composable
private fun EditButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF2F2F2),
            contentColor = Color(0xFF7A7A7A)
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun AddHobbyButton(
    onClick: () -> Unit,
    dimensions: ResponsiveDimensions
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = rememberThrottledClick { onClick() }),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(
                    horizontal = dimensions.cardPadding,
                    vertical = 12.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus_btn),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFB5B5B5)
                )

                Text(
                    text = "취미 추가하기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3A)
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF3A3A3A)
            )
        }
    }
}

@Preview
@Composable
fun ModifyHobbyScreenPreview() {
    ForDayTheme {
        ModifyHobbyScreenRoot(
            onBack = {},
            onStorage = {},
            onChangeFrequency = {},
            onChangeJourneyDays = {},
            onAddHobby = {},
            onChangeDuration = {}
        )
    }
}