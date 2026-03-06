package com.forday.app.domain.usecase

import com.forday.app.domain.repository.UserRepository
import javax.inject.Inject

class SetProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(imageUrl: String?) =
        userRepository.setProfileImage(imageUrl)
}