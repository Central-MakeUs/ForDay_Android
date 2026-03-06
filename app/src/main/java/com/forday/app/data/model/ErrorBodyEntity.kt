package com.forday.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorBodyEntity(
  @SerialName("status") val status: Int = -1,
  @SerialName("data") val data: Data = Data(),
) {

  @Serializable
  data class Data(
    val message: String = "",
    val errorClassName: String = "",
  )
}
