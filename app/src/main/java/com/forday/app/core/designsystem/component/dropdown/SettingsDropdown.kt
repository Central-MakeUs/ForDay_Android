package com.forday.app.core.designsystem.component.dropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.presentation.home.SettingsMenuItem

@Composable
fun SettingsDropdown(
    onItemClick: (SettingsMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    items: List<SettingsMenuItem> = SettingsMenuItem.entries,
) {
    Surface(
        modifier = modifier.wrapContentWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemClick(item) }
                ) {
                    Text(
                        text = item.label,
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3A)
                    )
                }
            }
        }
    }
}