package com.forday.app.domain.model

data class ReactionCancelDomain(
    val status: Int,
    val isSuccess: Boolean,
    val message: String,
    val reactionType: String,
    val recordId: Int,
    val errorClassName: String
)