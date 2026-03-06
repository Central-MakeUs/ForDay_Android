package com.forday.app.core.designsystem.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun HobbyInputDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    onNext: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hobbyText by remember { mutableStateOf("") }
    val maxLength = 10

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 제목 및 닫기 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 21.6.sp,
                            color = Color(0xFF1E1E1E)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            tint = ForDayTheme.color.Neutral900,
                            contentDescription = "닫기",)
                    }

                }

                // 입력 필드
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = hobbyText,
                            onValueChange = {
                                if (it.length <= maxLength) {
                                    hobbyText = it
                                }
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                lineHeight = 19.6.sp, // 140%
                                color = Color(0xFF1E1E1E)
                            ),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (hobbyText.isEmpty()) {
                                        Text(
                                            text = description,
                                            style = TextStyle(
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                lineHeight = 19.6.sp,
                                                color = Color(0xFF7A7A7A)
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        // 아이콘 (필요시)
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_eye_on),
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp),
//                            tint = Color(0xFF7A7A7A)
//                        )
                    }

                    // 밑줄
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFB5B5B5))
                    )

                    // 글자 수 카운터
                    Text(
                        text = "${hobbyText.length}/$maxLength",
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFFB5B5B5)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = TextAlign.End
                    )
                }

                // 다음 버튼
                Button(
                    onClick = { onNext(hobbyText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEE9449)
                    ),
                    enabled = hobbyText.isNotEmpty()
                ) {
                    Text(
                        text = "다음",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HobbyInputDialogPreview() {
    ForDayTheme {
        HobbyInputDialog(
            onDismiss = {},
            onNext = {},
            modifier = Modifier,
            title = TODO(),
            description = TODO()
        )
    }
}