package com.forday.app.presentation.sosik.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.mypage.MyPageViewModel
import timber.log.Timber

private val ColorWhite        = Color(0xFFFFFFFF)
private val ColorTextPrimary  = Color(0xFF1E1E1E)
private val ColorTextSecond   = Color(0xFF3A3A3A)
private val ColorTextDisabled = Color(0xFFB5B5B5)
private val ColorDivider      = Color(0xFFE5E5E5)
private val ColorPrimary      = Color(0xFFFF9447)
private val ColorPrimaryLight = Color(0xFFFFF5EE)
private val ColorPrimaryText  = Color(0xFFFF8E3C)
private val ColorBtnDisabled  = Color(0xFFE5E5E5)
private val ColorDimmer       = Color(0x99000000)  // rgba(0,0,0,0.6)

// ─────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────

val REPORT_REASONS = listOf(
    "스팸 · 광고",
    "욕설 · 혐오 발언",
    "성적인 콘텐츠",
    "폭력적 · 위험한 콘텐츠",
    "개인정보 침해",
    "저작권 침해",
    "허위 정보",
    "사기 · 사칭",
    "기타"
)

// ─────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────

/**
 * 신고하기 ScreenRoot — ViewModel 연결
 */
@Composable
fun ReportScreenRoot(
    recordId: Int,
    nickname: String,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onNavigateToSosik: () -> Unit,
    userId: String? = null,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    Timber.e("@@@@@@@#@#@#@userId : "+userId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showResultSheet by remember { mutableStateOf(false) }

    // 신고 성공 → 바텀시트 표시
    LaunchedEffect(uiState.reportPostingSuccess) {
        if (uiState.reportPostingSuccess) {
            showResultSheet = true
            viewModel.resetReportPostingSuccess()
        }
    }

    // 유저 신고 성공 → SosikScreen으로 이동 (토스트는 ViewModel에서 처리)
    LaunchedEffect(uiState.reportUserSuccess) {
        if (uiState.reportUserSuccess) {
            viewModel.resetReportUserSuccess()
            onNavigateToSosik()
        }
    }

    // 차단 성공 → SosikScreen으로 이동
    LaunchedEffect(uiState.blockUserSuccess) {
        if (uiState.blockUserSuccess) {
            viewModel.resetBlockUserSuccess()
            viewModel.clearReportedWriterId()
            onNavigateToSosik()
        }
    }

    ReportScreen(
        nickname = nickname,
        showResultSheet = showResultSheet,
        onBack = onBack,
        onSubmit = { reason ->
            if (userId != null) {
                Timber.e("1@@@@@@@@@@@@@@@ "+userId)
                viewModel.reportUser(userId, reason)
            } else {
                Timber.e("2@@@@@@@@@@@@@@@ "+userId)
                viewModel.reportPosting(recordId, reason)
            }
        },
        onComplete = { shouldBlock ->
            if (shouldBlock) {
                // 차단 API 호출 → 결과는 LaunchedEffect(blockUserSuccess)에서 처리
                viewModel.blockUser(uiState.reportedWriterId)
            } else {
                // 차단 없이 완료 → 즉시 뒤로가기
                viewModel.clearReportedWriterId()
                onComplete()
            }
        }
    )
}

/**
 * 신고하기 화면
 * @param nickname          신고 대상 닉네임 (차단 옵션에 사용)
 * @param showResultSheet   신고 완료 바텀시트 표시 여부
 * @param onBack            뒤로가기
 * @param onSubmit          신고 제출 (reason → API 호출)
 * @param onComplete        "완료" 후 콜백 (shouldBlock: 차단 여부)
 */
@Composable
fun ReportScreen(
    nickname: String = "유저닉네임",
    showResultSheet: Boolean = false,
    onBack: () -> Unit = {},
    onSubmit: (String) -> Unit = {},
    onComplete: (shouldBlock: Boolean) -> Unit = {}
) {
    // ── State ──
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var blockUser by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(ColorWhite)) {

        // ── Main Content ──
        Column(modifier = Modifier.fillMaxSize()) {

            // 헤더
            ReportHeader(onBack = onBack)

            // 스크롤 가능한 본문
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 안내 텍스트
                ReportGuideText()

                // 신고 사유 목록
                ReportReasonList(
                    reasons = REPORT_REASONS,
                    selectedReason = selectedReason,
                    onReasonSelected = { selectedReason = it }
                )

                // 하단 버튼 높이만큼 여백
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // ── 하단 제출 버튼 ──
        ReportSubmitButton(
            enabled = selectedReason != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { selectedReason?.let { onSubmit(it) } }
        )

        // ── 딤 + 결과 바텀시트 ──
        AnimatedVisibility(
            visible = showResultSheet,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorDimmer)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* 딤 클릭 시 닫지 않음 */ }
            )
        }

        AnimatedVisibility(
            visible = showResultSheet,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ReportResultBottomSheet(
                nickname = nickname,
                blockUser = blockUser,
                onBlockChanged = { blockUser = it },
                onComplete = { onComplete(blockUser) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────

@Composable
private fun ReportHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(ColorWhite)
    ) {
        // 뒤로가기 아이콘
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = ForDayTheme.color.Gray800,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack
                    )
            )
        }

        // 타이틀
        Text(
            text = "신고하기",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextSecond
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Guide Text
// ─────────────────────────────────────────────────────────────

@Composable
private fun ReportGuideText() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "해당 게시글이나 유저에\n어떤 문제가 있나요?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary,
            lineHeight = 24.sp
        )
        Text(
            text = "회원님의 신고는 익명으로 처리됩니다.\n신고사유를 선택해주세요.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = ColorTextSecond,
            lineHeight = 19.6.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Reason List
// ─────────────────────────────────────────────────────────────

@Composable
private fun ReportReasonList(
    reasons: List<String>,
    selectedReason: String?,
    onReasonSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        reasons.forEach { reason ->
            ReportReasonItem(
                label = reason,
                isSelected = reason == selectedReason,
                onClick = { onReasonSelected(reason) }
            )
        }
    }
}

@Composable
private fun ReportReasonItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor    by animateColorAsState(if (isSelected) ColorPrimaryLight else ColorWhite, label = "bg")
    val textColor  by animateColorAsState(if (isSelected) ColorPrimaryText  else ColorTextSecond, label = "text")
    val borderColor by animateColorAsState(if (isSelected) Color.Transparent else ColorDivider, label = "border")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .then(
                if (!isSelected) Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            lineHeight = 19.6.sp,
            modifier = Modifier.weight(1f)
        )

        // 체크 아이콘
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            CheckIcon()
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Submit Button
// ─────────────────────────────────────────────────────────────

@Composable
private fun ReportSubmitButton(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val btnColor by animateColorAsState(
        targetValue = if (enabled) ColorPrimary else ColorBtnDisabled,
        animationSpec = tween(200),
        label = "btn"
    )
    val textColor by animateColorAsState(
        targetValue = if (enabled) ColorWhite else ColorTextDisabled,
        animationSpec = tween(200),
        label = "btnText"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0x00FFFFFF), Color(0xFFFFFFFF)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(btnColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "제출하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Result Bottom Sheet
// ─────────────────────────────────────────────────────────────

@Composable
private fun ReportResultBottomSheet(
    nickname: String,
    blockUser: Boolean,
    onBlockChanged: (Boolean) -> Unit,
    onComplete: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(ColorWhite)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 콘텐츠 영역
        Column(
            modifier = Modifier
                .width(320.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 감사 메시지
            Text(
                text = "신고가 완료되었습니다.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Start
            )

            // 차단 카드
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorWhite)
                    .border(1.dp, ColorDivider, RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBlockChanged(!blockUser) }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${nickname} 님 차단하기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorTextSecond,
                        modifier = Modifier.weight(1f)
                    )
                    RoundCheckbox(checked = blockUser, onCheckedChange = onBlockChanged)
                }
            }
        }

        // 완료 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorPrimary)
                    .clickable { onComplete(blockUser) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "완료",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    color = ColorWhite
                )
            }
        }

        // Safe Area 여백
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ─────────────────────────────────────────────────────────────
// Custom Checkbox (rounded)
// ─────────────────────────────────────────────────────────────

@Composable
private fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (checked) ColorPrimary else ColorWhite,
        label = "checkBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) ColorPrimary else Color(0xFFD1D1D1),
        label = "checkBorder"
    )

    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.1.dp, borderColor, CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            // 체크 표시 (실제 구현시 Icon(painterResource(R.drawable.ic_check)))
            CheckMarkCanvas(color = ColorWhite, size = 10.dp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Canvas Icon Helpers (실제 프로젝트에서는 벡터 리소스로 교체)
// ─────────────────────────────────────────────────────────────

@Composable
private fun CheckIcon() {
    // 오렌지 체크 아이콘 — 실제 구현 시 Icon(painterResource(R.drawable.ic_check_orange)) 로 교체
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(ColorPrimary),
        contentAlignment = Alignment.Center
    ) {
        CheckMarkCanvas(color = ColorWhite, size = 10.dp)
    }
}

@Composable
private fun CheckMarkCanvas(color: Color, size: androidx.compose.ui.unit.Dp) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.1f, h * 0.5f)
            lineTo(w * 0.4f, h * 0.8f)
            lineTo(w * 0.9f, h * 0.2f)
        }
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.5.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ReportScreenDefaultPreview() {
    // State 1: 미선택 (버튼 비활성)
    ReportScreen()
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ReportScreenSelectedPreview() {
    // State 2: 항목 선택됨 (버튼 활성)
    var selected by remember { mutableStateOf<String?>("욕설 · 혐오 발언") }
    ReportScreen()
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ReportScreenSubmittedPreview() {
    // State 3: 제출 후 바텀시트
    ReportScreen()
}