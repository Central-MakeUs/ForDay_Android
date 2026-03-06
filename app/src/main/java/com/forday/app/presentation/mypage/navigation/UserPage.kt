package com.forday.app.presentation.mypage.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class UserPage(val userId: String?, val recordAuthor: Boolean = false) : NavKey
