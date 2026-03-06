package com.forday.app.presentation.modifyroutine.screen

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.state.ErrorContent
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.designsystem.toast.ErrorToast
import com.forday.app.presentation.modifyroutine.ModifyRoutineViewModel
import com.forday.app.presentation.modifyroutine.RoutineUiModel
import kotlinx.coroutines.delay

@Composable
fun ModifyRoutineScreenRoot(
    hobbyId: Long?,
    onBack: () -> Unit,
    onAddRoutine: () -> Unit,
    onEditRoutine: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModifyRoutineViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRoutineForDelete by remember { mutableStateOf<RoutineUiModel?>(null) }
    var showToast by remember { mutableStateOf(false) }

    // 편집 관련 상태 추가
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRoutineForEdit by remember { mutableStateOf<RoutineUiModel?>(null) }
    var editText by remember { mutableStateOf("") }

    LaunchedEffect(showToast) {
        if (showToast) {
            delay(2_000)
            showToast = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchHobbyRoutineList(hobbyId)
    }

    val errorData = state.errorData
    if (errorData != null) {
        ErrorContent(
            errorData = errorData,
            onAction = {
                when (errorData.errorType) {
                    ErrorDataUiState.ErrorType.TYPE_RETRY ->
                        viewModel.fetchHobbyRoutineList(hobbyId)

                    ErrorDataUiState.ErrorType.TYPE_BACK ->
                        onBack()
                }
            },
        )
    } else {
        ModifyRoutineScreen(
            routineList = state.routines,
            isLoading = state.isLoading,
            onBack = onBack,
            onAddRoutine = onAddRoutine,
            onEditRoutine = onEditRoutine,
            onEditClick = { routine ->
                selectedRoutineForEdit = routine
                editText = routine.content
                showEditDialog = true
            },
            // 삭제 클릭 - 다이얼로그만 표시
            onDeleteClick = { routine ->
                selectedRoutineForDelete = routine
                showDeleteDialog = true
                // viewModel.deleteRoutine(routine.routineId) 제거!
            },
            showDeleteDialog = showDeleteDialog,
            onDismissDeleteDialog = {
                showDeleteDialog = false
                selectedRoutineForDelete = null
            },
            // 삭제 확인 - 실제로 삭제
            onConfirmDelete = {
                selectedRoutineForDelete?.let { routine ->
                    viewModel.deleteRoutine(routine.routineId)  // 여기서 삭제!
                    showDeleteDialog = false
                    selectedRoutineForDelete = null
                    showToast = true
                }
            },
            showEditDialog = showEditDialog,
            editText = editText,
            onEditTextChange = { newText ->
                if (newText.length <= 20) {
                    editText = newText
                }
            },
            onDismissEditDialog = {
                showEditDialog = false
                selectedRoutineForEdit = null
                editText = ""
            },
            onConfirmEdit = {
                selectedRoutineForEdit?.let { routine ->
                    viewModel.modifyRoutine(routine.routineId, editText)
                    showEditDialog = false
                    selectedRoutineForEdit = null
                    editText = ""
                }
            },
            showToast = showToast,
            modifier = modifier
        )
    }

}

@Composable
fun ModifyRoutineScreen(
    routineList: List<RoutineUiModel>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onAddRoutine: () -> Unit,
    onEditRoutine: (Long) -> Unit,
    onEditClick: (RoutineUiModel) -> Unit,
    onDeleteClick: (RoutineUiModel) -> Unit,
    showDeleteDialog: Boolean,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    showEditDialog: Boolean,
    editText: String,
    onEditTextChange: (String) -> Unit,
    onDismissEditDialog: () -> Unit,
    onConfirmEdit: () -> Unit,
    showToast: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 헤더
            ModifyRoutineTopBar(
                onBack = onBack,
                onAdd = onAddRoutine
            )

            // 로딩/에러/빈 상태/리스트 처리
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF9447)
                        )
                    }
                }

                routineList.isEmpty() -> {
                    EmptyRoutineContent(onAddRoutine = onAddRoutine)
                }

                else -> {
                    // 기존 리스트
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "현재 진행하고 있는 활동들이에요.",
                            modifier = Modifier.padding(horizontal = 20.dp),
                            fontSize = 14.sp,
                            color = Color(0xFF7A7A7A)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            routineList.forEach { routine ->
                                RoutineListItem(
                                    routine = routine,
                                    onEdit = { onEditClick(routine) },
                                    onDelete = { onDeleteClick(routine) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 삭제 확인 다이얼로그
        if (showDeleteDialog) {
            DeleteConfirmDialog(
                onDismiss = onDismissDeleteDialog,
                onConfirm = onConfirmDelete
            )
        }

        // 편집 다이얼로그
        if (showEditDialog) {
            EditRoutineDialog(
                currentText = editText,
                onTextChange = onEditTextChange,
                onDismiss = onDismissEditDialog,
                onConfirm = onConfirmEdit
            )
        }
    }
}

@Composable
private fun EmptyRoutineContent(
    onAddRoutine: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 111.dp), // 헤더 아래 여백
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        // 일러스트 (슬픈 아이콘 + 박스)
        Box(
            modifier = Modifier.size(width = 160.dp, height = 162.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // ✅ 슬픈 표정 아이콘 (박스 위에 - 먼저 그려짐)
            Image(
                painter = painterResource(R.drawable.icon_sad),
                contentDescription = "활동 없음",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp) // ✅ 박스 위로 올리기 (아이콘 절반만큼)
            )

            // 박스 일러스트 (나중에 그려짐)
            Image(
                painter = painterResource(R.drawable.box_img),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 160.dp, height = 140.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        // 텍스트 + 버튼
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "진행 중인 활동이 없어요.",
                style = TextStyle(
                    fontFamily = FontFamily.Default, // Pretendard
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp, // 14 * 1.4
                    color = Color(0xFF7A7A7A),
                    textAlign = TextAlign.Center
                )
            )

            // 활동 추가하기 버튼
            Button(
                onClick = onAddRoutine,
                modifier = Modifier
                    .width(288.dp)
                    .heightIn(min = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF1E6) // Primary/003
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(
                    horizontal = 40.dp,
                    vertical = 11.5.dp
                )
            ) {
                Text(
                    text = "활동 추가하기",
                    style = TextStyle(
                        fontFamily = FontFamily.Default, // Pretendard
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 16.8.sp, // 14 * 1.2
                        color = Color(0xFFFF9447)
                    )
                )
            }
        }
    }
}

@Composable
private fun ModifyRoutineTopBar(
    onBack: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_chevron_left),
            contentDescription = "뒤로가기",
            tint = Color(0xFF1E1E1E),
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    onClick = onBack,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
//                .padding(10.dp)  // Icon 자체 크기 조절 (44dp 안에서 적절한 크기로)
        )

        Text(
            text = "활동 리스트",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )

        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = "추가",
            tint = Color(0xFF1E1E1E),
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    onClick = rememberThrottledClick { onAdd() },
                    indication = null,  // ripple 효과 제거
                    interactionSource = remember { MutableInteractionSource() }
                )
//                .padding(12.dp)  // IconButton의 기본 패딩과 유사하게
        )
    }
}

@Composable
private fun RoutineListItem(
    routine: RoutineUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val density = LocalDensity.current
    var aiIconOffsetY by remember { mutableStateOf(0.dp) }
    var stickerRowSize by remember { mutableStateOf(IntSize.Zero) }
    var stickerOffsetX by remember { mutableStateOf(0.dp) }
    var stickerOffsetY by remember { mutableStateOf(0.dp) }
    var textBoxWidthPx by remember { mutableIntStateOf(0) }
    var lastLineRightPx by remember { mutableFloatStateOf(0f) }
    var lastLineTopPx by remember { mutableFloatStateOf(0f) }
    var lastLineBottomPx by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(
        lastLineRightPx,
        lastLineTopPx,
        lastLineBottomPx,
        stickerRowSize,
        textBoxWidthPx
    ) {
        if (textBoxWidthPx <= 0) return@LaunchedEffect
        if (stickerRowSize.height <= 0 || stickerRowSize.width <= 0) return@LaunchedEffect

        val gapPx = with(density) { 8.dp.toPx() }
        val stickerW = stickerRowSize.width.toFloat()
        val stickerH = stickerRowSize.height.toFloat()

        val lastLineCenterY = (lastLineTopPx + lastLineBottomPx) / 2f
        val desiredX = lastLineRightPx + gapPx
        val fitsSameLine = desiredX + stickerW <= textBoxWidthPx

        val targetXPx = if (fitsSameLine) desiredX else 0f
        val targetYPx = if (fitsSameLine) {
            (lastLineCenterY - stickerH / 2f)
        } else {
            // 다음 줄로 내려보내되, 마지막 줄 높이 안에서 중앙 정렬 느낌 유지
            val lineHeight = (lastLineBottomPx - lastLineTopPx).coerceAtLeast(0f)
            (lastLineBottomPx + (lineHeight - stickerH) / 2f)
        }

        stickerOffsetX = with(density) { targetXPx.coerceAtLeast(0f).toDp() }
        stickerOffsetY = with(density) { targetYPx.coerceAtLeast(0f).toDp() }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ✅ 활동 이름 + 스티커 영역
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)  // ✅ 8dp 간격
            ) {
                // AI 아이콘 + 활동 이름을 Column으로 묶기
                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // AI 추천 아이콘
                        if (routine.isAiRecommended) {
                            Icon(
                                painter = painterResource(R.drawable.ic_ai_list),
                                contentDescription = "AI",
                                modifier = Modifier
                                    .offset(y = aiIconOffsetY)
                                    .size(14.dp),  // ✅ 첫 줄 중앙 맞춤
                                tint = Color.Unspecified
                            )
                        }

                        // 활동 이름 + 스티커(텍스트 마지막 줄 끝 기준 8dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .onSizeChanged { textBoxWidthPx = it.width }
                        ) {
                            Text(
                                text = routine.content,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E1E1E),
                                lineHeight = 19.6.sp,
                                onTextLayout = { layoutResult: TextLayoutResult ->
                                    val lastLineIndex = (layoutResult.lineCount - 1).coerceAtLeast(0)
                                    lastLineRightPx = layoutResult.getLineRight(lastLineIndex)
                                    lastLineTopPx = layoutResult.getLineTop(lastLineIndex)
                                    lastLineBottomPx = layoutResult.getLineBottom(lastLineIndex)

                                    if (routine.isAiRecommended) {
                                        val firstLineCenterPx =
                                            (layoutResult.getLineTop(0) + layoutResult.getLineBottom(0)) / 2f
                                        val iconHalfPx = with(density) { (14.dp / 2).toPx() }
                                        val targetTopPx = (firstLineCenterPx - iconHalfPx).coerceAtLeast(0f)
                                        val newOffset = with(density) { targetTopPx.toDp() }

                                        if (newOffset != aiIconOffsetY) {
                                            aiIconOffsetY = newOffset
                                        }
                                    }
                                }
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .offset(x = stickerOffsetX, y = stickerOffsetY)
                                    .onSizeChanged { stickerRowSize = it }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_routinelist),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                                Text(
                                    text = routine.collectedStickerNum.toString(),
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    color = Color(0xFF9E9E9E),
                                    style = LocalTextStyle.current.copy(
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 편집 + 삭제 버튼 영역
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "편집",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            onClick = rememberThrottledClick { onEdit() },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color(0xFFB5B5B5)
                )

                if (routine.isDeletable) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = "삭제",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                onClick = rememberThrottledClick { onDelete() },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        tint = Color(0xFFB5B5B5)
                    )
                }
            }
        }
    }
}

// ✅ 편집 다이얼로그
@Composable
private fun EditRoutineDialog(
    currentText: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),  // ✅ 피그마 확인
            color = Color.White,
            modifier = Modifier

                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)  // ✅ 상단만 24dp
            ) {
                // 제목 + 닫기 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),  // ✅ 좌우 20dp, 상하 10dp
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "활동 수정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E1E),
                        lineHeight = 21.6.sp  // ✅ 18 * 1.2 = 21.6
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "닫기",
                        tint = Color(0xFF1E1E1E),  // ✅ Neutral/900
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                onClick = onDismiss,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    )
                }

                // TextField 영역
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 20.dp)  // ✅ 상단 20dp, 하단 20dp
                ) {
                    // TextField 내부 영역
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)  // ✅ 8dp 간격
                    ) {
                        // 입력 필드 + 지우기 버튼
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),  // ✅ 8dp 간격
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 텍스트 입력
                            Box(modifier = Modifier.weight(1f)) {
                                BasicTextField(
                                    value = currentText,
                                    onValueChange = onTextChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 14.sp,
                                        color = Color(0xFF1E1E1E),
                                        lineHeight = 19.6.sp  // ✅ 14 * 1.4 = 19.6
                                    ),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        innerTextField()
                                    }
                                )
                            }

                            // 지우기 버튼
                            if (currentText.isNotEmpty()) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close_button),
                                    contentDescription = "지우기",
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable(
                                            onClick = rememberThrottledClick { onTextChange("") },
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ),
                                    tint = Color(0xFF1E1E1E)
                                )
                            }
                        }

                        // 밑줄
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color(0xFFB5B5B5)  // ✅ Neutral/400
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))  // ✅ TextField와 글자수 간격

                    // 글자 수
                    Text(
                        text = "${currentText.length}/20",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 10.sp,
                        color = Color(0xFFB5B5B5),  // ✅ Neutral/400
                        textAlign = TextAlign.End,
                        lineHeight = 14.sp  // ✅ 10 * 1.4 = 14
                    )
                }

                // 수정완료 버튼
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)  // ✅ fillMaxWidth 전에 padding
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .heightIn(min = 40.dp),  // ✅ 최소 높이만 지정, 콘텐츠에 따라 확장 가능
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9447),
                        disabledContainerColor = Color(0xFFFF9447).copy(alpha = 0.5f)  // ✅ 비활성 상태 색상
                    ),
                    shape = RoundedCornerShape(40.dp),
                    enabled = currentText.isNotBlank(),
                    contentPadding = PaddingValues(  // ✅ 버튼 내부 패딩 명시
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
                ) {
                    Text(
                        text = "수정완료",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 16.8.sp
                    )
                }
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
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "이 활동을 삭제하시겠어요?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "삭제 시 복구는 안돼요!",
                    fontSize = 14.sp,
                    color = Color(0xFF7A7A7A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 닫기 버튼
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF2F2F2)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "닫기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A7A7A)
                        )
                    }

                    // 삭제하기 버튼
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9447)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "삭제하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteSuccessToast() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xAE000000)  // ✅ rgba(0, 0, 0, 0.68) - 반투명 검은색
    ) {
        Text(
            text = "활동이 삭제되었습니다.",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),  // ✅ 12dp로 변경
            fontSize = 14.sp,  // ✅ sp 단위로 해상도 대응
            fontWeight = FontWeight.Bold,  // ✅ Medium → Bold
            color = Color.White,
            textAlign = TextAlign.Start,
            lineHeight = 16.8.sp  // ✅ 14sp × 1.2 = 16.8sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModifyRoutineScreenPreview() {
    ModifyRoutineScreenRoot(
        hobbyId = 1L,
        onBack = {},
        onAddRoutine = {},
        onEditRoutine = {}
    )
}