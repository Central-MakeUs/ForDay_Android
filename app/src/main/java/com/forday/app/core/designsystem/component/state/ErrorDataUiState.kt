package com.forday.app.core.designsystem.component.state

data class ErrorDataUiState(
    val message: String,
    val errorType: ErrorType,
    val errorClassName: String? = null,  // 특수한 에러 상황에 대한 처리를 위해 받는 errorClassName
) {

    enum class ErrorType {
        TYPE_RETRY,
        TYPE_BACK,
    }
}