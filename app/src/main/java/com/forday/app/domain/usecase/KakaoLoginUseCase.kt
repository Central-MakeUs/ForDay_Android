package com.forday.app.domain.usecase

import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.domain.repository.UserRepository
import javax.inject.Inject

class KakaoLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(kakaoAccessToken: String): Result<KakaoLoginDomain> = runCatching {
        authRepository.kakaoLogin(kakaoAccessToken).getOrThrow()
    }

}