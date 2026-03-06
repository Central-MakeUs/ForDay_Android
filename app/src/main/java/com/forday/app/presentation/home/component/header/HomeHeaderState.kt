//package com.forday.app.presentation.home.component.header
//
//import com.forday.app.domain.model.AiRoutineItemDomain
//import com.forday.app.presentation.home.StickerInfoUiModel
//import com.forday.app.presentation.home.StickerUiModel
//import com.forday.app.presentation.home.model.InProgressHobbyUiModel
//import com.forday.app.presentation.home.model.MyHobbyUiModel
//import com.forday.app.presentation.home.model.RoutinePreviewUiModel
//import com.forday.app.presentation.home.model.RoutineUiModel
//
//data class HomeHeaderState(
//    // --- 기존 필드 유지 ---
//  val hobbyId: Long? = null,
//  val hobbyFirst: String = "",
//  val hobbySecond: String = "",
//  val inProgressHobbies: List<InProgressHobbyUiModel> = emptyList(),
//  val greetingMessage: String ="",
//  val userSummaryText: String = "",
//  val recommendMessage: String = "",
//  val routinePreview: RoutinePreviewUiModel? = null,
//  val routineList: List<RoutineUiModel> = emptyList(),  // 여기에 routineId 있음
//  val aiCallRemaining: Boolean? = null,
//
//  val aiRoutineList: List<AiRoutineItemState> = emptyList(),
//  val aiCallCount: Int? = null,  // ai 호출횟수
//
//  val currentStickerPage: Int = 0,
//  val nickName: String? = "",
//
//    // --- HobbyStickerHistoryUiModel과 연동되는 필드 ---
//  val stickerCnt: Int = 0,                // totalStickerCount와 매핑
//  val isRecordedToday: Boolean = false,   // isRecordedToday와 매핑
//  val stickers: List<StickerUiModel> = emptyList(), // stickers 리스트 활성화
//  val isDurationSet: Boolean = false,      // [신규] 기간 설정 여부
//  val hasNextPage: Boolean = false,       // [신규] 다음 페이지 존재 여부
//  val currentPage: Int = 0,                // [신규] 현재 페이지 번호
//  val stickerInfo: StickerInfoUiModel? = null,
//
//    // --- 나머지 목록/UI 상태 필드 ---
//  val currentHobbyStatus: String = "",
//  val hobbyList: List<MyHobbyUiModel> = emptyList(),
//  val isLoading: Boolean = false,
//  val error: String? = null,
//  val routineId: Int? = null,  // AI 취미활동 생성한 고유 번호
//) {
//    val totalStickerPages: Int
//        get() = maxOf(1, (stickerCnt + 27) / 28)
//
//    // ✅ 계산 프로퍼티: 이전 페이지 이동 가능 여부
//    val canGoPreviousPage: Boolean
//        get() = currentStickerPage > 0
//
//    // ✅ 계산 프로퍼티: 다음 페이지 이동 가능 여부
//    val canGoNextPage: Boolean
//        get() = currentStickerPage < totalStickerPages - 1
//}
//
//data class AiRoutineItemState(
//    val routineId: Int,
//    val topic: String,
//    val content: String,
//    val description: String
//)
//
//fun AiRoutineItemDomain.toPresentation(): AiRoutineItemState {
//    return AiRoutineItemState(
//        routineId = this.routineId,
//        topic = this.topic,
//        content = this.content,
//        description = this.description
//    )
//}