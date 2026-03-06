package com.forday.app.remote.model.response

import com.forday.app.data.model.HobbyTabEntity
import com.forday.app.data.model.UserHobbyTabEntity
import com.google.gson.annotations.SerializedName

data class UserHobbyTabResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserHobbyTabDataResponse
)

data class UserHobbyTabDataResponse(
    @SerializedName("inProgressHobbyCount") val inProgressHobbyCount: Int,
    @SerializedName("hobbyCardCount") val hobbyCardCount: Int,
    @SerializedName("hobbyList") val hobbyList: List<HobbyDto>
)

data class HobbyDto(
    @SerializedName("hobbyId") val hobbyId: Int,
    @SerializedName("hobbyName") val hobbyName: String,
    @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String,
    @SerializedName("status") val status: String
)

/**
 * Remote Mapper: Remote -> Data
 */
fun UserHobbyTabResponse.toData() = UserHobbyTabEntity(
    inProgressCount = data.inProgressHobbyCount,
    hobbyCardCount = data.hobbyCardCount,
    hobbies = data.hobbyList.map { it.toData() }
)

fun HobbyDto.toData() = HobbyTabEntity(
    hobbyId = this@toData.hobbyId,
    name = hobbyName,
    imageUrl = thumbnailImageUrl,
    status = status
)