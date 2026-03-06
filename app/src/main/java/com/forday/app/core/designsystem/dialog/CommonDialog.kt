package com.forday.app.core.designsystem.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun CommonDialog(
    title: String = "Dialog title",
    bodyText: String = "body text\nbody text",
    showCloseIcon: Boolean = true,
    primaryButtonText: String = "Button",
    secondaryButtonText: String = "Button",
    onPrimaryClick: () -> Unit = {},
    onSecondaryClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    showDialog: Boolean = true
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            DialogContent(
                title = title,
                bodyText = bodyText,
                showCloseIcon = showCloseIcon,
                primaryButtonText = primaryButtonText,
                secondaryButtonText = secondaryButtonText,
                onPrimaryClick = onPrimaryClick,
                onSecondaryClick = onSecondaryClick,
                onCloseClick = onDismiss
            )
        }
    }
}

@Composable
fun DialogContent(
    title: String,
    bodyText: String,
    showCloseIcon: Boolean,
    primaryButtonText: String,
    isSecondaryButtonVisible: Boolean = false,
    secondaryButtonText: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(312.dp)
            .background(
                color = ForDayTheme.color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                style = ForDayTheme.typography.title18,
                color = ForDayTheme.color.Neutral900,
                modifier = Modifier.weight(1f)
            )

            if (showCloseIcon) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onCloseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        style = TextStyle(fontSize = 18.sp),
                        color = ForDayTheme.color.Neutral900
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = bodyText,
                style = ForDayTheme.typography.label14,
                color = ForDayTheme.color.Gray800,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (isSecondaryButtonVisible) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(ForDayTheme.color.Gray03)
                        .clickable { onSecondaryClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = secondaryButtonText,
                        style = ForDayTheme.typography.body14,
                        color = ForDayTheme.color.Neutral900,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(ForDayTheme.color.Orange01)
                    .clickable { onPrimaryClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = primaryButtonText,
                    style = ForDayTheme.typography.body14,
                    color = ForDayTheme.color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun DialogCard(
    modifier: Modifier = Modifier,
    title: String = "Dialog title",
    bodyText: String = "body text\nbody text",
    showCloseIcon: Boolean = true,
    primaryButtonText: String = "Button",
    secondaryButtonText: String = "Button",
    onPrimaryClick: () -> Unit = {},
    onSecondaryClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    DialogContent(
        title = title,
        bodyText = bodyText,
        showCloseIcon = showCloseIcon,
        primaryButtonText = primaryButtonText,
        secondaryButtonText = secondaryButtonText,
        onPrimaryClick = onPrimaryClick,
        onSecondaryClick = onSecondaryClick,
        onCloseClick = onCloseClick
    )
}

@Preview
@Composable
fun DialogPreview() {
    var showDialog by remember { mutableStateOf(true) }
    ForDayTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)),
            contentAlignment = Alignment.Center
        ) {
            if (showDialog) {
                DialogCard(
                    title = "Dialog title",
                    bodyText = "body text\nbody text",
                    showCloseIcon = true,
                    primaryButtonText = "Button",
                    secondaryButtonText = "Button",
                    onPrimaryClick = { showDialog = false },
                    onSecondaryClick = { showDialog = false },
                    onCloseClick = { showDialog = false }
                )
            }
        }
    }
}