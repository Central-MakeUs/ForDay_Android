package com.forday.app.remote.model.response

import com.forday.app.data.model.DeletePostingDataEntity
import com.forday.app.data.model.DeletePostingEntity
import com.forday.app.remote.RemoteMapper
import kotlinx.serialization.Serializable

@Serializable
data class DeletePostingResponse(
    val status: Int,
    val success: Boolean,
    val data: DeletePostingDataResponse
) : RemoteMapper<DeletePostingEntity> {
    override fun toData(): DeletePostingEntity {
        return DeletePostingEntity(
            status = status,
            isSuccess = success,
            data = data.toData()
        )
    }
}

@Serializable
data class DeletePostingDataResponse(
    val message: String? = null,
    val recordId: Int? = null,
    val errorClassName: String? = null
) : RemoteMapper<DeletePostingDataEntity> {
    override fun toData(): DeletePostingDataEntity {
        return DeletePostingDataEntity(
            message = message.orEmpty(),
            recordId = recordId ?: -1, // 성공 시 값이 없으면 -1
            errorClassName = errorClassName.orEmpty()
        )
    }
}