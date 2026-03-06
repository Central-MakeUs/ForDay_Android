package com.forday.app.presentation

import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.util.logAndExtractServerErrorBody
import com.forday.app.core.util.toUserMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import timber.log.Timber

inline fun <T> Flow<T>.httpCatch(
    tag: String,
    noinline onThrowable: ((Throwable) -> Unit)? = null,
    crossinline body: suspend (ErrorDataUiState) -> Unit,
): Flow<T> {
    return this.catch { throwable ->
        val message = when (throwable) {
            is HttpException -> throwable.logAndExtractServerErrorBody(tag = tag)
            else -> {
                Timber.e(
                    throwable,
                    "%sOtherException code=%d message=%s body=%s",
                    "[$tag] ",
                    999999,
                    throwable.message ?: "",
                    throwable.stackTraceToString(),
                )
                null
            }
        }

        val errorType = when {
            throwable is HttpException && throwable.code() in 400..499 -> ErrorDataUiState.ErrorType.TYPE_BACK
            else -> ErrorDataUiState.ErrorType.TYPE_RETRY
        }

        body(
            ErrorDataUiState(
                message = message?.data?.message ?: throwable.toUserMessage(),
                errorType = errorType,
                errorClassName = message?.data?.errorClassName
            )
        )

        onThrowable?.invoke(throwable)
    }
}
