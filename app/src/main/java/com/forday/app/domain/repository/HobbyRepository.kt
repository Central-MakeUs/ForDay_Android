package com.forday.app.domain.repository

import com.forday.app.domain.model.AiRecommendedDomain
import com.forday.app.domain.model.CreateHobbyDomain
import com.forday.app.domain.model.CreateRoutinesDomain
import com.forday.app.domain.model.DeleteRoutineDomain
import com.forday.app.domain.model.HobbyCardAgainDomain
import com.forday.app.domain.model.HobbyCardDomain
import com.forday.app.domain.model.HobbyMainImageDomain
import com.forday.app.domain.model.HobbyRoutineListDomain
import com.forday.app.domain.model.HobbyStickerHistoryDomain
import com.forday.app.domain.model.HomeHobbyDomain
import com.forday.app.domain.model.MyHobbyListDomain
import com.forday.app.domain.model.RoutineListDomain
import com.forday.app.domain.model.SearchHobbyMateRoutinesDomain
import com.forday.app.domain.model.SetHobbyPeriodDomain
import com.forday.app.domain.model.UpdateHobbyDurationDomain
import com.forday.app.domain.model.UpdateHobbyExecutionCountDomain
import com.forday.app.domain.model.UpdateHobbyStatusDomain
import com.forday.app.domain.model.UpdateHobbyTimeDomain
import com.forday.app.domain.model.UpdateRoutineDomain
import com.forday.app.domain.model.UserHobbyTabDomain
import com.forday.app.domain.model.WriteRoutineDomain
import com.forday.app.domain.model.PreviousAiRecommendDomain
import com.forday.app.domain.model.RecreateHobbyDomain

interface HobbyRepository {
    suspend fun getHobbyCardData(): HobbyCardDomain

    suspend fun createHobby(
        selectedHobbyId: Long?,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        hobbyPeriod: Boolean
    ): CreateHobbyDomain

    suspend fun searchHobbyMateRoutines(selectedHobbyId: Long?): SearchHobbyMateRoutinesDomain

    suspend fun createRoutines(
        hobbyId: Long?,
        routineList: List<Pair<Boolean, String>>
    ): CreateRoutinesDomain

    suspend fun getAiRecommendedRoutines(hobbyId: Long?): AiRecommendedDomain

    suspend fun getHomeHobby(hobbyId: Long?): HomeHobbyDomain

    suspend fun getSpecificRoutineList(hobbyId: Long?, size: Int?): RoutineListDomain

    suspend fun getMyHobbyList(hobbyStatus: String?): MyHobbyListDomain

    suspend fun writeRoutine(routineId: Long, sticker: String, memo: String, imageUrl: String, visibility: String): WriteRoutineDomain
    suspend fun modifyHobbyTime(hobbyId: Long?, minutes: Int): UpdateHobbyTimeDomain
    suspend fun modifyHobbyExecutionCount(hobbyId: Long?, executionCount: Int): UpdateHobbyExecutionCountDomain
    suspend fun modifyHobbyDuration(hobbyId: Long?, goalDays: Boolean): UpdateHobbyDurationDomain
    suspend fun changeHobbyStatus(hobbyId: Long?, hobbyStatus: String): UpdateHobbyStatusDomain
    suspend fun getHobbyRoutineList(hobbyId: Long?): HobbyRoutineListDomain
    suspend fun modifyHobbyRoutine(routineId: Long, content: String): UpdateRoutineDomain
    suspend fun deleteHobbyRoutine(routineId: Long): DeleteRoutineDomain
    suspend fun getStickers(hobbyId: Long?, page: Int?, size: Int?): HobbyStickerHistoryDomain
    suspend fun extendHobbyPeriod(hobbyId: Long?, type: String): SetHobbyPeriodDomain
    suspend fun getUsersProgressHobbyTabs(userId: String?): UserHobbyTabDomain
    suspend fun setHobbyMainImage(hobbyId: Long?, coverImageUrl: String?, recordId: Long?): HobbyMainImageDomain
    suspend fun getHobbyCardDataAgain(): HobbyCardAgainDomain

    suspend fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String?): PreviousAiRecommendDomain

    suspend fun reCreateHobby(
        hobbyId: Long?,
        hobbyInfoId: Long?,
        hobbyName: String?,
        hobbyPurpose: String?,
        hobbyTimeMinutes: Int?,
        executionCount: Int?,
        durationSet: Boolean?
    ): RecreateHobbyDomain
}