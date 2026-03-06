package com.forday.app.core.util

import com.forday.app.data.model.ErrorBodyEntity
import kotlinx.serialization.json.Json
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    encodeDefaults = true
}

fun HttpException.logAndExtractServerMessage(tag: String? = null): String? {
    val errorBody = runCatching { response()?.errorBody()?.string() }.getOrNull()

    Timber.e(
        this,
        "%sHttpException code=%d message=%s body=%s",
        if (tag.isNullOrBlank()) "" else "[$tag] ",
        code(),
        message(),
        errorBody
    )

    if (errorBody.isNullOrBlank()) return null

    return runCatching {
        val root = JSONObject(errorBody)
        val data = root.optJSONObject("data")
        data?.optString("message")
    }.getOrNull()
}

fun HttpException.logAndExtractServerErrorBody(tag: String? = null): ErrorBodyEntity? {
    val errorBody = runCatching { response()?.errorBody()?.string() }.getOrNull()

    Timber.e(
        this,
        "%sHttpException code=%d message=%s body=%s",
        if (tag.isNullOrBlank()) "" else "[$tag] ",
        code(),
        message(),
        errorBody
    )

    if (errorBody.isNullOrBlank()) return null
    return runCatching {
        json.decodeFromString<ErrorBodyEntity>(errorBody)
    }.getOrNull()
}
