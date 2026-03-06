package com.forday.app.domain.repository

import com.forday.app.domain.model.CancelScrapDomain
import com.forday.app.domain.model.ReportPostingDomain
import com.forday.app.domain.model.DeletePostingDomain
import com.forday.app.domain.model.ModifyPostingDomain
import com.forday.app.domain.model.ReactionCancelDomain
import com.forday.app.domain.model.ReactionDetailDomain
import com.forday.app.domain.model.ReactionDomain
import com.forday.app.domain.model.RoutineRecordDetailDomain
import com.forday.app.domain.model.ScrapDomain
import com.forday.app.domain.model.ScrapListDomain
import com.forday.app.domain.model.UserFeedDomain
import com.forday.app.domain.model.VisibilityDomain
import com.forday.app.remote.model.request.ModifyPostingRequest

interface RoutineRepository {
    suspend fun getMyRoutineRecordDetail(recordId: Int): RoutineRecordDetailDomain?
    suspend fun reactionToRoutinePosting(recordId: Int, reactionType: String): ReactionDomain
    suspend fun cancelMyReaction(recordId: Int, reactionType: String): ReactionCancelDomain
    suspend fun modifyPostingVisibility(recordId: Int, visibility: String): VisibilityDomain
    suspend fun getReactionUsers(recordId: Int, reactionType: String, lastUserId: String, size: Int): ReactionDetailDomain
    suspend fun getUserFeedList(hobbyIds: List<Int?>, lastRecordId: Long?, feedSize: Long?, userId: String? = null): UserFeedDomain
    suspend fun modifyPosting(recordId: Int, modifyPostingRequest: ModifyPostingRequest): ModifyPostingDomain
    suspend fun deletePosting(recordId: Long): DeletePostingDomain
    suspend fun getUserScrapList(lastScrapId: Long?, size: Long?, userId: String?): ScrapListDomain
    suspend fun scrapPosting(recordId: Int): ScrapDomain
    suspend fun cancelScrapPosting(recordId: Int): CancelScrapDomain
    suspend fun reportPosting(recordId: Int, reason: String): ReportPostingDomain
}