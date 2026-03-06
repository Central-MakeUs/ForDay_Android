package com.forday.app.remote.model.response

import com.forday.app.data.model.LogoutEntity
import com.forday.app.remote.RemoteMapper

data class LogoutResponse(
    val success: Boolean,
    val status: Int,
    val data: LogoutData
) : RemoteMapper<LogoutEntity> { // Remote -> Data 변환을 위해 인터페이스 구현

    override fun toData(): LogoutEntity {
        // 인터페이스 메서드 이름이 toDomain으로 공통화되어 있다면 그대로 사용합니다.
        // (참고: 계층에 따라 toData() 등으로 이름을 정의하기도 합니다.)
        return LogoutEntity(
            message = this.data.message,
            isSuccess = this.success
        )
    }
}

data class LogoutData(
    val message: String
)