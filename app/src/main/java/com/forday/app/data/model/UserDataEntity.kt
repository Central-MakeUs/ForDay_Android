package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.UserData

data class UserDataEntity(
    val id: String,
    val name: String,
    val description: String,
) : DataMapper<UserData> {
    override fun toDomain(): UserData = UserData(id, name, description)
}