package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.SosikDataDomain
import com.forday.app.domain.model.SosikDomain
import com.forday.app.domain.model.SosikRecordDomain
import com.forday.app.domain.model.SosikTabInfoDomain
import com.forday.app.domain.model.SosikUserInfoDomain

data class SosikEntity(
    val status: Int,
    val success: Boolean,
    val data: SosikDataEntity?
) : DataMapper<SosikDomain> {
    override fun toDomain(): SosikDomain = SosikDomain(
        status = status,
        success = success,
        data = data?.toDomain()
    )
}

data class SosikDataEntity(
    val tabInfo: List<SosikTabInfoEntity>,
    val lastRecordId: Long?,
    val recordList: List<SosikRecordEntity>,
    val hasNext: Boolean
) : DataMapper<SosikDataDomain> {
    override fun toDomain(): SosikDataDomain = SosikDataDomain(
        tabInfo = tabInfo.map { it.toDomain() },
        lastRecordId = lastRecordId,
        recordList = recordList.map { it.toDomain() },
        hasNext = hasNext
    )
}

data class SosikTabInfoEntity(
    val hobbyId: Long,
    val hobbyName: String,
    val currentHobby: Boolean
) : DataMapper<SosikTabInfoDomain> {
    override fun toDomain(): SosikTabInfoDomain = SosikTabInfoDomain(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        currentHobby = currentHobby
    )
}

data class SosikRecordEntity(
    val recordId: Long,
    val thumbnailUrl: String,
    val sticker: String,
    val title: String,
    val memo: String,
    val userInfo: SosikUserInfoEntity,
    val pressedAweSome: Boolean,
    val hobbyName: String,
    val recordAuthor: Boolean
) : DataMapper<SosikRecordDomain> {
    override fun toDomain(): SosikRecordDomain = SosikRecordDomain(
        recordId = recordId,
        thumbnailUrl = thumbnailUrl,
        sticker = sticker,
        title = title,
        memo = memo,
        userInfo = userInfo.toDomain(),
        pressedAweSome = pressedAweSome,
        hobbyName = hobbyName,
        recordAuthor = recordAuthor
    )
}

data class SosikUserInfoEntity(
    val userId: String,
    val nickname: String?,
    val profileImageUrl: String?
) : DataMapper<SosikUserInfoDomain> {
    override fun toDomain(): SosikUserInfoDomain = SosikUserInfoDomain(
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl
    )
}
