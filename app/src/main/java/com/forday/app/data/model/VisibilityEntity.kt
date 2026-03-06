package com.forday.app.data.model

import com.forday.app.domain.model.VisibilityDomain
import com.forday.app.domain.model.VisibilityType

data class VisibilityEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: VisibilityDataEntity
) {
    fun toDomain(): VisibilityDomain {
        return VisibilityDomain(
            status = status,
            isSuccess = isSuccess,
            message = data.message,
            previousVisibility = mapToVisibilityType(data.previousVisibility),
            newVisibility = mapToVisibilityType(data.newVisibility),
            errorClassName = data.errorClassName
        )
    }

    private fun mapToVisibilityType(value: String): VisibilityType {
        return try {
            VisibilityType.valueOf(value)
        } catch (e: Exception) {
            VisibilityType.NONE
        }
    }
}

data class VisibilityDataEntity(
    val message: String,
    val previousVisibility: String,
    val newVisibility: String,
    val errorClassName: String
)