package com.forday.app.data.model

import com.forday.app.domain.model.CancelAccountDomain

data class CancelAccountEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: CancelAccountDataEntity
) {
    fun toDomain(): CancelAccountDomain = data.toDomain()
}

data class CancelAccountDataEntity(
    val message: String,
    val deletedAt: String
) {
    fun toDomain(): CancelAccountDomain =
        CancelAccountDomain(
            message = message,
            deletedAt = deletedAt
        )
}