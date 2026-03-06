package com.forday.app.domain.usecase

import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.domain.model.SwitchAccountDomain
import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class SwitchAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(socialType: String, kakaoAccessToken: String): Result<SwitchAccountDomain> = runCatching {
        authRepository.switchAccount(socialType, kakaoAccessToken).getOrThrow()
    }
}