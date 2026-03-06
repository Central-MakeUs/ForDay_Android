package com.forday.app.domain.usecase

import com.forday.app.domain.repository.FileRepository
import javax.inject.Inject

class DeleteS3ImageUseCase @Inject constructor(
    private val routineRepository: FileRepository
) {
    suspend operator fun invoke(imageUrl: String) =
        routineRepository.deleteS3Image(imageUrl)
}