package com.forday.app.presentation.sosik.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class Register(val userId: String? = null) : NavKey  // 신고하기 페이지