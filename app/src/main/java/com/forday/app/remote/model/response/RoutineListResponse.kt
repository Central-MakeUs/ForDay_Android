package com.forday.app.remote.model.response

import com.forday.app.data.model.RoutineListDataEntity
import com.forday.app.data.model.RoutineListEntity
import com.forday.app.data.model.RoutineListItemEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName
import kotlin.collections.map

data class RoutineListResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: RoutineListData
) : RemoteMapper<RoutineListEntity> {
    override fun toData(): RoutineListEntity = RoutineListEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class RoutineListData(
    @SerializedName("activities") val activities: List<RoutineListItem>
) : RemoteMapper<RoutineListDataEntity> {
    override fun toData(): RoutineListDataEntity = RoutineListDataEntity(
        activities = activities.map { it.toData() }
    )
}

data class RoutineListItem(
    @SerializedName("activityId") val activityId: Int,
    @SerializedName("content") val content: String,
    @SerializedName("aiRecommended") val aiRecommended: Boolean
) : RemoteMapper<RoutineListItemEntity> {
    override fun toData(): RoutineListItemEntity = RoutineListItemEntity(
        activityId = activityId,
        content = content,
        aiRecommended = aiRecommended
    )
}