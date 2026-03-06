package com.forday.app.remote.model.request

data class HobbyMainImageRequest(
    val hobbyId: Long?,
    val coverImageUrl: String?,
    val recordId: Long?
)