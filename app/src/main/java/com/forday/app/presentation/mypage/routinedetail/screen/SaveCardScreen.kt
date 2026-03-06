package com.forday.app.presentation.mypage.routinedetail.screen


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.forday.app.core.designsystem.toast.ErrorToast
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dayn.forday.R
import kotlinx.coroutines.launch

// ── 색상 정의 ──────────────────────────────────────────────────
private val OrangeMain = Color(0xFFFF8C42)  // 저장하기 버튼, 페이지 인디케이터
private val TextGray = Color(0xFF888888)
private val TextDark = Color(0xFF222222)

// ── 감정(Mood) 타입 정의 ───────────────────────────────────────
enum class MoodType {
    HAPPY,  // smile  → #EE9449 오렌지
    JOY,    // laugh  → #8FB3FF 블루
    TIRED,  // sad    → #A8D8A2 그린
    ANGRY   // angry  → #FFD56A 옐로우
}

/**
 * 감정 타입별 베이스 색상 반환
 * 피그마 디자인 기준:
 *   - 양 끝: baseColor.copy(alpha = 0.20f)
 *   - 중간:  baseColor (100%)
 *   - 전체 Gradient opacity: 60%
 */
fun MoodType.toThemeColor(): Color = when (this) {
    MoodType.HAPPY -> Color(0xFFEE9449)
    MoodType.JOY   -> Color(0xFF8FB3FF)
    MoodType.TIRED -> Color(0xFFA8D8A2)
    MoodType.ANGRY -> Color(0xFFFFD56A)
}

/**
 * stickerUrl 문자열로부터 MoodType을 추론
 */
fun stickerUrlToMood(stickerUrl: String): MoodType = when {
    stickerUrl.contains("smile", ignoreCase = true) -> MoodType.HAPPY
    stickerUrl.contains("laugh", ignoreCase = true) -> MoodType.JOY
    stickerUrl.contains("sad",   ignoreCase = true) -> MoodType.TIRED
    stickerUrl.contains("angry", ignoreCase = true) -> MoodType.ANGRY
    else -> MoodType.HAPPY
}

// ── 데이터 모델 ────────────────────────────────────────────────
data class ActivityCardData(
    val imageUrl: String,
    val title: String,
    val dateTime: String,
    val memo: String = "",
    val dateFormatted: String = "",
    val stickerUrl: String = "",
    val mood: MoodType? = null  // 명시적으로 mood를 넘길 수도 있음 (없으면 stickerUrl로 추론)
)

// ── 메인 화면 ──────────────────────────────────────────────────
@Composable
fun SaveCardScreen(
    cardData: ActivityCardData,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val simpleGraphicsLayer = rememberGraphicsLayer()
    val fullBleedGraphicsLayer = rememberGraphicsLayer()
    var showSavedToast by remember { mutableStateOf(false) }

    LaunchedEffect(showSavedToast) {
        if (showSavedToast) {
            delay(2000L)
            showSavedToast = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            SaveCardTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 헤더 텍스트
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "원하는 카드 프레임을 골라주세요.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "나의 활동기록을 카드로 공유할 수 있어요.",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 카드 캐러셀
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 60.dp),
                    pageSpacing = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) { page ->
                    when (page) {
                        0 -> CardFrameSimple(cardData = cardData, graphicsLayer = simpleGraphicsLayer)
                        1 -> CardFrameFullBleed(cardData = cardData, graphicsLayer = fullBleedGraphicsLayer)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 페이지 인디케이터
                PageIndicator(
                    pageCount = 2,
                    currentPage = pagerState.currentPage
                )
            }

            SaveButton(onClick = {
                coroutineScope.launch {
                    val layer = if (pagerState.currentPage == 0) simpleGraphicsLayer else fullBleedGraphicsLayer
                    val bitmap = layer.toImageBitmap().asAndroidBitmap()
                    saveBitmapToGallery(context, bitmap)
                    showSavedToast = true
                    onSave()
                }
            })
        }

        AnimatedVisibility(
            visible = showSavedToast,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 92.dp)
        ) {
            ErrorToast(
                message = "활동기록 사진 저장완료!",
                iconVisible = true,
            )
        }
    }
}

// ── TopBar ─────────────────────────────────────────────────────
@Composable
private fun SaveCardTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "저장하기",
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFF3A3A3A)
        )
        IconButton(
            onClick = onBack,
            interactionSource = remember { NoRippleInteractionSource() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "뒤로가기",
                tint = Color(0xFF3A3A3A)
            )
        }
    }
}

// ── 저장하기 버튼 ───────────────────────────────────────────────
@Composable
private fun SaveButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Button(
            onClick = onClick,
            interactionSource = remember { NoRippleInteractionSource() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)
        ) {
            Text(
                text = "저장하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// ── 페이지 인디케이터 ───────────────────────────────────────────
@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) OrangeMain
                        else Color(0xFFDDDDDD)
                    )
            )
        }
    }
}

// ── 스티커 drawable 매핑 ────────────────────────────────────────
private fun stickerDrawableRes(stickerUrl: String): Int? = when {
    stickerUrl.contains("smile", ignoreCase = true) -> R.drawable.ic_sticker_smile
    stickerUrl.contains("laugh", ignoreCase = true) -> R.drawable.ic_sticker_laugh
    stickerUrl.contains("sad",   ignoreCase = true) -> R.drawable.ic_sticker_sad
    stickerUrl.contains("angry", ignoreCase = true) -> R.drawable.ic_sticker_angry
    else -> null
}

// ── 프레임 1: 심플 카드형 ───────────────────────────────────────
@Composable
fun CardFrameSimple(
    cardData: ActivityCardData,
    modifier: Modifier = Modifier,
    graphicsLayer: GraphicsLayer? = null
) {
    val stickerRes = remember(cardData.stickerUrl) { stickerDrawableRes(cardData.stickerUrl) }

    // 감정 테마 색상 계산
    // mood 필드가 명시되어 있으면 우선 사용, 없으면 stickerUrl로 추론
    val mood = remember(cardData.mood, cardData.stickerUrl) {
        cardData.mood ?: stickerUrlToMood(cardData.stickerUrl)
    }
    val baseColor = mood.toThemeColor()

    // 피그마 디자인 스펙: 20% → 100% → 20%, 전체 opacity 60%
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            baseColor.copy(alpha = 0.20f),
            baseColor,
            baseColor.copy(alpha = 0.20f)
        )
    )

    // 가장 바깥: 감정 테마 그라디언트 배경
    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(0.65f)
            .then(
                if (graphicsLayer != null) Modifier.drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                } else Modifier
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = backgroundBrush,
                alpha = 0.60f  // 전체 Gradient opacity 60%
            ),
        contentAlignment = Alignment.Center
    ) {
        // 안쪽: 흰 카드 (패딩으로 테마 배경이 테두리처럼 보임)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                // 이미지 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.4f)
                ) {
                    AsyncImage(
                        model = cardData.imageUrl,
                        contentDescription = "활동 이미지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                    stickerRes?.let { drawableRes ->
                        Image(
                            painter = painterResource(drawableRes),
                            contentDescription = "스티커",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(36.dp)
                        )
                    }
                }

                // 텍스트 영역
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = cardData.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cardData.dateTime,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(R.drawable.ic_logo_text),
                        contentDescription = "포데이 로고",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(18.dp)
                    )
                }
            }
        }
    }
}


// ── 감정별 FullBleed 패널 색상 ─────────────────────────────────
// 피그마 스펙: verticalGradient (상단 0% → 하단 100%)
//   HAPPY  : #FFE6D1(0%) → #F4A261
//   JOY    : #8FB3FF(0%) → #8FB3FF
//   TIRED  : #A8D8A2(0%) → #97D190
//   ANGRY  : #FFD56A(0%) → #F9CC5B
private data class FullBleedPanelColors(val top: Color, val bottom: Color)

private fun MoodType.toPanelColors(): FullBleedPanelColors = when (this) {
    MoodType.HAPPY -> FullBleedPanelColors(Color(0xFFFFE6D1), Color(0xFFF4A261))
    MoodType.JOY   -> FullBleedPanelColors(Color(0xFF8FB3FF), Color(0xFF8FB3FF))
    MoodType.TIRED -> FullBleedPanelColors(Color(0xFFA8D8A2), Color(0xFF97D190))
    MoodType.ANGRY -> FullBleedPanelColors(Color(0xFFFFD56A), Color(0xFFF9CC5B))
}

// ── 프레임 2: 풀블리드 카드형 ──────────────────────────────────
@Composable
fun CardFrameFullBleed(
    cardData: ActivityCardData,
    modifier: Modifier = Modifier,
    graphicsLayer: GraphicsLayer? = null
) {
    val stickerRes = remember(cardData.stickerUrl) { stickerDrawableRes(cardData.stickerUrl) }

    // 감정 테마 색상 계산
    val mood = remember(cardData.mood, cardData.stickerUrl) {
        cardData.mood ?: stickerUrlToMood(cardData.stickerUrl)
    }
    val panelColors = mood.toPanelColors()

    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(0.65f)
            .then(
                if (graphicsLayer != null) Modifier.drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                } else Modifier
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        // 풀블리드 이미지
        AsyncImage(
            model = cardData.imageUrl,
            contentDescription = "활동 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 상단 포데이 로고
        Image(
            painter = painterResource(R.drawable.ic_logo_text),
            contentDescription = "포데이 로고",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .height(20.dp)
                .shadow(elevation = 10.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000)),
        )

        // 스티커
        stickerRes?.let { drawableRes ->
            Image(
                painter = painterResource(drawableRes),
                contentDescription = "스티커",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 130.dp)
                    .size(52.dp)
            )
        }

        // ② 그라디언트 오버레이
        // 카드 전체(360dp) 대비 그라디언트 영역(165dp) = 165/360 ≈ 0.46f
        // colorStops 2단계:
        //   0.0f ~ 0.6f : 투명 → 불투명 (자연스러운 페이드인 구간)
        //   0.6f ~ 1.0f : 완전 불투명 고정 (텍스트 가독성 확보 구간)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.46f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to panelColors.top.copy(alpha = 0f),  // 상단: 완전 투명
                            0.6f to panelColors.bottom,                 // 60% 지점: 완전 불투명 시작
                            1.0f to panelColors.bottom                  // 하단 끝: 완전 불투명 유지
                        )
                    )
                )
        )

        // ③ 텍스트 패널 — 그라디언트 오버레이 위에 독립적으로 배치
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = cardData.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (cardData.memo.isNotEmpty()) {
                Text(
                    text = cardData.memo,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = cardData.dateFormatted.ifEmpty { cardData.dateTime },
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

// ── 갤러리 저장 ─────────────────────────────────────────────────
private fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
    val filename = "forday_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/포데이")
    }
    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ) ?: return
    context.contentResolver.openOutputStream(uri)?.use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
    }
}

// ── Preview ────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SaveCardScreenPreview() {
    val sampleData = ActivityCardData(
        imageUrl = "https://picsum.photos/400/400",
        title = "미라클 모닝 아침 독서",
        dateTime = "2026-01-11 12:06",
        memo = "오늘도 아침독서 완료! 뿌듯한 아침 독서...",
        dateFormatted = "2026.01.10. (토)",
        stickerUrl = "smile"
    )
    SaveCardScreen(cardData = sampleData)
}

@Preview(showBackground = true)
@Composable
fun CardFrameSimpleHappyPreview() {
    CardFrameSimple(
        cardData = ActivityCardData(
            imageUrl = "https://picsum.photos/400/400",
            title = "미라클 모닝 아침 독서",
            dateTime = "2026-01-11 12:06",
            stickerUrl = "smile"  // HAPPY → #EE9449 오렌지
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CardFrameSimpleJoyPreview() {
    CardFrameSimple(
        cardData = ActivityCardData(
            imageUrl = "https://picsum.photos/400/400",
            title = "미라클 모닝 아침 독서",
            dateTime = "2026-01-11 12:06",
            stickerUrl = "laugh"  // JOY → #8FB3FF 블루
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CardFrameSimpleTiredPreview() {
    CardFrameSimple(
        cardData = ActivityCardData(
            imageUrl = "https://picsum.photos/400/400",
            title = "미라클 모닝 아침 독서",
            dateTime = "2026-01-11 12:06",
            stickerUrl = "sad"  // TIRED → #A8D8A2 그린
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CardFrameSimpleAngryPreview() {
    CardFrameSimple(
        cardData = ActivityCardData(
            imageUrl = "https://picsum.photos/400/400",
            title = "미라클 모닝 아침 독서",
            dateTime = "2026-01-11 12:06",
            stickerUrl = "angry"  // ANGRY → #FFD56A 옐로우
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CardFrameFullBleedPreview() {
    CardFrameFullBleed(
        cardData = ActivityCardData(
            imageUrl = "https://picsum.photos/400/400",
            title = "미라클 모닝 아침 독서",
            dateTime = "2026-01-11 12:06",
            memo = "오늘도 아침독서 완료! 뿌듯한 아침 독서...",
            dateFormatted = "2026.01.10. (토)"
        )
    )
}