package com.forday.app.presentation.onboarding.hobbyselect

data class HobbyItem(
    val title: String,
    val subtitle: String,
    var isSelected: Boolean = false
)
