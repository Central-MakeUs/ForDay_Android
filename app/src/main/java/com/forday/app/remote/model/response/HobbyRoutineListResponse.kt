package com.forday.app.remote.model.response

import com.forday.app.data.model.*
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class HobbyRoutineListResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val isSuccess: Boolean?,
    @SerializedName("data") val data: HobbyRoutineListDataResponse?
) : RemoteMapper<HobbyRoutineListEntity> {
    override fun toData(): HobbyRoutineListEntity = HobbyRoutineListEntity(
        status = status ?: 0,
        isSuccess = isSuccess ?: false,
        data = data?.toData() ?: HobbyRoutineListDataEntity(emptyList(), null, "")
    )
}

data class HobbyRoutineListDataResponse(
    @SerializedName("activities") val routines: List<RoutineItemResponse>?,
    @SerializedName("errorClassName") val errorClassName: String?,
    @SerializedName("message") val message: String?
) : RemoteMapper<HobbyRoutineListDataEntity> {
    override fun toData(): HobbyRoutineListDataEntity = HobbyRoutineListDataEntity(
        // NPE 방지 핵심: routines가 null일 경우 빈 리스트 반환
        routines = routines?.map { it.toData() } ?: emptyList(),
        errorClassName = errorClassName,
        message = message ?: ""
    )
}

data class RoutineItemResponse(
    @SerializedName("activityId") val routineId: Int?,
    @SerializedName("content") val content: String?,
    @SerializedName("aiRecommended") val aiRecommended: Boolean?,
    @SerializedName("deletable") val deletable: Boolean?,
    // API 명세의 "collectedStickerNum": 1 에 맞춰 Int로 변경
    @SerializedName("collectedStickerNum") val collectedStickerNum: Int?
) : RemoteMapper<RoutineDataEntity> {
    override fun toData(): RoutineDataEntity = RoutineDataEntity(
        routineId = routineId ?: -1,
        content = content ?: "",
        aiRecommended = aiRecommended ?: false,
        deletable = deletable ?: false,
        stickerCount = collectedStickerNum ?: 0
    )
}