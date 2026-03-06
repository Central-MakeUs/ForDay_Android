package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.CreateHobbyRequest
import com.forday.app.remote.model.request.CreateRoutinesRequest
import com.forday.app.remote.model.request.ExtendHobbyRequest
import com.forday.app.remote.model.request.HobbyIdRequest
import com.forday.app.remote.model.request.HobbyMainImageRequest
import com.forday.app.remote.model.request.HobbyStatusRequest
import com.forday.app.remote.model.request.ModifyHobbyDurationRequest
import com.forday.app.remote.model.request.ModifyHobbyExecutionCountRequest
import com.forday.app.remote.model.request.ModifyHobbyTimeRequest
import com.forday.app.remote.model.request.RecreateHobbyRequest
import com.forday.app.remote.model.request.RoutineRequest
import com.forday.app.remote.model.request.WriteRoutineRequest
import com.forday.app.remote.model.response.AiRecommendedResponse
import com.forday.app.remote.model.response.CreateHobbyResponse
import com.forday.app.remote.model.response.PreviousAiRecommendResponse
import com.forday.app.remote.model.response.CreateRoutinesResponse
import com.forday.app.remote.model.response.DeleteRoutineResponse
import com.forday.app.remote.model.response.HobbyCardAgainResponse
import com.forday.app.remote.model.response.HobbyCardResponse
import com.forday.app.remote.model.response.HobbyMainImageResponse
import com.forday.app.remote.model.response.HobbyRoutineListResponse
import com.forday.app.remote.model.response.HobbyStickerHistoryResponse
import com.forday.app.remote.model.response.HobbyStickerHistoryWrapperResponse
import com.forday.app.remote.model.response.MyHobbyListResponse
import com.forday.app.remote.model.response.HomeHobbyResponse
import com.forday.app.remote.model.response.RoutineListResponse
import com.forday.app.remote.model.response.SearchHobbyMateRoutinesResponse
import com.forday.app.remote.model.response.SetHobbyPeriodResponse
import com.forday.app.remote.model.response.UpdateHobbyDurationResponse
import com.forday.app.remote.model.response.UpdateHobbyExecutionCountResponse
import com.forday.app.remote.model.response.UpdateHobbyStatusResponse
import com.forday.app.remote.model.response.UpdateHobbyTimeResponse
import com.forday.app.remote.model.response.UpdateRoutineResponse
import com.forday.app.remote.model.response.UserHobbyTabResponse
import com.forday.app.remote.model.response.WriteRoutineResponse
import com.forday.app.remote.model.response.RecreateHobbyResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HobbyApi {
    @GET("/app/metadata")
    suspend fun getHobbyCardData(): HobbyCardResponse

    @POST("/hobbies/create")  //온보딩 끝날 때 온보딩 데이터 서버 저장하는 api (취미 생성)   token
    suspend fun createHobby(
        @Body body: CreateHobbyRequest
    ): CreateHobbyResponse

    @GET("/hobbies/activities/others/v1")  //다른 포비들의 활동 조회 (AI 기반) API  token
    suspend fun searchHobbyMateRoutines(
        @Query("hobbyId") hobbyId: Long?
    ): SearchHobbyMateRoutinesResponse

    @POST("/hobbies/{hobbyId}/activities")  // 취미 활동 추가 API  token
    suspend fun createRoutines(
        @Path("hobbyId") hobbyId: Long?, // URL의 {hobbyId}로 들어갈 값
        @Body request: CreateRoutinesRequest
    ): CreateRoutinesResponse

    @GET("/hobbies/activities/ai/recommend")
    suspend fun getAiRecommendedRoutines(  // AI 추천 취미활동 API  token
        @Query("hobbyId") hobbyId: Long?
    ): AiRecommendedResponse

    @GET("/hobbies/home")
    suspend fun getHomeHobby(  // TODO 홈 진입했을 때 취미정보 조회 (수정필요 - viewModel단까지 전체적으로)
        @Query("hobbyId") hobbyId: Long?
    ): HomeHobbyResponse

    @GET("/hobbies/{hobbyId}/activities")
    suspend fun getSpecificRoutineList( // 특정 취미의 활동 목록 조회
        @Path("hobbyId") hobbyId: Long?,
        @Query("size") size: Int?
    ): RoutineListResponse

    @GET("/hobbies/setting")
    suspend fun getMyHobbyList(  // 내 취미 설정 페이지 조회
        @Query("hobbyStatus") hobbyStatus: String?
    ): MyHobbyListResponse

    @POST("/hobbies/activities/{activityId}/record")   // 활동 기록하기
    suspend fun writeRoutine(
        @Path("activityId") routineId: Long,
        @Body body: WriteRoutineRequest
    ): WriteRoutineResponse

    @PATCH("/hobbies/{hobbyId}/time")  // 취미 정보 수정 (취미 시간)
    suspend fun modifyHobbyTime(
        @Path("hobbyId") hobbyId: Long?,
        @Body body: ModifyHobbyTimeRequest
    ): UpdateHobbyTimeResponse

    @PATCH("/hobbies/{hobbyId}/execution-count")   //취미 정보 수정 (취미 주당 횟수)
    suspend fun modifyHobbyExecutionCount(
        @Path("hobbyId") hobbyId: Long?,
        @Body body: ModifyHobbyExecutionCountRequest
    ): UpdateHobbyExecutionCountResponse

    @PATCH("/hobbies/{hobbyId}/goal-days")  // 취미 정보 수정 - 목표 기간
    suspend fun modifyHobbyDuration(
        @Path("hobbyId") hobbyId: Long?,
        @Body body: ModifyHobbyDurationRequest
    ): UpdateHobbyDurationResponse

    @PATCH("/hobbies/{hobbyId}/status")  // 취미 보관 또는 꺼내기 (취미 상태 변경)
    suspend fun changeHobbyStatus(
        @Path("hobbyId") hobbyId: Long?,
        @Body body: HobbyStatusRequest  // 바꾸고자 하는 취미 상태
    ): UpdateHobbyStatusResponse

    @GET("/hobbies/{hobbyId}/activities/list")  // 활동 리스트 조회
    suspend fun getHobbyRoutineList(
        @Path("hobbyId") hobbyId: Long?,
    ): HobbyRoutineListResponse

    @PATCH("/activities/{activityId}")
    suspend fun modifyHobbyRoutine(
        @Path("activityId") routineId: Long?,
        @Body body: RoutineRequest
    ): UpdateRoutineResponse

    @DELETE("/activities/{activityId}")    // 활동 삭제하기
    suspend fun deleteHobbyRoutine(
        @Path("activityId") routineId: Long?
    ): DeleteRoutineResponse

    @GET("/hobbies/stickers")  // 스티커판 조회
    suspend fun getStickers(
        @Query("hobbyId") hobbyId: Long?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): HobbyStickerHistoryWrapperResponse

    @PATCH("/hobbies/{hobbyId}/extension")// 취미 연장하기
    suspend fun extendHobbyPeriod(
        @Path("hobbyId") hobbyId: Long?,
        @Body body: ExtendHobbyRequest
    ): SetHobbyPeriodResponse

    @PATCH("/hobbies/cover-image")  // 취미 대표이미지 설정
    suspend fun setHobbyMainImage(
        @Body body: HobbyMainImageRequest
    ): HobbyMainImageResponse

    @GET("/users/hobbies/in-progress")   //사용자 취미 진행 상단탭 조회
    suspend fun getUsersProgressHobbyTabs(
        @Query("userId") userId: String?
    ): UserHobbyTabResponse

    @GET("/hobbies/info/re-check")
    suspend fun getHobbyCardDataAgain(): HobbyCardAgainResponse  // 취미 정보 재조회

    @PUT("/hobbies/{hobbyId}/update")  // 생성 완료된 취미 수정
    suspend fun reCreateHobby(
        @Path("hobbyId") hobbyId: Long?,
        @Body body: RecreateHobbyRequest
    ): RecreateHobbyResponse

    @GET("/activities/ai-recommend/items")  // AI 추천 활동 리스트 재조회
    suspend fun getAiRecommendedRoutinesAgain(
        @Query("hobbyId") hobbyId: Long?,
        @Query("type") type: String?,
    ): PreviousAiRecommendResponse
}