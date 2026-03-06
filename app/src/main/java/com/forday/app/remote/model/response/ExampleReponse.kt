package com.forday.app.remote.model.response

import androidx.compose.runtime.Immutable
import com.forday.app.data.model.DataLayerEntity
import com.forday.app.remote.RemoteMapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ExampleReponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
) : RemoteMapper<DataLayerEntity> {
    override fun toData(): DataLayerEntity = DataLayerEntity(id, name, description)
}