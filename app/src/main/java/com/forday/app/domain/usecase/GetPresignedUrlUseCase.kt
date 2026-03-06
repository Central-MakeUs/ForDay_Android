package com.forday.app.domain.usecase

import com.forday.app.domain.repository.FileRepository
import javax.inject.Inject

class GetPresignedUrlUseCase @Inject constructor(
    private val routineRepository: FileRepository
) {
    suspend operator fun invoke(images: List<Map<String, Any>>) =
        routineRepository.getPresignedUrl(images)

}