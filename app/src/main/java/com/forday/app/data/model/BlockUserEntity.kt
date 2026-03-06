package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.BlockUserDataDomain
import com.forday.app.domain.model.BlockUserDomain

data class BlockUserEntity(
    val status: Int,
    val success: Boolean,
    val data: BlockUserDataEntity
) : DataMapper<BlockUserDomain> {
    override fun toDomain(): BlockUserDomain = BlockUserDomain(
        status = status,
        success = success,
        data = data.toDomain()
    )
}

data class BlockUserDataEntity(
    val message: String = "",
    val nickname: String = ""
) : DataMapper<BlockUserDataDomain> {
    override fun toDomain(): BlockUserDataDomain = BlockUserDataDomain(
        message = message,
        nickname = nickname
    )
}
