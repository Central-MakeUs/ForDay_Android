package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.LogoutDomain

data class LogoutEntity(
    val message: String,
    val isSuccess: Boolean
) : DataMapper<LogoutDomain> {

    override fun toDomain(): LogoutDomain {
        return LogoutDomain(
            message = message,
            isSuccess = isSuccess
        )
    }
}