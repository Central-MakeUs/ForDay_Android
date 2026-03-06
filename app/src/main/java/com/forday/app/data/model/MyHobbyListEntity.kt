package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.*
import kotlin.collections.map

data class MyHobbyListEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: MyHobbyListDataEntity
) : DataMapper<MyHobbyListDomain> {
    override fun toDomain(): MyHobbyListDomain = MyHobbyListDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class MyHobbyListDataEntity(
    val currentHobbyStatus: String,
    val inProgressHobbyCount: Int,
    val archivedHobbyCount: Int,
    val hobbies: List<MyHobbyListItemEntity> = emptyList()
) : DataMapper<MyHobbyListDataDomain> {
    override fun toDomain(): MyHobbyListDataDomain = MyHobbyListDataDomain(
        currentHobbyStatus = currentHobbyStatus,
        inProgressHobbyCount = inProgressHobbyCount,
        archivedHobbyCount = archivedHobbyCount,
        hobbies = hobbies.map { it.toDomain() }
    )
}

data class MyHobbyListItemEntity(
    val hobbyId: Int,
    val hobbyName: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val goalDays: Int,
    val hobbyInfoId: Int?, // ✅ 추가
    val imageCode: String // ✅ 추가
) : DataMapper<MyHobbyListItemDomain> {
    override fun toDomain(): MyHobbyListItemDomain = MyHobbyListItemDomain(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        hobbyTimeMinutes = hobbyTimeMinutes,
        executionCount = executionCount,
        goalDays = goalDays,
        hobbyInfoId = hobbyInfoId, // ✅ 추가
        imageCode = imageCode // ✅ 추가
    )
}