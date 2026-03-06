package com.forday.app.data.remote

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
import com.forday.app.data.model.WriteRoutineEntity
import com.forday.app.data.model.PreviousAiRecommendEntity
import com.forday.app.data.model.RecreateHobbyEntity
import com.forday.app.remote.model.response.UpdateHobbyDurationResponse

interface HobbyDataSource {
    suspend fun getHobbyCardData(): HobbyCardEntity

    suspend fun createHobby(
        hobbyCardId: Long?,
        hobbyName: String?,
        hobbyTimeMinutes: Int?,
        hobbyPurpose: String?,
        executionCount: Int?,
        isDurationSet: Boolean?
    ): CreateHobbyEntity

    suspend fun searchHobbymateRoutines(
        hobbyId: Long?,
    ): SearchHobbyMateRoutinesEntity

    suspend fun createRoutines(hobbyId: Long?, routineList: List<Pair<Boolean, String>>): CreateRoutinesEntity

    suspend fun getAiRecommendedRoutines(hobbyId: Long?): AiRecommendedEntity

    suspend fun getHomeHobby(hobbyId: Long?): HomeHobbyEntity

    suspend fun getSpecificRoutineList(hobbyId: Long?, size: Int?): RoutineListEntity

    suspend fun getMyHobbyList(hobbyStatus: String?): MyHobbyListEntity

    suspend fun writeRoutine(routineId: Long, sticker: String, memo: String, imageUrl: String, visibility: String): WriteRoutineEntity
    suspend fun modifyHobbyTime(hobbyId: Long?, minutes: Int): UpdateHobbyTimeEntity
    suspend fun modifyHobbyExecutionCount(hobbyId: Long?, executionCount: Int): UpdateHobbyExecutionCountEntity
    suspend fun modifyHobbyDuration(hobbyId: Long?, goalDays: Boolean): UpdateHobbyDurationEntity
    suspend fun changeHobbyStatus(hobbyId: Long?, hobbyStatus: String): UpdateHobbyStatusEntity
    suspend fun getHobbyRoutineList(hobbyId: Long?): HobbyRoutineListEntity
    suspend fun modifyHobbyRoutine(routineId: Long, content: String): UpdateRoutineEntity
    suspend fun deleteHobbyRoutine(routineId: Long): DeleteRoutineEntity
    suspend fun getStickers(hobbyId: Long?, page: Int?, size: Int?): HobbyStickerHistoryEntity
    suspend fun extendHobbyPeriod(hobbyId: Long?, type: String): SetHobbyPeriodEntity
    suspend fun getUsersProgressHobbyTabs(userId: String?): UserHobbyTabEntity
    suspend fun setHobbyMainImage(hobbyId: Long?, coverImageUrl: String?, recordId: Long?): HobbyMainImageEntity
    suspend fun getHobbyCardDataAgain(): HobbyCardAgainEntity

    suspend fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String?): PreviousAiRecommendEntity

    suspend fun reCreateHobby(
        hobbyId: Long?,
        hobbyInfoId: Long?,
        hobbyName: String?,
        hobbyPurpose: String?,
        hobbyTimeMinutes: Int?,
        executionCount: Int?,
        durationSet: Boolean?
    ): RecreateHobbyEntity
}