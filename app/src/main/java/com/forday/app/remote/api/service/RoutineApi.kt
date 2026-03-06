package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.GetUserScrapListRequest
import com.forday.app.remote.model.request.ModifyPostingRequest
import com.forday.app.remote.model.request.PostingVisibilityRequest
import com.forday.app.remote.model.request.ReactionRequest
import com.forday.app.remote.model.request.ReportPostingRequest
import com.forday.app.remote.model.response.CancelScrapResponse
import com.forday.app.remote.model.response.DeletePostingResponse
import com.forday.app.remote.model.response.SosikResponse
import com.forday.app.remote.model.response.ModifyPostingResponse
import com.forday.app.remote.model.response.ReactionCancelResponse
import com.forday.app.remote.model.response.ReactionResponse
import com.forday.app.remote.model.response.ReactionUsersResponse
import com.forday.app.remote.model.response.ReportPostingResponse
import com.forday.app.remote.model.response.RoutineRecordDetailResponse
import com.forday.app.remote.model.response.ScrapListResponse
import com.forday.app.remote.model.response.ScrapResponse
import com.forday.app.remote.model.response.UserFeedResponse
import com.forday.app.remote.model.response.VisibilityResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutineApi {

    @GET("/records/{recordId}")
    suspend fun getMyRoutineRecordDetail(  // 활동 기록 상세 조회
        @Path("recordId") recordId: Int,
    ): RoutineRecordDetailResponse

    @POST("/records/{recordId}/reaction")  //활동 기록에 반응 남기기
    suspend fun reactionToRoutinePosting(
        @Path("recordId") recordId: Int,
        @Body body: ReactionRequest
    ): ReactionResponse

    @DELETE("/records/{recordId}/reaction") // 활동 기록에 반응 취소하기
    suspend fun cancelMyReaction(
        @Path("recordId") recordId: Int,
        @Query("reactionType") reactionType: String
    ): ReactionCancelResponse

    @PATCH("/records/{recordId}/visibility") // 내 활동 기록 - 공개 범위 수정
    suspend fun modifyPostingVisibility(
        @Path("recordId") recordId: Int,
        @Body body: PostingVisibilityRequest
    ): VisibilityResponse

    @GET("/records/{recordId}/reaction-users") // 활동 기록에 새로 반응한 사용자 목록 조회
    suspend fun getReactionUsers(
        @Path("recordId") recordId: Int,
        @Query("reactionType") reactionType: String,
        @Query("lastUserId") lastUserId: String,  // 필수 x
        @Query("size") size: Int                  // 필수 x, Default value : 10
    ): ReactionUsersResponse

    @GET("/users/feeds")  // 나의 활동 피드 목록 조회
    suspend fun getMyRoutineFeedList(
        @Query("hobbyId") hobbyIds: List<Int?>,
        @Query("lastRecordId") lastRecordId: Long?,
        @Query("feedSize") feedSize: Long?,
        @Query("userId") userId: String?,
    ): UserFeedResponse

    @PUT("/records/{recordId}")  // 활동 기록 수정하기
    suspend fun modifyPosting(
        @Path("recordId") recordId: Int,
        @Body body: ModifyPostingRequest
    ): ModifyPostingResponse

    @DELETE("/records/{recordId}")  //활동 기록 삭제하기
    suspend fun deletePosting(
        @Path("recordId") recordId: Long
    ): DeletePostingResponse

    @GET("/users/scraps")
    suspend fun getUserScrapList(  // 사용자 스크랩 목록 조회
        @Query("lastScrapId") lastScrapId: Long?,
        @Query("size") size: Long?,
        @Query("userId") userId: String?,
    ): ScrapListResponse

    @POST("/records/{recordId}/scrap")  // 활동 기록 스크랩 추가
    suspend fun scrapPosting(
        @Path("recordId") recordId: Int
    ): ScrapResponse

    @DELETE("/records/{recordId}/scrap")  // 활동 기록 스크랩 취소
    suspend fun cancelScrap(
        @Path("recordId") recordId: Int
    ): CancelScrapResponse

    @GET("/records/stories")  // 소식에서 기록 목록 조회
    suspend fun getPeopleRoutineList(
        @Query("hobbyId") hobbyId: Long?,
        @Query("lastRecordId") lastRecordId: Long?,
        @Query("size") size: Long?,
        @Query("keyword") keyword: String?,
        @Query("storyFilterType") storyFilterType: String?
    ): SosikResponse

    @POST("/records/{recordId}/report")  // 활동기록 신고하기
    suspend fun reportPosting(
        @Path("recordId") recordId: Int,
        @Body body: ReportPostingRequest
    ): ReportPostingResponse
}