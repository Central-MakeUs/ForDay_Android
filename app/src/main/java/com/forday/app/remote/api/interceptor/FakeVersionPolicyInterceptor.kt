package com.forday.app.remote.api.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

// TODO: 테스트용 - 배포 전 NetworkModule에서 제거할 것
class FakeVersionPolicyInterceptor : Interceptor {

    companion object {
        // 테스트할 케이스를 여기서 변경
        private val ACTIVE_MOCK = MockCase.MOCK_4
    }

    enum class MockCase {
        MOCK_1, // update: BLOCK      - api1.txt
        MOCK_2, // update: FORCE      - api2.txt
        MOCK_3, // update: RECOMMEND  - api3.txt
        MOCK_4, // update: NONE       - api4.txt
    }

    private val mock1 = """
        {
          "status": 200,
          "success": true,
          "data": {
            "policyVersion": 4,
            "platform": "ANDROID",
            "current": {
              "version": "1.1.0",
              "build": 150
            },
            "minSupported": {
              "version": "1.0.7",
              "build": 207
            },
            "latest": {
              "version": "1.2.0",
              "build": 230
            },
            "update": "BLOCK",
            "storeUrl": "https://play.google.com/store/apps/details?id=com.dayn.forday&hl=ko",
            "message": "현재 서비스 정기 점검 중입니다. (예정 시간: 14:00 ~ 17:00)"
          }
        }
    """.trimIndent()

    private val mock2 = """
        {
          "status": 200,
          "success": true,
          "data": {
            "policyVersion": 3,
            "platform": "ANDROID",
            "current": {
              "version": "1.0.0",
              "build": 100
            },
            "minSupported": {
              "version": "1.0.5",
              "build": 120
            },
            "latest": {
              "version": "1.1.0",
              "build": 150
            },
            "update": "FORCE",
            "storeUrl": "https://play.google.com/store/apps/details?id=com.dayn.forday&hl=ko",
            "message": "보안 업데이트가 포함되어 업데이트가 필요합니다."
          }
        }
    """.trimIndent()

    private val mock3 = """
        {
          "status": 200,
          "success": true,
          "data": {
            "policyVersion": 3,
            "platform": "ANDROID",
            "current": {
              "version": "1.1.0",
              "build": 100
            },
            "minSupported": {
              "version": "1.0.5",
              "build": 120
            },
            "latest": {
              "version": "1.1.0",
              "build": 150
            },
            "update": "RECOMMEND",
            "storeUrl": "https://play.google.com/store/apps/details?id=com.dayn.forday&hl=ko",
            "message": "새 기능이 추가되었습니다. 업데이트를 권장합니다."
          }
        }
    """.trimIndent()

    private val mock4 = """
        {
          "status": 200,
          "success": true,
          "data": {
            "policyVersion": 3,
            "platform": "ANDROID",
            "current": {
              "version": "1.1.0",
              "build": 150
            },
            "minSupported": {
              "version": "1.0.5",
              "build": 120
            },
            "latest": {
              "version": "1.1.0",
              "build": 150
            },
            "update": "NONE",
            "storeUrl": "https://play.google.com/store/apps/details?id=com.dayn.forday&hl=ko",
            "message": ""
          }
        }
    """.trimIndent()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!request.url.encodedPath.contains("/app/version-policy")) {
            return chain.proceed(request)
        }

        val fakeJson = when (ACTIVE_MOCK) {
            MockCase.MOCK_1 -> mock1
            MockCase.MOCK_2 -> mock2
            MockCase.MOCK_3 -> mock3
            MockCase.MOCK_4 -> mock4
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(fakeJson.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
