package com.forday.app.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.forday.app.data.model.OnboardingDataEntity
import com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class UserLocalDataSource @Inject constructor(
    @Named("user") private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val USER_NICKNAME = stringPreferencesKey("user_nickname")
        private val KAKAO_USER_ID = stringPreferencesKey("kakao_user_id")
        private val GUEST_USER_ID = stringPreferencesKey("guest_user_id")

        private val HOBBY_1 = stringPreferencesKey("hobby_1")          //취미명
        private val HOBBY_INFO_1 = intPreferencesKey("hobby_info_1")  //취미카드 id
        private val HOBBY_ID_1 = longPreferencesKey("hobby_id_1")      //취미 고유식별자
        private val HOBBY_2 = stringPreferencesKey("hobby_2")
        private val HOBBY_INFO_2 = longPreferencesKey("hobby_info_2")
        private val HOBBY_ID_2 = intPreferencesKey("hobby_id_2")
        private val HOBBY_TAKE_TIME = intPreferencesKey("hobby_take_time")
        private val HOBBY_PURPOSE = stringPreferencesKey("hobby_purpose_1")
        private val HOBBY_PURPOSE_2 = stringPreferencesKey("hobby_purpose_2")
        private val HOBBY_PURPOSE_3 = stringPreferencesKey("hobby_purpose_3")
        private val HOBBY_PURPOSE_4 = stringPreferencesKey("hobby_purpose_4")
        private val HOBBY_PER_WEEK = intPreferencesKey("hobby_per_week")
        private val HOBBY_PERIOD = booleanPreferencesKey("hobby_period")
        private val HOBBY_ROUTINE_1 = stringPreferencesKey("hobby_routine_1")
        private val HOBBY_ROUTINE_2 = stringPreferencesKey("hobby_routine_2")
        private val HOBBY_ROUTINE_3 = stringPreferencesKey("hobby_routine_3")

        private val ACCESS_TOKEN = stringPreferencesKey("kakao_access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("kakao_refresh_token")
        private val SOCIAL_TYPE = stringPreferencesKey("social_type")

        private val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        private val IS_NICKNAME_SET = booleanPreferencesKey("is_nickname_set")
        private val HAS_SEEN_INTRO = booleanPreferencesKey("has_seen_intro")

        private val CREATED_HOBBY_ID = longPreferencesKey("created_hobby_id")

        private val AI_ROUTINE_LIST = stringPreferencesKey("ai_routine_list")

        private val LEGACY_HOBBY_ID_1_STRING = stringPreferencesKey("hobby_id_1")
        private val LEGACY_HOBBY_INFO_1_LONG = longPreferencesKey("hobby_info_1")
    }

    val userNicknameFlow = dataStore.data.map { it[USER_NICKNAME] ?: null }
    val kakaoIdFlow = dataStore.data.map { it[KAKAO_USER_ID] ?: "" }
    val guestIdFlow = dataStore.data.map { it[GUEST_USER_ID] ?: null }
    val hobbyFirstFlow = dataStore.data.map { it[HOBBY_1] ?: "" }
    val hobbyFirstIdFlow = dataStore.data.map { it[HOBBY_ID_1] ?: 0L }
    val hobbySecondFlow = dataStore.data.map { it[HOBBY_2] ?: "" }
    val hobbySecondIdFlow = dataStore.data.map { it[HOBBY_ID_2] ?: 0 }
    val hobbyTakeTimeFlow = dataStore.data.map { it[HOBBY_TAKE_TIME] ?: 0 }
    val hobbyPurposeFirst = dataStore.data.map { it[HOBBY_PURPOSE] ?: "" }
    val hobbyPurposeSecond = dataStore.data.map { it[HOBBY_PURPOSE_2] ?: "" }
    val hobbyPurposeThird = dataStore.data.map { it[HOBBY_PURPOSE_3] ?: "" }
    val hobbyPurposeFourth = dataStore.data.map { it[HOBBY_PURPOSE_4] ?: "" }
    val hobbyPerWeekFlow = dataStore.data.map { it[HOBBY_PER_WEEK] ?: 0 }
    val hobbyPeriodFlow = dataStore.data.map { it[HOBBY_PERIOD] == true }
    val hobbyRoutineFirst = dataStore.data.map { it[HOBBY_ROUTINE_1] ?: "" }
    val hobbyRoutineSecond = dataStore.data.map { it[HOBBY_ROUTINE_2] ?: "" }
    val hobbyRoutineThird = dataStore.data.map { it[HOBBY_ROUTINE_3] ?: "" }
    val accessTokenFlow = dataStore.data.map { it[ACCESS_TOKEN] ?: null }
    val refreshTokenFlow = dataStore.data.map { it[REFRESH_TOKEN] ?: null }
    val socialTypeFlow = dataStore.data.map { it[SOCIAL_TYPE] ?: "" }
    val isOnboardingCompletedFlow = dataStore.data.map { it[IS_ONBOARDING_COMPLETED] == true }
    val isNicknameSetFlow = dataStore.data.map { it[IS_NICKNAME_SET] == true }
    val hasSeenIntroFlow = dataStore.data.map { it[HAS_SEEN_INTRO] == true }

    suspend fun saveUserNickname(nickname: String) {
        dataStore.edit { it[USER_NICKNAME] = nickname }
    }

    suspend fun saveKakaoId(id: String) {
        dataStore.edit { it[KAKAO_USER_ID] = id }
    }

    suspend fun saveGuestId(id: String) {
        dataStore.edit { it[GUEST_USER_ID] = id }
    }

    suspend fun saveHobbyFirst(hobby: String) {
        dataStore.edit { it[HOBBY_1] = hobby }
    }

    suspend fun saveHobbySecond(hobby: String) {
        dataStore.edit { it[HOBBY_2] = hobby }
    }

    suspend fun saveHobbyTakeTime(takeTime: Int) {
        dataStore.edit { it[HOBBY_TAKE_TIME] = takeTime }
    }

    suspend fun saveHobbyPurposeFirst(purpose: String) {
        dataStore.edit { it[HOBBY_PURPOSE] = purpose }
    }

    suspend fun saveHobbyPurposeSecond(purpose: String) {
        dataStore.edit { it[HOBBY_PURPOSE_2] = purpose }
    }

    suspend fun saveHobbyPurposeThird(purpose: String) {
        dataStore.edit { it[HOBBY_PURPOSE_3] = purpose }
    }

    suspend fun saveHobbyPurposeFourth(purpose: String) {
        dataStore.edit { it[HOBBY_PURPOSE_4] = purpose }
    }

    suspend fun saveHobbyPerWeek(perWeek: Int) {
        dataStore.edit { it[HOBBY_PER_WEEK] = perWeek }
    }

    suspend fun saveHobbyPeriod(period: Boolean) {
        dataStore.edit { it[HOBBY_PERIOD] = period }
    }

    suspend fun saveHobbyRoutineFirst(routine: String) {
        dataStore.edit { it[HOBBY_ROUTINE_1] = routine }
    }

    suspend fun saveHobbyRoutineSecond(routine: String) {
        dataStore.edit { it[HOBBY_ROUTINE_2] = routine }
    }

    suspend fun saveHobbyRoutineThird(routine: String) {
        dataStore.edit { it[HOBBY_ROUTINE_3] = routine }
    }

    suspend fun saveSocialType(socialType: String) {
        dataStore.edit { it[SOCIAL_TYPE] = socialType }
    }

    suspend fun saveIsOnboardingCompleted(isOnboardingCompleted: Boolean) {
        dataStore.edit { it[IS_ONBOARDING_COMPLETED] = isOnboardingCompleted }
    }

    suspend fun saveIsNicknameSet(isNicknameSet: Boolean) {
        dataStore.edit { it[IS_NICKNAME_SET] = isNicknameSet }
    }

    suspend fun saveHasSeenIntro(hasSeen: Boolean) {
        dataStore.edit { it[HAS_SEEN_INTRO] = hasSeen }
    }

    suspend fun saveAiRoutineList(routines: List<AiRoutineItemState>) {
        val json = Json.encodeToString(routines)
        dataStore.edit { it[AI_ROUTINE_LIST] = json }
    }

    suspend fun saveKakaoToken(accessToken: String, refreshToken: String, socialType: String) {
        dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
            it[SOCIAL_TYPE] = socialType
        }
    }

    suspend fun saveNickname(nickname: String?) {
        dataStore.edit { it[USER_NICKNAME] = nickname ?: "" }
    }

    suspend fun saveGuestTokenAndId(accessToken: String, refreshToken: String, userId: String, socialType: String) {
        dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
            it[GUEST_USER_ID] = userId
            it[SOCIAL_TYPE] = socialType
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { it[ACCESS_TOKEN] = accessToken }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { it[REFRESH_TOKEN] = refreshToken }
    }

    suspend fun saveCreatedHobbyId(hobbyId: Long) {
        dataStore.edit { it[HOBBY_ID_1] = hobbyId }  //서버로부터 받은 취미 id
    }

    suspend fun saveOnboardingData(selectedHobbyId: Long?, selectedHobbyName: String?, selectedMinutes: Int?, selectedPurpose: String?, selectedFrequency: Int?, selectedPeriod: Boolean) {
        Timber.e("@@@@@@@@@@@@@@saveOnboardingData "+selectedHobbyId+", "+selectedHobbyName+", "+selectedMinutes+", "+selectedPurpose+", "+selectedFrequency+", "+selectedPeriod)
        dataStore.edit {
//            it.remove(LEGACY_HOBBY_ID_1_STRING)  // 주석 풀면 hobbyId가 없어짐
//            it.remove(LEGACY_HOBBY_INFO_1_LONG)
            it[HOBBY_INFO_1] = selectedHobbyId?.toInt() ?: 0  // 취미카드 id
            it[HOBBY_1] = selectedHobbyName ?: ""
            it[HOBBY_TAKE_TIME] = selectedMinutes ?: 0  //근데 애초에 null일 리가 없음.....
            it[HOBBY_PURPOSE] = selectedPurpose ?: ""
            it[HOBBY_PER_WEEK] = selectedFrequency ?: 0  //근데 애초에 null일 리가 없음.....
            it[HOBBY_PERIOD] = selectedPeriod
        }
    }

    fun getHobbyId(): Flow<Long> {// 나중엔 지우기..? UT용으로 우선 넣어둠 todo
        return dataStore.data.map { it[HOBBY_ID_1] ?: 0L }
    }

    fun getOnboardingData(): Flow<OnboardingDataEntity> {
        return dataStore.data.map { preferences ->
            Timber.e("@@@@@@@@@@@@@@@@@@@getOnboardingData : "+preferences[HOBBY_ID_1]+", "+preferences[HOBBY_INFO_1]+", "+preferences[HOBBY_1]+", "+preferences[HOBBY_TAKE_TIME]+", "+preferences[HOBBY_PURPOSE]+", "+preferences[HOBBY_PER_WEEK]+", "+preferences[HOBBY_PERIOD])
            OnboardingDataEntity(
                hobbyId = preferences[HOBBY_ID_1],
                hobbyInfoId = preferences[HOBBY_INFO_1],
                hobbyName = preferences[HOBBY_1],
                hobbyTimeMinutes = preferences[HOBBY_TAKE_TIME],
                hobbyPurpose = preferences[HOBBY_PURPOSE],
                executionCount = preferences[HOBBY_PER_WEEK],
                durationSet = preferences[HOBBY_PERIOD]
            )
        }
    }

    fun getAiRoutineList(): Flow<List<AiRoutineItemState>> {
        return dataStore.data.map { preferences ->
            val json = preferences[AI_ROUTINE_LIST] ?: return@map emptyList()
            try {
                Json.decodeFromString<List<AiRoutineItemState>>(json)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun removeOnboardingData() {
        dataStore.edit {
            it.remove(HOBBY_INFO_1)
            it.remove(HOBBY_1)
            it.remove(HOBBY_TAKE_TIME)
            it.remove(HOBBY_PURPOSE)
            it.remove(HOBBY_PER_WEEK)
            it.remove(HOBBY_PERIOD)
        }
    }

    suspend fun removeAccessToken() {
        dataStore.edit { it.remove(ACCESS_TOKEN) }
    }

    suspend fun removeRefreshToken() {
        dataStore.edit { it.remove(REFRESH_TOKEN) }
    }

    suspend fun clearTokensOnly() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
        }
    }

    suspend fun removeTokenAndLoginType() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(SOCIAL_TYPE)
        }
    }

    suspend fun removeUserInfo() {
        dataStore.edit {
            it.remove(IS_ONBOARDING_COMPLETED)
            it.remove(IS_NICKNAME_SET)
            it.remove(USER_NICKNAME)
            it.remove(KAKAO_USER_ID)
            it.remove(HOBBY_ID_1)
            it.remove(HOBBY_1)
            it.remove(HOBBY_TAKE_TIME)
            it.remove(HOBBY_PURPOSE)
            it.remove(HOBBY_PER_WEEK)
            it.remove(HOBBY_PERIOD)
            it.remove(HOBBY_ROUTINE_1)
            it.remove(HOBBY_ROUTINE_2)
            it.remove(HOBBY_ROUTINE_3)
            it.remove(HOBBY_INFO_1)
            it.remove(CREATED_HOBBY_ID)
        }
    }

    suspend fun removeGuestId() {
        dataStore.edit { it.remove(GUEST_USER_ID) }
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    }

    fun getSocialType(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[SOCIAL_TYPE]
        }
    }

    fun getIsOnboardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] == true
        }
    }

    fun getIsNicknameSet(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_NICKNAME_SET] == true
        }
    }

    fun getUserNickname(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_NICKNAME]
        }
    }

    suspend fun clearKakaoTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(SOCIAL_TYPE)
        }
    }

    suspend fun clearGuestTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(GUEST_USER_ID)
            it.remove(SOCIAL_TYPE)
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}