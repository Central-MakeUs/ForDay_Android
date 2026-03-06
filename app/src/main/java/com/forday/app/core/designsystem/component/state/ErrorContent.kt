package com.forday.app.core.designsystem.component.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun ErrorContent(
    errorData: ErrorDataUiState,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = when (errorData.errorType) {
        ErrorDataUiState.ErrorType.TYPE_RETRY -> "다시 시도해주세요. 이용에 불편을 드려 죄송합니다."
        ErrorDataUiState.ErrorType.TYPE_BACK -> "이용에 불편을 드려 죄송합니다."
    }

    val buttonText = when (errorData.errorType) {
        ErrorDataUiState.ErrorType.TYPE_RETRY -> "새로고침"
        ErrorDataUiState.ErrorType.TYPE_BACK -> "뒤로 가기"
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_server_error),
            contentDescription = "에러 이미지",
            modifier = Modifier.fillMaxWidth(),
            tint = Color.Unspecified
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = errorData.message,
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
            color = ForDayTheme.color.Neutral900,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
            fontWeight = FontWeight.W400,
            color = ForDayTheme.color.Neutral600,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAction,
            modifier = Modifier.wrapContentWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = ForDayTheme.color.Neutral900,
            ),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text(
                text = buttonText,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                lineHeight = 16.8.sp,
                fontWeight = FontWeight.W400,
                color = ForDayTheme.color.White,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentRetryPreview() {
    ErrorContent(
        errorData = ErrorDataUiState(
            message = "루틴 목록을 불러올 수 없어요.",
            errorType = ErrorDataUiState.ErrorType.TYPE_RETRY,
        ),
        onAction = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentBackPreview() {
    ErrorContent(
        errorData = ErrorDataUiState(
            message = "접근할 수 없는 페이지예요.",
            errorType = ErrorDataUiState.ErrorType.TYPE_BACK,
        ),
        onAction = {},
    )
}
