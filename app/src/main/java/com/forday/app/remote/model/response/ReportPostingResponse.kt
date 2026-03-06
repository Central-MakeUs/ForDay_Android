package com.forday.app.remote.model.response

import com.forday.app.data.model.ReportPostingDataEntity
import com.forday.app.data.model.ReportPostingEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ReportPostingResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: ReportPostingDataResponse?
) : RemoteMapper<ReportPostingEntity> {
    override fun toData(): ReportPostingEntity = ReportPostingEntity(
        status = status ?: 0,
        success = success ?: false,
        data = data?.toData() ?: ReportPostingDataEntity()
    )
}

data class ReportPostingDataResponse(
    @SerializedName("recordId") val recordId: Int?,
    @SerializedName("recordWriterId") val recordWriterId: String?,
    @SerializedName("recordWriterNickname") val recordWriterNickname: String?,
    @SerializedName("message") val message: String?
) : RemoteMapper<ReportPostingDataEntity> {
    override fun toData(): ReportPostingDataEntity = ReportPostingDataEntity(
        recordId = recordId ?: 0,
        recordWriterId = recordWriterId ?: "",
        recordWriterNickname = recordWriterNickname ?: "",
        message = message ?: ""
    )
}
