package com.forday.app.data.impl

import com.forday.app.data.model.toDomain
import com.forday.app.data.remote.RoutineDataSource
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
import com.forday.app.domain.repository.RoutineRepository
import com.forday.app.remote.model.request.ModifyPostingRequest
import timber.log.Timber
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDataSource: RoutineDataSource
): RoutineRepository {
    override suspend fun getMyRoutineRecordDetail(recordId: Int): RoutineRecordDetailDomain? =
        routineDataSource.getMyRoutineRecordDetail(recordId)?.toDomain()

    override suspend fun reactionToRoutinePosting(
        recordId: Int,
        reactionType: String
    ): ReactionDomain =
        routineDataSource.reactionToRoutinePosting(recordId, reactionType).toDomain()

    override suspend fun cancelMyReaction(
        recordId: Int,
        reactionType: String
    ): ReactionCancelDomain =
        routineDataSource.cancelMyReaction(recordId, reactionType).toDomain()

    override suspend fun modifyPostingVisibility(
        recordId: Int,
        visibility: String
    ): VisibilityDomain =
        routineDataSource.modifyPostingVisibility(recordId, visibility).toDomain()

    override suspend fun getReactionUsers(
        recordId: Int,
        reactionType: String,
        lastUserId: String,
        size: Int
    ): ReactionDetailDomain =
        routineDataSource.getReactionUsers(recordId, reactionType, lastUserId, size).toDomain()

    override suspend fun getUserFeedList(
        hobbyIds: List<Int?>,
        lastRecordId: Long?,
        feedSize: Long?,
        userId: String?
    ): UserFeedDomain =
        routineDataSource.getUserFeedList(hobbyIds, lastRecordId, feedSize, userId).toDomain()

    override suspend fun modifyPosting(
        recordId: Int,
        modifyPostingRequest: ModifyPostingRequest
    ): ModifyPostingDomain {
        Timber.e("modifyPostingRequest ::::@#@#@ "+modifyPostingRequest.memo)
        return routineDataSource.modifyPosting(recordId, modifyPostingRequest).toDomain()
    }

    override suspend fun deletePosting(recordId: Long): DeletePostingDomain =
        routineDataSource.deletePosting(recordId).toDomain()

    override suspend fun getUserScrapList(
        lastScrapId: Long?,
        size: Long?,
        userId: String?
    ): ScrapListDomain =
        routineDataSource.getUserScrapList(lastScrapId, size, userId).toDomain()

    override suspend fun scrapPosting(recordId: Int): ScrapDomain =
        routineDataSource.scrapPosting(recordId).toDomain()

    override suspend fun cancelScrapPosting(recordId: Int): CancelScrapDomain =
        routineDataSource.cancelScrapPosting(recordId).toDomain()

    override suspend fun reportPosting(recordId: Int, reason: String): ReportPostingDomain =
        routineDataSource.reportPosting(recordId, reason).toDomain()
}