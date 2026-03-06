package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.PresignedUrlDataDomain
import com.forday.app.domain.model.PresignedUrlDomain
import com.forday.app.domain.model.PresignedUrlItemDomain

data class PresignedUrlEntity(
    val status: Int,
    val success: Boolean,
    val data: PresignedUrlDataEntity
) : DataMapper<PresignedUrlDomain> {
    override fun toDomain(): PresignedUrlDomain {
        return PresignedUrlDomain(
            status = status,
            success = success,
            data = data.toDomain()
        )
    }
}

data class PresignedUrlDataEntity(
    val images: List<PresignedUrlItemEntity>
) : DataMapper<PresignedUrlDataDomain> {
    override fun toDomain(): PresignedUrlDataDomain {
        return PresignedUrlDataDomain(
            images = images.map { it.toDomain() }
        )
    }
}

data class PresignedUrlItemEntity(
    val uploadUrl: String,
    val fileUrl: String,
    val order: Int
) : DataMapper<PresignedUrlItemDomain> {
    override fun toDomain(): PresignedUrlItemDomain {
        return PresignedUrlItemDomain(
            uploadUrl = uploadUrl,
            fileUrl = fileUrl,
            order = order
        )
    }
}