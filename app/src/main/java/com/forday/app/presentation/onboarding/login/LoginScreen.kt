package com.forday.app.presentation.onboarding.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.logger.analytics.AnalyticsEvents
import timber.log.Timber

@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel,
) {
    viewModel.logEvent(AnalyticsEvents.LOGIN_SCREEN)
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            onKakaoLogin = {
                viewModel.logEvent(AnalyticsEvents.KAKAO_LOGIN_CLICK)
                viewModel.onAction(LoginAction.LoginWithKakao(context))
            },
            onGuestMode = {
                viewModel.logEvent(AnalyticsEvents.GUEST_MODE_CLICK)
                viewModel.onAction(LoginAction.LoginWithGuest)
            }
        )
    }
}

@Composable
fun LoginScreen(
    onKakaoLogin: () -> Unit,
    onGuestMode: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(194.dp))

            Image(
                painter = painterResource(id = R.drawable.main_character),
                contentDescription = "메인 캐릭터",
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "취미 시작이 어려울 때.\n포데이",
                color = ForDayTheme.color.Neutral900,
                textAlign = TextAlign.Center,
                style = ForDayTheme.typography.title24
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AI 추천으로 쉽게 시작하는 취미생활",
                color = ForDayTheme.color.Secondary003,
                textAlign = TextAlign.Center,
                style = ForDayTheme.typography.body16
            )

            Spacer(modifier = Modifier.weight(1f))

            // SNS 힌트 말풍선
            HintBubble(text = "SNS로 가볍게 시작하기!")

            Spacer(modifier = Modifier.height(14.dp))

            // 카카오 로그인 버튼
            SocialLoginButton(
                text = "카카오톡으로 시작하기",
                backgroundColor = ForDayTheme.color.KakaoYellow,
                textColor = ForDayTheme.color.Neutral900,
                iconRes = R.drawable.ic_kakao,
                onClick = rememberThrottledClick(onClick = onKakaoLogin),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.width(126.dp),
                    thickness = 1.dp,
                    color = ForDayTheme.color.MediumGray
                )
                Text(
                    text = "또는",
                    color = ForDayTheme.color.Neutral600,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    style = ForDayTheme.typography.label12
                )
                HorizontalDivider(
                    modifier = Modifier.width(126.dp),
                    thickness = 1.dp,
                    color = ForDayTheme.color.MediumGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = rememberThrottledClick { onGuestMode() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),  // TextButton의 기본 패딩
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "게스트로 둘러보기",
                    color = ForDayTheme.color.Neutral600,
                    style = ForDayTheme.typography.title16,
                    modifier = Modifier
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val verticalOffset = size.height + 2.dp.toPx()
                            drawLine(
                                color = Color(0xFF7A7A7A),
                                start = Offset(0f, verticalOffset),
                                end = Offset(size.width, verticalOffset),
                                strokeWidth = strokeWidth
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(34.dp))
        }
    }
}

@Composable
fun HintBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // 말풍선 본체 (부드럽고 넓게 퍼진 그림자)
        Box(
            modifier = Modifier
                .wrapContentSize()
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint()

                        paint.color = android.graphics.Color.TRANSPARENT
                        paint.setShadowLayer(
                            40f, // blur radius - 이미지처럼 매우 부드럽게
                            0f,  // x offset
                            4f,  // y offset - 약간 아래로
                            android.graphics.Color.parseColor("#14000000") // 약 8% 검정
                        )

                        // 둥근 사각형 그리기
                        val rect = android.graphics.RectF(
                            0f,
                            0f,
                            size.width,
                            size.height
                        )
                        canvas.nativeCanvas.drawRoundRect(
                            rect,
                            21.dp.toPx(),
                            21.dp.toPx(),
                            paint
                        )
                    }
                }
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(21.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color(0xFF1E1E1E),
                textAlign = TextAlign.Center,
                style = ForDayTheme.typography.title12
            )
        }

        // 말풍선 꼬리 (삼각형)
        Canvas(
            modifier = Modifier
                .width(10.dp)
                .height(4.dp)
        ) {
            val trianglePath = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width / 2f - 5.dp.toPx(), 0f)
                lineTo(size.width / 2f, size.height)
                lineTo(size.width / 2f + 5.dp.toPx(), 0f)
                close()
            }

            drawPath(
                path = trianglePath,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    iconRes: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = rememberThrottledClick { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = textColor,
                style = ForDayTheme.typography.title16
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun LoginScreenPreview() {
    ForDayTheme {
        LoginScreen(
            onKakaoLogin = {},
            onGuestMode = {}
        )
    }
}