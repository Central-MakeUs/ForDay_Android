package com.forday.app.remote.model.response

import com.forday.app.data.model.RoutineItemEntity
import com.forday.app.data.model.SearchHobbyMateRoutinesDataEntity
import com.forday.app.data.model.SearchHobbyMateRoutinesEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class SearchHobbyMateRoutinesResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: SearchHobbyMateRoutinesData
) : RemoteMapper<SearchHobbyMateRoutinesEntity> {
    override fun toData(): SearchHobbyMateRoutinesEntity {
        return SearchHobbyMateRoutinesEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class SearchHobbyMateRoutinesData(
    @SerializedName("message")
    val message: String,
    @SerializedName("activities")
    val routines: List<RoutineItem>
) : RemoteMapper<SearchHobbyMateRoutinesDataEntity> {
    override fun toData(): SearchHobbyMateRoutinesDataEntity {
        return SearchHobbyMateRoutinesDataEntity(
            message = message,
            activities = routines.map { it.toData() },
        )
    }
}

data class RoutineItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("content")
    val content: String
) : RemoteMapper<RoutineItemEntity> {
    override fun toData(): RoutineItemEntity {
        return RoutineItemEntity(
            id = id,
            content = content,
        )
    }
}