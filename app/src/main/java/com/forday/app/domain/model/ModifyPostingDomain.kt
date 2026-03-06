package com.forday.app.domain.model

data class ModifyPostingDomain(
    val message: String,
    val activityId: Int,
    val content: String,
    val sticker: String,
    val memo: String,
    val imageUrl: String,
    val visibility: String
)