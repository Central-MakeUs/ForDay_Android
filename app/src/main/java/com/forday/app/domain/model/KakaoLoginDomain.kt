package com.forday.app.domain.model

// 1. 도메인 최상위 모델
// 보통 Domain Layer에서는 status나 success 같은 서버 전송용 필드는 제거하고,
// 데이터만 깔끔하게 넘기는 경우가 많습니다. (필요하다면 포함 가능)
data class KakaoLoginDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: KakaoLoginDataDomain
)

// 2. 핵심 로그인 데이터 모델
data class KakaoLoginDataDomain(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
    val socialType: String,
    val isOnboardingCompleted: Boolean,
    val isNicknameSet: Boolean,
    val nickname: String?,
    val guestUserId: String?,
    // UI에서 온보딩 정보를 상세히 보여줘야 하므로 객체 타입을 권장합니다.
    // 만약 단순히 문자열만 필요하다면 String?도 괜찮습니다.
    val onboardingData: KakaoOnboardingDataDomain?
)

// 3. 온보딩 상세 정보 (Domain 전용)
data class KakaoOnboardingDataDomain(
    val id: Int,
    val hobbyCardId: Int,
    val hobbyName: String,
    val hobbyPurpose: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val isDurationSet: Boolean
)