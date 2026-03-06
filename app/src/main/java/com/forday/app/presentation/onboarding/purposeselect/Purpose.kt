package com.forday.app.presentation.onboarding.purposeselect

enum class Purpose(val title: String, val subtitle: String, val badge: String? = null) {
    Stress("스트레스 해소", "마음을 편안하게"),
    Growth("성장과 만족감", "성취감을 얻기 위해", "추천"),
    Energy("에너지 회복", "기본과 활력을 되찾기 위해"),
    Balance("삶의 균형 유지", "일 중심이 아닌\n삶과의 균형을 위해"),
    NONE("", "")
}