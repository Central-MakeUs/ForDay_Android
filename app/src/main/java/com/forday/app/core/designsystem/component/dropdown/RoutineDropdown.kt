package com.forday.app.core.designsystem.component.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme

data class DropdownItem(
    val text: String,
    val hasAiIcon: Boolean = false,
    val isSelected: Boolean = false
)

@Composable
fun RoutineDropdown(
    items: List<DropdownItem>,
    onItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 10.dp)
        ) {
            items.forEachIndexed { index, item ->
                DropdownItemRow(
                    item = item,
                    onClick = { onItem(index) }
                )
            }
        }
    }
}

@Composable
private fun DropdownItemRow(
    item: DropdownItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .then(
                if (item.isSelected) {
                    Modifier.background(
                        color = Color(0xFFF2F2F2),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
            .clickable(onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null)
            .padding(horizontal = 29.5.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.hasAiIcon) {
                Icon(
                    painter = painterResource(R.drawable.ic_ai_list),
                    contentDescription = "AI",
                    modifier = Modifier.size(14.dp),
                    tint = Color.Unspecified
                )
            }

            Text(
                text = item.text,
                fontSize = 14.sp,
                fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (item.isSelected) Color(0xFF3A3A3A) else Color(0xFF9E9E9E),
                lineHeight = if (item.isSelected) 16.8.sp else 19.6.sp
            )
        }
    }
}

@Preview
@Composable
fun ActivityDropdownPreview() {
    ForDayTheme {
        RoutineDropdown(
            items = listOf(
                DropdownItem(text = "활동1", isSelected = true),
                DropdownItem(text = "활동2", hasAiIcon = true),
                DropdownItem(text = "활동3")
            ),
            onItem = {}
        )
    }
}