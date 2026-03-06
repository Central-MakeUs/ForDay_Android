package com.forday.app.presentation.mypage.routinedetail

import kotlinx.serialization.Serializable

/**
 * 루틴 기록 상세 화면에서 사용하는 UI 모델
 */
@Serializable
data class RoutineRecordDetailUiModel(
    val hobbyId: Int = 0,
    val hobbyName: String = "",
    val routineId: Int = 0,                // activityId: 루틴(활동) 자체의 고유 ID
    val content: String = "",       // activityContent: 루틴의 내용 (예: "내 기분과 비슷한 문장 찾기")
    val recordId: Int = 0,          // activityRecordId: 해당 날짜에 기록된 특정 기록의 고유 ID
    val imageUrl: String = "",      // imageUrl: 사용자가 업로드한 인증 사진 URL
    val stickerUrl: String = "",    // sticker: 선택된 스티커 파일명 또는 URL (예: "smile.jpg")
    val isScraped: Boolean = false,
    val writerId: String = "",
    val writerNickname: String = "",
    val writerProfileImageUrl: String = "",
    val date: String = "",          // createdAt: 기록 생성 일시 (예: "2026-01-24 오후 06:06")
    val memo: String = "",          // memo: 사용자가 남긴 한 줄 메모 (예: "오늘 허벅지 불타는 줄")
    val isMine: Boolean = false,    // recordOwner: 현재 로그인한 사용자가 이 기록의 작성자인지 여부
    val isPublic: Boolean = false,  // visibility: 공개 여부 (JSON의 "PUBLIC"이면 true, "PRIVATE"이면 false)
    val reactions: RoutineReactionUiModel = RoutineReactionUiModel(), // newReaction: 새로운 반응 존재 여부 데이터
    val myReactions: RoutineUserReactionUiModel = RoutineUserReactionUiModel() // userReaction: 내가 누른 반응 데이터
)

/**
 * 루틴 기록에 대한 전체 반응 상태 (새로운 반응 알림 등)
 */
@Serializable
data class RoutineReactionUiModel(
    val awesome: Boolean = false,   // newAweSome: '멋져요' 반응 여부
    val great: Boolean = false,     // newGreat: '최고예요' 반응 여부
    val amazing: Boolean = false,   // newAmazing: '놀라워요' 반응 여부
    val fighting: Boolean = false   // newFighting: '응원해요' 반응 여부
)

/**
 * 현재 로그인한 사용자가 해당 기록에 남긴 반응 상태
 */
@Serializable
data class RoutineUserReactionUiModel(
    val pressedAwesome: Boolean = false,  // pressedAweSome: 내가 '멋져요'를 눌렀는지 여부
    val pressedGreat: Boolean = false,    // pressedGreat: 내가 '최고예요'를 눌렀는지 여부
    val pressedAmazing: Boolean = false,  // pressedAmazing: 내가 '놀라워요'를 눌렀는지 여부
    val pressedFighting: Boolean = false  // pressedFighting: 내가 '응원해요'를 눌렀는지 여부
)