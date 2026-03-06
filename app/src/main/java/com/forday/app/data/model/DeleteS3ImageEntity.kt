package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.DeleteS3ImageDataDomain
import com.forday.app.domain.model.DeleteS3ImageDomain

data class DeleteS3ImageEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: DeleteS3ImageDataEntity
) : DataMapper<DeleteS3ImageDomain> {
    override fun toDomain(): DeleteS3ImageDomain = DeleteS3ImageDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class DeleteS3ImageDataEntity(
    val message: String,
    val errorClassName: String?
) : DataMapper<DeleteS3ImageDataDomain> {
    override fun toDomain(): DeleteS3ImageDataDomain = DeleteS3ImageDataDomain(
        message = message,
        errorClassName = errorClassName
    )
}