package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.RegisterNicknameDataDomain
import com.forday.app.domain.model.RegisterNicknameDomain

data class RegisterNicknameEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: RegisterNicknameDataEntity
) : DataMapper<RegisterNicknameDomain> {
    override fun toDomain(): RegisterNicknameDomain {
        return RegisterNicknameDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

data class RegisterNicknameDataEntity(
    val nickname: String,
    val message: String
) : DataMapper<RegisterNicknameDataDomain> {
    override fun toDomain(): RegisterNicknameDataDomain {
        return RegisterNicknameDataDomain(
            nickname = nickname,
            message = message
        )
    }
}