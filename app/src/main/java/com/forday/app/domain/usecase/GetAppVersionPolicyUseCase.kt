package com.forday.app.domain.usecase

import com.forday.app.domain.model.AppVersionPolicyDomain
import com.forday.app.domain.repository.AppVersionPolicyRepository
import javax.inject.Inject

class GetAppVersionPolicyUseCase @Inject constructor(
    private val repository: AppVersionPolicyRepository
) {
    suspend operator fun invoke(platform: String, appVersion: String, build: Int): AppVersionPolicyDomain =
        repository.getAppVersionPolicy(platform, appVersion, build)
}
