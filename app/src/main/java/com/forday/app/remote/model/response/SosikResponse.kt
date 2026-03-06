package com.forday.app.remote.model.response

import com.forday.app.data.model.SosikDataEntity
import com.forday.app.data.model.SosikEntity
import com.forday.app.data.model.SosikRecordEntity
import com.forday.app.data.model.SosikTabInfoEntity
import com.forday.app.data.model.SosikUserInfoEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class SosikResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SosikDataResponse? = null
) : RemoteMapper<SosikEntity> {
    override fun toData(): SosikEntity = SosikEntity(
        status = status,
        success = success,
        data = data?.toData()
    )
}

data class SosikDataResponse(
    @SerializedName("tabInfo") val tabInfo: List<SosikTabInfoResponse>? = emptyList(),
    @SerializedName("lastRecordId") val lastRecordId: Long? = null,
    @SerializedName("recordList") val recordList: List<SosikRecordResponse>? = emptyList(),
    @SerializedName("hasNext") val hasNext: Boolean? = false
) : RemoteMapper<SosikDataEntity> {
    override fun toData(): SosikDataEntity = SosikDataEntity(
        tabInfo = tabInfo?.map { it.toData() } ?: emptyList(),
        lastRecordId = lastRecordId,
        recordList = recordList?.map { it.toData() } ?: emptyList(),
        hasNext = hasNext ?: false
    )
}

data class SosikTabInfoResponse(
    @SerializedName("hobbyId") val hobbyId: Long,
    @SerializedName("hobbyName") val hobbyName: String?,
    @SerializedName("currentHobby") val currentHobby: Boolean?
) : RemoteMapper<SosikTabInfoEntity> {
    override fun toData(): SosikTabInfoEntity = SosikTabInfoEntity(
        hobbyId = hobbyId,
        hobbyName = hobbyName ?: "",
        currentHobby = currentHobby ?: false
    )
}

data class SosikRecordResponse(
    @SerializedName("recordId") val recordId: Long,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String?,
    @SerializedName("sticker") val sticker: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("memo") val memo: String?,
    @SerializedName("userInfo") val userInfo: SosikUserInfoResponse?,
    @SerializedName("pressedAweSome") val pressedAweSome: Boolean?,
    @SerializedName("hobbyName") val hobbyName: String?,
    @SerializedName("recordAuthor") val recordAuthor: Boolean?
) : RemoteMapper<SosikRecordEntity> {
    override fun toData(): SosikRecordEntity = SosikRecordEntity(
        recordId = recordId,
        thumbnailUrl = thumbnailUrl ?: "",
        sticker = sticker ?: "",
        title = title ?: "",
        memo = memo ?: "",
        userInfo = userInfo?.toData() ?: SosikUserInfoEntity(userId = "", nickname = null, profileImageUrl = null),
        pressedAweSome = pressedAweSome ?: false,
        hobbyName = hobbyName ?: "",
        recordAuthor = recordAuthor ?: false
    )
}

data class SosikUserInfoResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
) : RemoteMapper<SosikUserInfoEntity> {
    override fun toData(): SosikUserInfoEntity = SosikUserInfoEntity(
        userId = userId ?: "",
        nickname = nickname,
        profileImageUrl = profileImageUrl
    )
}
