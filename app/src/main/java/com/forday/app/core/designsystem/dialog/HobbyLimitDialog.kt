package com.forday.app.core.designsystem.dialog


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick

@Composable
fun HobbyLimitDialog(
    onDismiss: () -> Unit,
    onNavigateToStorage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                // Title Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "최대 2개까지만 진행할 수 있어요",
                        modifier = Modifier.weight(1f),
                        style = TextStyle(
                            fontFamily = FontFamily.Default, // Pretendard로 교체 필요
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 21.6.sp, // 18 * 1.2
                            color = Color(0xFF1E1E1E)
                        )
                    )
                }

                // Content Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "현재 진행중인 취미가 2개여서 취미를 꺼낼 수 없어요. " +
                                    "취미를 다시 시작하고 싶다면 진행중인 취미를 보관하고 다시 진행해주세요.",
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontFamily = FontFamily.Default, // Pretendard로 교체 필요
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                lineHeight = 19.6.sp, // 14 * 1.4
                                color = Color(0xFF3A3A3A)
                            )
                        )
                    }
                }

                // Buttons Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 닫기 Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        interactionSource = remember { NoRippleInteractionSource() },
                        shape = RoundedCornerShape(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE5E5E5)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "닫기",
                            style = TextStyle(
                                fontFamily = FontFamily.Default, // Pretendard로 교체 필요
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                lineHeight = 16.8.sp, // 14 * 1.2
                                color = Color(0xFF1E1E1E)
                            )
                        )
                    }

                    // 보관하러 가기 Button
                    Button(
                        onClick = rememberThrottledClick(onClick = onNavigateToStorage),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        interactionSource = remember { NoRippleInteractionSource() },
                        shape = RoundedCornerShape(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9447)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "보관하러 가기",
                            style = TextStyle(
                                fontFamily = FontFamily.Default, // Pretendard로 교체 필요
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                lineHeight = 16.8.sp, // 14 * 1.2
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

// 사용 예시
@Preview(showBackground = true)
@Composable
fun HobbyLimitDialogPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)) // Dimmer
        ) {
            HobbyLimitDialog(
                onDismiss = { },
                onNavigateToStorage = { }
            )
        }
    }
}

// 실제 사용
@Composable
fun HobbyScreen() {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 화면 내용

        if (showDialog) {
            HobbyLimitDialog(
                onDismiss = { showDialog = false },
                onNavigateToStorage = {
                    showDialog = false
                    // 보관함으로 이동 로직
                }
            )
        }
    }
}