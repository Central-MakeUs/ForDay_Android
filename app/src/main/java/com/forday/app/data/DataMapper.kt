package com.forday.app.data

internal interface DataMapper<DomainModel> {
    fun toDomain(): DomainModel
}