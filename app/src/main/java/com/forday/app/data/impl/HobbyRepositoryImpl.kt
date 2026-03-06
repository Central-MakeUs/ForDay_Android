package com.forday.app.data.impl

import com.forday.app.data.model.toDomain
import com.forday.app.data.remote.HobbyDataSource
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
import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class HobbyRepositoryImpl @Inject constructor(
    private val hobbyDataSource: HobbyDataSource,
) : HobbyRepository {

    override suspend fun getHobbyCardData(): HobbyCardDomain =
        hobbyDataSource.getHobbyCardData().toDomain()

    override suspend fun searchHobbyMateRoutines(selectedHobbyId: Long?): SearchHobbyMateRoutinesDomain =  // 나와 비슷한 취미를 가진 유저 루틴 api 요청
        hobbyDataSource.searchHobbymateRoutines(hobbyId = selectedHobbyId)
            .toDomain()

    override suspend fun createHobby(
        selectedHobbyId: Long?,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        hobbyPeriod: Boolean
    ): CreateHobbyDomain =
        hobbyDataSource.createHobby(
            hobbyCardId = selectedHobbyId,
            hobbyName = selectedHobbyName,
            hobbyTimeMinutes = selectedMinutes,
            hobbyPurpose = selectedPurpose,
            executionCount = selectedFrequency,
            isDurationSet = hobbyPeriod
        ).toDomain()

    override suspend fun createRoutines(   //취미 활동 생성
        hobbyId: Long?,
        routineList: List<Pair<Boolean, String>>
    ): CreateRoutinesDomain =
        hobbyDataSource.createRoutines(hobbyId, routineList).toDomain()

    override suspend fun getAiRecommendedRoutines(hobbyId: Long?): AiRecommendedDomain =
        // ai 추천 취미활동
        hobbyDataSource.getAiRecommendedRoutines(hobbyId).toDomain()

    override suspend fun getHomeHobby(hobbyId: Long?): HomeHobbyDomain =   // 홈 화면 진입했을 때 취미정보 조회
        hobbyDataSource.getHomeHobby(hobbyId).toDomain()

    override suspend fun getSpecificRoutineList(hobbyId: Long?, size: Int?): RoutineListDomain =
        hobbyDataSource.getSpecificRoutineList(hobbyId, size).toDomain()

    override suspend fun getMyHobbyList(hobbyStatus: String?): MyHobbyListDomain =         // 내 취미 설정 페이지 조회?
        hobbyDataSource.getMyHobbyList(hobbyStatus).toDomain()

    override suspend fun writeRoutine(  // 취미활동 기록하기
        routineId: Long,
        sticker: String,
        memo: String,
        imageUrl: String,
        visibility: String
    ): WriteRoutineDomain =
        hobbyDataSource.writeRoutine(
            routineId = routineId,
            sticker = sticker,
            memo = memo,
            imageUrl = imageUrl,
            visibility = visibility
        ).toDomain()

    override suspend fun modifyHobbyTime(hobbyId: Long?, minutes: Int): UpdateHobbyTimeDomain =
        hobbyDataSource.modifyHobbyTime(hobbyId, minutes).toDomain()

    override suspend fun modifyHobbyExecutionCount(hobbyId: Long?, executionCount: Int): UpdateHobbyExecutionCountDomain =
        hobbyDataSource.modifyHobbyExecutionCount(hobbyId, executionCount).toDomain()

    override suspend fun modifyHobbyDuration(hobbyId: Long?, goalDays: Boolean): UpdateHobbyDurationDomain =
        hobbyDataSource.modifyHobbyDuration(hobbyId, goalDays).toDomain()

    override suspend fun changeHobbyStatus(
        hobbyId: Long?,
        hobbyStatus: String
    ): UpdateHobbyStatusDomain =
        hobbyDataSource.changeHobbyStatus(hobbyId, hobbyStatus).toDomain()

    override suspend fun getHobbyRoutineList(hobbyId: Long?): HobbyRoutineListDomain =
        hobbyDataSource.getHobbyRoutineList(hobbyId).toDomain()

    override suspend fun modifyHobbyRoutine(routineId: Long, content: String): UpdateRoutineDomain =
        hobbyDataSource.modifyHobbyRoutine(routineId, content).toDomain()

    override suspend fun deleteHobbyRoutine(routineId: Long): DeleteRoutineDomain =
        hobbyDataSource.deleteHobbyRoutine(routineId).toDomain()

    override suspend fun getStickers(
        hobbyId: Long?,
        page: Int?,
        size: Int?
    ): HobbyStickerHistoryDomain =
        hobbyDataSource.getStickers(hobbyId, page, size).toDomain()

    override suspend fun extendHobbyPeriod(hobbyId: Long?, type: String): SetHobbyPeriodDomain =
        hobbyDataSource.extendHobbyPeriod(hobbyId, type).toDomain()

    override suspend fun getUsersProgressHobbyTabs(userId: String?): UserHobbyTabDomain =
        hobbyDataSource.getUsersProgressHobbyTabs(userId).toDomain()

    override suspend fun setHobbyMainImage(
        hobbyId: Long?,
        coverImageUrl: String?,
        recordId: Long?
    ): HobbyMainImageDomain =
        hobbyDataSource.setHobbyMainImage(hobbyId, coverImageUrl, recordId).toDomain()

    override suspend fun getHobbyCardDataAgain(): HobbyCardAgainDomain =
        hobbyDataSource.getHobbyCardDataAgain().toDomain()

    override suspend fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String?): PreviousAiRecommendDomain =
        hobbyDataSource.getAiRecommendedRoutinesAgain(hobbyId, type).toDomain()

    override suspend fun reCreateHobby(
        hobbyId: Long?,
        hobbyInfoId: Long?,
        hobbyName: String?,
        hobbyPurpose: String?,
        hobbyTimeMinutes: Int?,
        executionCount: Int?,
        durationSet: Boolean?
    ): RecreateHobbyDomain =
        hobbyDataSource.reCreateHobby(
            hobbyId = hobbyId,
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            durationSet = durationSet
        ).toDomain()
}