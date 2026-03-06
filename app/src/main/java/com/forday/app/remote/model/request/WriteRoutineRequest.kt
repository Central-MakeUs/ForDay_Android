package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class WriteRoutineRequest(
    val sticker: String,
    val memo: String,
    val imageUrl: String,
    val visibility: String
)