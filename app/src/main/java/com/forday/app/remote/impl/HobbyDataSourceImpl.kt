package com.forday.app.remote.impl

import com.forday.app.data.model.AiRecommendedEntity
import com.forday.app.data.model.CreateHobbyEntity
import com.forday.app.data.model.CreateRoutinesEntity
import com.forday.app.data.model.DeleteRoutineEntity
import com.forday.app.data.model.HobbyCardAgainEntity
import com.forday.app.data.model.HobbyCardEntity
import com.forday.app.data.model.HobbyMainImageEntity
import com.forday.app.data.model.HobbyRoutineListEntity
import com.forday.app.data.model.HobbyStickerHistoryEntity
import com.forday.app.data.model.HomeHobbyEntity
import com.forday.app.data.model.MyHobbyListEntity
import com.forday.app.data.model.RoutineListEntity
import com.forday.app.data.model.SearchHobbyMateRoutinesEntity
import com.forday.app.data.model.SetHobbyPeriodEntity
import com.forday.app.data.model.UpdateHobbyDurationEntity
import com.forday.app.data.model.UpdateHobbyExecutionCountEntity
import com.forday.app.data.model.UpdateHobbyStatusEntity
import com.forday.app.data.model.UpdateHobbyTimeEntity
import com.forday.app.data.model.UpdateRoutineEntity
import com.forday.app.data.model.UserHobbyTabEntity
import com.forday.app.data.model.PreviousAiRecommendEntity
import com.forday.app.data.model.WriteRoutineEntity
import com.forday.app.data.remote.HobbyDataSource
import com.forday.app.remote.api.service.HobbyApi
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
import com.forday.app.remote.model.request.RoutineItem
import com.forday.app.remote.model.request.RoutineRequest
import com.forday.app.remote.model.request.WriteRoutineRequest
import com.forday.app.remote.model.response.toData
import timber.log.Timber
import javax.inject.Inject

class HobbyDataSourceImpl @Inject constructor(
    private val hobbyApi: HobbyApi
) : HobbyDataSource {
    override suspend fun getHobbyCardData(): HobbyCardEntity =
        hobbyApi.getHobbyCardData().toData()

    override suspend fun createHobby(
        hobbyCardId: Long?,
        hobbyName: String?,
        hobbyTimeMinutes: Int?,
        hobbyPurpose: String?,
        executionCount: Int?,
        isDurationSet: Boolean?
    ): CreateHobbyEntity =
        hobbyApi.createHobby(
            CreateHobbyRequest(
                hobbyCardId,
                hobbyName,
                hobbyTimeMinutes,
                hobbyPurpose,
                executionCount,
                isDurationSet
            )
        ).toData()

    override suspend fun searchHobbymateRoutines(hobbyId: Long?): SearchHobbyMateRoutinesEntity =
        hobbyApi.searchHobbyMateRoutines(hobbyId).toData()

    override suspend fun createRoutines(
        hobbyId: Long?,
        routineList: List<Pair<Boolean, String>>
    ): CreateRoutinesEntity {
        val request = CreateRoutinesRequest(
            activities = routineList.map { RoutineItem(it.first, it.second) }
        )
        return hobbyApi.createRoutines(hobbyId, request).toData()
    }

    override suspend fun getAiRecommendedRoutines(hobbyId: Long?): AiRecommendedEntity =
        hobbyApi.getAiRecommendedRoutines(hobbyId).toData()

    override suspend fun getHomeHobby(hobbyId: Long?): HomeHobbyEntity =
        hobbyApi.getHomeHobby(hobbyId).toData()

    override suspend fun getSpecificRoutineList(hobbyId: Long?, size: Int?): RoutineListEntity =
        hobbyApi.getSpecificRoutineList(hobbyId, size).toData()

    override suspend fun getMyHobbyList(hobbyStatus: String?): MyHobbyListEntity =
        hobbyApi.getMyHobbyList(hobbyStatus).toData()

    override suspend fun writeRoutine(
        routineId: Long,
        sticker: String,
        memo: String,
        imageUrl: String,
        visibility: String
    ): WriteRoutineEntity =
        hobbyApi.writeRoutine(
            routineId = routineId,
            WriteRoutineRequest(
                sticker = sticker,
                memo = memo,
                imageUrl = imageUrl,
                visibility = visibility
            )
        ).toData()

    override suspend fun modifyHobbyTime(hobbyId: Long?, minutes: Int): UpdateHobbyTimeEntity =
        hobbyApi.modifyHobbyTime(hobbyId, ModifyHobbyTimeRequest(minutes)).toData()

    override suspend fun modifyHobbyExecutionCount(hobbyId: Long?, executionCount: Int): UpdateHobbyExecutionCountEntity =
        hobbyApi.modifyHobbyExecutionCount(hobbyId, ModifyHobbyExecutionCountRequest(executionCount)).toData()

    override suspend fun modifyHobbyDuration(hobbyId: Long?, goalDays: Boolean): UpdateHobbyDurationEntity =
        hobbyApi.modifyHobbyDuration(hobbyId, ModifyHobbyDurationRequest(goalDays)).toData()

    override suspend fun changeHobbyStatus(
        hobbyId: Long?,
        hobbyStatus: String
    ): UpdateHobbyStatusEntity =
        hobbyApi.changeHobbyStatus(hobbyId, HobbyStatusRequest(hobbyStatus)).toData()

    override suspend fun getHobbyRoutineList(hobbyId: Long?): HobbyRoutineListEntity =
        hobbyApi.getHobbyRoutineList(hobbyId).toData()

    override suspend fun modifyHobbyRoutine(routineId: Long, content: String): UpdateRoutineEntity =
        hobbyApi.modifyHobbyRoutine(routineId, RoutineRequest(content)).toData()

    override suspend fun deleteHobbyRoutine(routineId: Long): DeleteRoutineEntity =
        hobbyApi.deleteHobbyRoutine(routineId).toData()

    override suspend fun getStickers(
        hobbyId: Long?,
        page: Int?,
        size: Int?
    ): HobbyStickerHistoryEntity {
        return hobbyApi.getStickers(hobbyId, page, size).data?.toData()
            ?: HobbyStickerHistoryEntity(
                hobbyId = 0,
                durationSet = false,
                activityRecordedToday = false,
                currentPage = 0,
                totalPage = 0,
                pageSize = 0,
                totalStickerNum = 0,
                hasPrevious = false,
                hasNext = false,
                stickers = emptyList()
            )
    }

    override suspend fun extendHobbyPeriod(hobbyId: Long?, type: String): SetHobbyPeriodEntity =
        hobbyApi.extendHobbyPeriod(hobbyId, ExtendHobbyRequest(type)).toData()

    override suspend fun getUsersProgressHobbyTabs(userId: String?): UserHobbyTabEntity =
        hobbyApi.getUsersProgressHobbyTabs(userId).toData()

    override suspend fun setHobbyMainImage(
        hobbyId: Long?,
        coverImageUrl: String?,
        recordId: Long?
    ): HobbyMainImageEntity =
        hobbyApi.setHobbyMainImage(HobbyMainImageRequest(hobbyId, coverImageUrl, recordId)).toData()

    override suspend fun getHobbyCardDataAgain(): HobbyCardAgainEntity =
        hobbyApi.getHobbyCardDataAgain().toData()

    override suspend fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String?): PreviousAiRecommendEntity =
        hobbyApi.getAiRecommendedRoutinesAgain(hobbyId, type).toData()

    override suspend fun reCreateHobby(
        hobbyId: Long?,
        hobbyInfoId: Long?,
        hobbyName: String?,
        hobbyPurpose: String?,
        hobbyTimeMinutes: Int?,
        executionCount: Int?,
        durationSet: Boolean?
    ) = hobbyApi.reCreateHobby(
        hobbyId = hobbyId,
        body = RecreateHobbyRequest(
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            durationSet = durationSet
        )
    ).toData()

}