package com.forday.app.core.designsystem.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick

@Composable
fun RoutineOnlyOneHaveDialog(
    onDismiss: () -> Unit,
    onViewRecords: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 타이틀
                Text(
                    text = "활동 기록은 하루에 1개만 가능해요",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    textAlign = TextAlign.Start,
                    lineHeight = 21.6.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 본문
                Text(
                    text = "이미 오늘의 활동을 기록하셨어요. 다른 활동을 기록하고 싶다면, 이전 활동 기록을 삭제해야 돼요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF3A3A3A),
                    textAlign = TextAlign.Start,
                    lineHeight = 19.6.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 버튼들
                var closeTextSize by remember { mutableStateOf(16.sp) }
                var viewTextSize by remember { mutableStateOf(16.sp) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 닫기 버튼
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        interactionSource = remember { NoRippleInteractionSource() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE5E5E5)
                        ),
                        shape = RoundedCornerShape(40.dp),
                        contentPadding = PaddingValues(vertical = 11.5.dp)
                    ) {
                        Text(
                            text = "닫기",
                            fontSize = closeTextSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E1E),
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false,
                            onTextLayout = { result ->
                                if (result.hasVisualOverflow) closeTextSize *= 0.9f
                            }
                        )
                    }

                    // 기록 보러가기 버튼
                    Button(
                        onClick = rememberThrottledClick { onViewRecords() },
                        modifier = Modifier.weight(1f),
                        interactionSource = remember { NoRippleInteractionSource() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9447)
                        ),
                        shape = RoundedCornerShape(40.dp),
                        contentPadding = PaddingValues(vertical = 11.5.dp)
                    ) {
                        Text(
                            text = "기록 보러가기",
                            fontSize = viewTextSize,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false,
                            onTextLayout = { result ->
                                if (result.hasVisualOverflow) viewTextSize *= 0.9f
                            }
                        )
                    }
                }
            }
        }
    }
}
