package com.forday.app.core.util

import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class UserMessageCategory {
    AUTH,
    COMMON
}

fun Throwable.toUserMessage(category: UserMessageCategory = UserMessageCategory.COMMON): String {
    return when (this) {
        is ClientError -> {
            if (reason == ClientErrorCause.Cancelled) {
                "로그인이 취소되었어요."
            } else {
                "로그인에 실패했어요. 잠시 후 다시 시도해주세요."
            }
        }

        is UnknownHostException,
        is SocketTimeoutException,
        is IOException -> "네트워크 연결을 확인해주세요."

        is HttpException -> {
            when (code()) {
                401, 403 -> {
                    if (category == UserMessageCategory.AUTH) {
                        "인증에 실패했어요. 다시 로그인해주세요."
                    } else {
                        "요청을 처리할 수 없어요. 다시 시도해주세요."
                    }
                }
                409 -> "이미 존재하는 소셜 계정입니다. 다시 로그인해주세요."
                in 500..599 -> "서버에 문제가 있어요. 잠시 후 다시 시도해주세요."
                else -> "요청에 실패했어요. 잠시 후 다시 시도해주세요."
            }
        }

        else -> {
            if (category == UserMessageCategory.AUTH) {
                "로그인 처리 중 오류가 발생했습니다."
            } else {
                Timber.e("@#@#@#@#@# "+cause)
                "오류가 발생했습니다."
            }
        }
    }
}
