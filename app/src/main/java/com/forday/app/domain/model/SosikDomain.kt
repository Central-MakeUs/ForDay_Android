package com.forday.app.domain.model

data class SosikDomain(
    val status: Int,
    val success: Boolean,
    val data: SosikDataDomain?
)

data class SosikDataDomain(
    val tabInfo: List<SosikTabInfoDomain>,
    val lastRecordId: Long?,
    val recordList: List<SosikRecordDomain>,
    val hasNext: Boolean
)

data class SosikTabInfoDomain(
    val hobbyId: Long,
    val hobbyName: String,
    val currentHobby: Boolean
)

data class SosikRecordDomain(
    val recordId: Long,
    val thumbnailUrl: String,
    val sticker: String,
    val title: String,
    val memo: String,
    val userInfo: SosikUserInfoDomain,
    val pressedAweSome: Boolean,
    val hobbyName: String,
    val recordAuthor: Boolean
)

data class SosikUserInfoDomain(
    val userId: String,
    val nickname: String?,
    val profileImageUrl: String?
)
