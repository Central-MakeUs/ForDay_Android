package com.forday.app.domain.model

data class MyHobbyListDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: MyHobbyListDataDomain
)

data class MyHobbyListDataDomain(
    val currentHobbyStatus: String,  // 현재 선택된 취미의 상태
    val inProgressHobbyCount: Int,   // 진행 중(IN_PROGRESS) 상태의 취미 개수
    val archivedHobbyCount: Int,     // 보관(ARCHIVED) 상태의 취미 개수
    val hobbies: List<MyHobbyListItemDomain>  // 취미 목록
)

data class MyHobbyListItemDomain(
    val hobbyId: Int,           // 취미 고유 식별자
    val hobbyName: String,      // 취미 이름
    val hobbyTimeMinutes: Int,  // 1회 수행 기준 목표 시간(분)
    val executionCount: Int,    // 해당 취미의 누적 실행 횟수
    val goalDays: Int,          // 설정된 목표 일수
    val hobbyInfoId: Int?,      // ✅ 추가: 취미 정보 ID (nullable)
    val imageCode: String       // ✅ 추가: 취미 아이콘 코드
)