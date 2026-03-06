package com.forday.app.domain.usecase

import com.forday.app.domain.repository.UserRepository
import javax.inject.Inject

class SaveNicknameUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(nickname: String) = repository.saveNickname(nickname)
}