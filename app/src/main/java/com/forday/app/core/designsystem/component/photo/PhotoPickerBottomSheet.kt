package com.forday.app.core.designsystem.component.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick

@Composable
fun PhotoPickerBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "사진 추가",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3A3A3A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 사진 촬영
        PhotoPickerOption(
            icon = R.drawable.ic_camera,
            text = "사진 촬영",
            onClick = rememberThrottledClick {
                onCameraClick()
                onDismiss()
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 갤러리
        PhotoPickerOption(
            icon = R.drawable.ic_camera, // 아이콘 추가 필요 (없으면 ic_camera 재사용)
            text = "갤러리에서 선택",
            onClick = rememberThrottledClick {
                onGalleryClick()
                onDismiss()
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 취소
        Text(
            text = "취소",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7A7A7A),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDismiss() }
                .padding(vertical = 12.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun PhotoPickerOption(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = rememberThrottledClick(onClick = onClick),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF3A3A3A)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3A3A3A)
            )
        }
    }
}