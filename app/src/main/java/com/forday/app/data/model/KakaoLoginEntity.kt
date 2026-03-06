package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.KakaoLoginDataDomain
import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.domain.model.KakaoOnboardingDataDomain


// 최상위 Entity (Response 전체 매핑)
data class KakaoLoginEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: KakaoLoginDataEntity
) : DataMapper<KakaoLoginDomain> {
    override fun toDomain(): KakaoLoginDomain {
        return KakaoLoginDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

// 핵심 로그인 데이터 Entity
data class KakaoLoginDataEntity(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
    val socialType: String,
    val isOnboardingCompleted: Boolean,
    val isNicknameSet: Boolean,
    val nickname: String?,
    val guestUserId: String?, // 추가: 응답 JSON에 있던 필드
    val onboardingData: KakaoOnboardingDataEntity? // String? 대신 객체 타입 사용
) : DataMapper<KakaoLoginDataDomain> {
    override fun toDomain(): KakaoLoginDataDomain {
        return KakaoLoginDataDomain(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isNewUser = isNewUser,
            socialType = socialType,
            isOnboardingCompleted = isOnboardingCompleted,
            isNicknameSet = isNicknameSet,
            nickname = nickname,
            guestUserId = guestUserId,
            // 하위 객체도 도메인 모델로 변환하여 전달
            onboardingData = onboardingData?.toDomain()
        )
    }
}

// 온보딩 상세 데이터 Entity
data class KakaoOnboardingDataEntity(
    val id: Int,
    val hobbyCardId: Int,
    val hobbyName: String,
    val hobbyPurpose: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val durationSet: Boolean
) : DataMapper<KakaoOnboardingDataDomain> { // 하위 객체도 매퍼 적용 추천
    override fun toDomain(): KakaoOnboardingDataDomain {
        return KakaoOnboardingDataDomain(
            id = id,
            hobbyCardId = hobbyCardId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            isDurationSet = durationSet
        )
    }
}