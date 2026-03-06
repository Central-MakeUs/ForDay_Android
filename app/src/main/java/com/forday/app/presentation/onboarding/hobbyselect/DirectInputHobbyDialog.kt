package com.forday.app.presentation.onboarding.hobbyselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.forday.app.core.designsystem.theme.FordayColor

@Composable
fun DirectInputHobbyDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onNext: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .width(312.dp)
                    .height(210.dp)
                    .background(FordayColor.White, shape = RoundedCornerShape(20.dp))
                    .padding(vertical = 24.dp, horizontal = 20.dp)
            ) {

                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "취미 입력",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 입력 필드
                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        if (it.length <= 10) onValueChange(it)
                    },
                    placeholder = {
                        Text("취미를 입력해 주세요.")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (value.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "지우기",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = rememberThrottledClick { onValueChange("") }
                                    )
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${value.length}/10",
                    fontSize = 12.sp,
                    color = FordayColor.Neutral50,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 다음 버튼
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(
                            if (value.isNotBlank())
                                FordayColor.Red01
                            else
                                FordayColor.Neutral50
                        )
                        .clickable(
                            onClick = rememberThrottledClick { onNext() },
                            enabled = value.isNotBlank()
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "다음",
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DirectInputHobbyDialogPreview() {
    DirectInputHobbyDialog(
        value = "취미 입력",
        onValueChange = {},
        onDismiss = {},
        onNext = {}
    )
}