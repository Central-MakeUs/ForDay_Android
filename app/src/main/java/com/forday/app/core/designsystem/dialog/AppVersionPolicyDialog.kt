package com.forday.app.core.designsystem.dialog

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.domain.model.AppUpdateType

@Composable
fun AppVersionPolicyDialog(
    updateType: AppUpdateType,
    message: String,
    onUpdate: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        BackHandler { activity?.finish() }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Text(
                    text = when (updateType) {
                        AppUpdateType.BLOCK -> "서비스 점검 중"
                        AppUpdateType.FORCE -> "업데이트가 필요해요"
                        AppUpdateType.RECOMMEND -> "새 버전이 출시됐어요"
                        AppUpdateType.NONE -> ""
                    },
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E1E)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF7A7A7A)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (updateType) {
                    AppUpdateType.BLOCK -> Unit

                    AppUpdateType.FORCE -> {
                        Button(
                            onClick = rememberThrottledClick(onClick = onUpdate),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            interactionSource = remember { NoRippleInteractionSource() },
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9447)
                            )
                        ) {
                            Text(
                                text = "업데이트하기",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    AppUpdateType.RECOMMEND -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = rememberThrottledClick(onClick = onDismiss),
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(),
                                interactionSource = remember { NoRippleInteractionSource() },
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEEEEEE)
                                )
                            ) {
                                Text(
                                    text = "나중에",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E1E1E)
                                    )
                                )
                            }

                            Button(
                                onClick = rememberThrottledClick(onClick = onUpdate),
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(),
                                interactionSource = remember { NoRippleInteractionSource() },
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9447)
                                )
                            ) {
                                Text(
                                    text = "업데이트하기",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    AppUpdateType.NONE -> Unit
                }
            }
        }
    }
}
