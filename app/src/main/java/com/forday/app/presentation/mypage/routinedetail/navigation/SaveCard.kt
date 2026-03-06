package com.forday.app.presentation.mypage.routinedetail.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SaveCard(
    val imageUrl: String = "",
    val title: String = "",
    val dateTime: String = "",
    val memo: String = "",
    val dateFormatted: String = "",
    val stickerUrl: String = ""
) : NavKey