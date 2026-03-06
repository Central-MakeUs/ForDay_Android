package com.forday.app.domain.model

data class VisibilityDomain(
    val status: Int,
    val isSuccess: Boolean,
    val message: String,
    val previousVisibility: VisibilityType,
    val newVisibility: VisibilityType,
    val errorClassName: String
)

enum class VisibilityType {
    PUBLIC,
    PRIVATE,
    FRIEND,
    NONE
}