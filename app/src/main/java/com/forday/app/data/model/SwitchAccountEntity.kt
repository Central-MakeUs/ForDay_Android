package com.forday.app.data.model

import com.forday.app.domain.model.SwitchAccountDomain

data class SwitchAccountEntity(
    val socialType: String,
    val accessToken: String,
    val refreshToken: String,
    val errorClassName: String?,
    val message: String?
)

fun SwitchAccountEntity.toDomain(): SwitchAccountDomain {
    return SwitchAccountDomain(
        socialType = this.socialType,
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}