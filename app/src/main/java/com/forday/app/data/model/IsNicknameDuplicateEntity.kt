package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.IsNicknameDuplicateDataDomain
import com.forday.app.domain.model.IsNicknameDuplicateDomain

data class IsNicknameDuplicateEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: IsNicknameDuplicateDataEntity
) : DataMapper<IsNicknameDuplicateDomain> {
    override fun toDomain(): IsNicknameDuplicateDomain {
        return IsNicknameDuplicateDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

data class IsNicknameDuplicateDataEntity(
    val nickname: String,
    val message: String,
    val available: Boolean
) : DataMapper<IsNicknameDuplicateDataDomain> {
    override fun toDomain(): IsNicknameDuplicateDataDomain {
        return IsNicknameDuplicateDataDomain(
            nickname = nickname,
            message = message,
            available = available
        )
    }
}
