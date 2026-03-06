package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.DomainEntity

data class DataLayerEntity(
    val id: String,
    val name: String,
    val description: String,
) : DataMapper<DomainEntity> {
    override fun toDomain(): DomainEntity = DomainEntity(id, name, description)
}