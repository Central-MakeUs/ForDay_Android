package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class GetUsersProgressHobbyTabsUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(userId: String? = null) = hobbyRepository.getUsersProgressHobbyTabs(userId)
}