package com.forday.app.domain.model

data class ReactionDomain(  // 활동 기록에 반응 남기기
    val status: Int,
    val isSuccess: Boolean,
    val message: String,
    val reactionType: String,
    val recordId: Int,
    val errorClassName: String
)