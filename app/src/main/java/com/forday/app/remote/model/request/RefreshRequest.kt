package com.forday.app.remote.model.request

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RefreshRequest(
    val refreshToken: String
)
