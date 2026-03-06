package com.forday.app.core.designsystem.component.progressbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun ForDayProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ForDayTheme.color.Neutral50)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = ForDayTheme.color.Gray03,
                    shape = RoundedCornerShape(20.dp)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .background(
                    brush = ForDayTheme.gradients.gradient002,
                    shape = RoundedCornerShape(20.dp)
                )
        )
    }
}


/**
 * 단계별 프로그레스 바
 *
 * @param currentStep 현재 단계 (1부터 시작)
 * @param totalSteps 전체 단계 수
 * @param modifier Modifier
 */
@Composable
fun StepProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentStep.toFloat() / totalSteps.toFloat()
    ForDayProgressBar(
        progress = progress,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ForDayProgressBarPreview() {
    ForDayTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            ForDayProgressBar(progress = 0.2f)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StepProgressBarPreview() {
    ForDayTheme() {
        Box(modifier = Modifier.fillMaxWidth()) {
            StepProgressBar(currentStep = 2, totalSteps = 5)
        }
    }
}