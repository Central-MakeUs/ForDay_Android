package com.forday.app.data.remote

import com.forday.app.data.model.CancelScrapEntity
import com.forday.app.data.model.ReportPostingEntity
import com.forday.app.data.model.DeletePostingEntity
import com.forday.app.data.model.ModifyPostingEntity
import com.forday.app.data.model.ReactionCancelEntity
import com.forday.app.data.model.ReactionEntity
import com.forday.app.data.model.ReactionUsersEntity
import com.forday.app.data.model.RoutineRecordDetailEntity
import com.forday.app.data.model.ScrapEntity
import com.forday.app.data.model.ScrapListEntity
import com.forday.app.data.model.UserFeedEntity
import com.forday.app.data.model.VisibilityEntity
import com.forday.app.remote.model.request.ModifyPostingRequest
import com.forday.app.remote.model.response.RoutineRecordDetailResponse

interface RoutineDataSource {

    suspend fun getMyRoutineRecordDetail(recordId: Int): RoutineRecordDetailEntity?
    suspend fun reactionToRoutinePosting(recordId: Int, reactionType: String): ReactionEntity
    suspend fun cancelMyReaction(recordId: Int, reactionType: String): ReactionCancelEntity
    suspend fun modifyPostingVisibility(recordId: Int, visibility: String): VisibilityEntity
    suspend fun getReactionUsers(recordId: Int, reactionType: String, lastUserId: String, size: Int): ReactionUsersEntity
    suspend fun getUserFeedList(hobbyIds: List<Int?>, lastRecordId: Long?, feedSize: Long?, userId: String? = null): UserFeedEntity
    suspend fun modifyPosting(recordId: Int, modifyPostingRequest: ModifyPostingRequest): ModifyPostingEntity
    suspend fun deletePosting(recordId: Long): DeletePostingEntity
    suspend fun getUserScrapList(lastScrapId: Long?, size: Long?, userId: String?): ScrapListEntity
    suspend fun scrapPosting(recordId: Int): ScrapEntity
    suspend fun cancelScrapPosting(recordId: Int): CancelScrapEntity
    suspend fun reportPosting(recordId: Int, reason: String): ReportPostingEntity
}