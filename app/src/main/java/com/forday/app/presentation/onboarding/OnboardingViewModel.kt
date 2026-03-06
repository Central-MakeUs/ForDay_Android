package com.forday.app.presentation.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.CreateHobbyUseCase
import com.forday.app.domain.usecase.GetAccessTokenUseCase
import com.forday.app.domain.usecase.GetHobbyDataUseCase
import com.forday.app.domain.usecase.GetIsNicknameDuplicateUseCase
import com.forday.app.domain.usecase.GetIsNicknameSetUseCase
import com.forday.app.domain.usecase.GetIsOnboardingCompletedUseCase
import com.forday.app.domain.usecase.GetOnboardingDataUseCase
import com.forday.app.domain.usecase.GuestLoginUseCase
import com.forday.app.domain.usecase.KakaoLoginUseCase
import com.forday.app.domain.usecase.ModifyHobbyDurationUseCase
import com.forday.app.domain.usecase.ModifyHobbyExecutionCountUseCase
import com.forday.app.domain.usecase.ModifyHobbyTimeUseCase
import com.forday.app.domain.usecase.RegisterNicknameUseCase
import com.forday.app.domain.usecase.SaveIsNicknameSetUseCase
import com.forday.app.domain.usecase.SaveIsOnboardingCompletedUseCase
import com.forday.app.domain.usecase.SaveNicknameUseCase
import com.forday.app.domain.usecase.SaveOnboardingDataUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.periodselect.JourneyMode
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.GetGuestUserIdUseCase
import com.forday.app.domain.usecase.GetHasSeenIntroUseCase
import com.forday.app.domain.usecase.GetHobbyCardDataAgainUseCase
import com.forday.app.domain.usecase.GetSocialTypeUseCase
import com.forday.app.domain.usecase.RecreateHobbyUseCase
import com.forday.app.domain.usecase.RemoveOnboardingDataUseCase
import com.forday.app.domain.usecase.SaveCreatedHobbyIdUseCase
import com.forday.app.domain.usecase.SaveHasSeenIntroUseCase
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.onboarding.swipeintro.navigation.SwipeIntro as SwipeIntroRoute

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getHobbyDataUseCase: GetHobbyDataUseCase,
    private val kakaoLoginUseCase: KakaoLoginUseCase,
    private val guestLoginUseCase: GuestLoginUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsOnboardingCompletedUseCase: GetIsOnboardingCompletedUseCase,
    private val getHobbyCardDataAgainUseCase: GetHobbyCardDataAgainUseCase,
    private val getIsNicknameSetUseCase: GetIsNicknameSetUseCase,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase,
    private val getIsNicknameDuplicateUseCase: GetIsNicknameDuplicateUseCase,
    private val registerNicknameUseCase: RegisterNicknameUseCase,
    private val recreateHobbyUseCase: RecreateHobbyUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase,
    private val saveOnboardingDataUseCase: SaveOnboardingDataUseCase,
    private val createHobbyUseCase: CreateHobbyUseCase,
    private val saveIsOnboardingCompletedUseCase: SaveIsOnboardingCompletedUseCase,
    private val saveIsNicknameSetUseCase: SaveIsNicknameSetUseCase,
    private val modifyHobbyTimeUseCase: ModifyHobbyTimeUseCase, // 취미 정보 수정 - 취미 시간
    private val modifyHobbyExecutionCountUseCase: ModifyHobbyExecutionCountUseCase, // 취미 정보 수정 - 취미 주당 횟수
    private val modifyHobbyDurationUseCase: ModifyHobbyDurationUseCase,
    private val saveCreatedHobbyIdUseCase: SaveCreatedHobbyIdUseCase,
    private val removeOnboardingDataUseCase: RemoveOnboardingDataUseCase,
    private val getHasSeenIntroUseCase: GetHasSeenIntroUseCase,
    private val saveHasSeenIntroUseCase: SaveHasSeenIntroUseCase,
    private val getSocialTypeUseCase: GetSocialTypeUseCase,
    private val getGuestUserIdUseCase: GetGuestUserIdUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<OnboardingSideEffect>() {

    private val _uiState: MutableStateFlow<OnboardingUiState> =
        MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.toStateIn()

    init {
        Timber.e("@@@@@@@@@@@@@ 호출1 " + uiState.value.isOnboardingCompleted + ", " + uiState.value.isNicknameSet + ", " + uiState.value.accessToken)
        viewModelScope.launch {
            autoGuestLoginIfNeeded()
            getUserData()
            determineInitialRoute()
        }
        viewModelScope.launch {
            getHasSeenIntroUseCase()
                .catch { }
                .collect { hasSeenIntro ->
                    _uiState.update { it.copy(hasSeenIntro = hasSeenIntro) }
                }
        }
        viewModelScope.launch {
            getSocialTypeUseCase()
                .catch { }
                .collect { socialType ->
                    _uiState.update { it.copy(socialType = socialType) }
                }
        }
    }

    fun onAction(action: OnboardingAction) = viewModelScope.launch {  //아직 사용중이지 않음
        when (action) {
            is OnboardingAction.OnClose -> Unit
        }
    }

    fun fetchHobbyData() = viewModelScope.launch {  // 취미 카드 데이터 불러오기
        _uiState.update { it.copy(isLoading = true) }
        flow {
            emit(getHobbyDataUseCase())
        }.httpCatch("fetchHobbyData") { errorData ->
            _uiState.update {
                it.copy(
                    errorData = errorData,
                    isLoading = false
                )
            }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    hobbies = data.hobbies.map { it.toPresentation() },
                    appVersion = data.appVersion,
                    isLoading = false
                )
            }
        }
    }

    // 커스텀 다이얼로그 표시
    fun showDialog() {
        _uiState.update { it.copy(showDialog = true) }
    }

    // 커스텀 다이얼로그 닫기 (취소)
    fun dismissDialog() {
        _uiState.update {
            it.copy(showDialog = false)
            // customHobbyText는 유지 (초기화하지 않음)
        }
    }

    // HobbyCard 선택 시
    fun saveHobbyInfo(hobbyId: Long?, hobbyName: String) {
        _uiState.update {
            it.copy(
                selectedHobbyId = hobbyId,
                customHobbyText = "",
                selectedHobbyName = hobbyName
            )
        }
    }

    // 커스텀 취미 확인 시
    fun confirmCustomHobby(text: String) {
        _uiState.update {
            it.copy(
                customHobbyText = text,
                showDialog = false,
                selectedHobbyId = 0,
                selectedHobbyName = text
            )
        }
    }

    fun savePurposes(purposes: Set<String>) {
        _uiState.update {
            it.copy(
                selectedPurpose = purposes.joinToString(","),  // Set을 콤마로 구분된 문자열로 변환
                customPurposeText = "" // 목적 카드 선택 시 커스텀 텍스트 초기화
            )
        }
    }

    // 커스텀 목적 확인 (기존 메서드)
    fun confirmCustomPurpose(text: String) {
        _uiState.update {
            it.copy(
                customPurposeText = text,
                selectedPurpose = "" // 커스텀 목적 입력 시 선택된 목적 초기화
            )
        }
    }

    //시간 선택
    fun saveTime(minutes: Int) {
        Timber.d("ViewModel selectTime called: $minutes")
        _uiState.update {
            it.copy(selectedMinutes = minutes)
        }
    }

    fun saveFrequency(frequency: Int) {
        _uiState.update {
            it.copy(selectedFrequency = frequency)
        }
    }

    private fun determineInitialRoute() =  //TODO 앱 실행 시 여정일 화면 진입했을 때 뒤로가기 안되는 문제 수정
        uiState
            .filter { it.accessToken != null || it.isOnboardingCompleted != null }
            .take(1)
            .onEach { state ->
                if (state.isSplashLoading) {
                    delay(2000)  // Splash 2초 todo 조건 걸기
                }
                // 2초 후 최신 상태로 라우팅 결정 (hasSeenIntro 포함)
                val freshState = _uiState.value
                val route = when {
                    freshState.accessToken == null && freshState.hasSeenIntro == false -> SwipeIntroRoute
                    freshState.accessToken == null -> Login
                    freshState.isOnboardingCompleted == false -> SelectHobby
                    freshState.isOnboardingCompleted == true && freshState.isNicknameSet == true -> Home
                    else -> {
                        getOnboardingData()
                        SelectPeriod(mode = ScreenMode.ONBOARDING)
                    }
                }

                _uiState.update {
                    it.copy(initialRoute = route as NavKey?, isSplashLoading = false)
                }
            }
            .catch {

            }
            .launchIn(viewModelScope)


    fun selectJourneyMode(mode: JourneyMode) {
        _uiState.update { it.copy(selectedJourneyMode = mode) }
    }

    private fun getUserData() = viewModelScope.launch {
        combine(
            getAccessTokenUseCase(),
            getIsOnboardingCompletedUseCase(),
            getIsNicknameSetUseCase(),
        ) { accessToken, isOnboardingCompleted, isNicknameSet ->
            Triple(
                accessToken,
                isOnboardingCompleted,
                isNicknameSet
            )
        }
            .catch { throwable ->
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }.collect { (accessToken, isOnboardingCompleted, isNicknameSet) ->
                Timber.e("@@@@@@@@isNicknameSet@@@" + isNicknameSet + ", " + isOnboardingCompleted)
                _uiState.update {
                    it.copy(
                        accessToken = accessToken,
                        isOnboardingCompleted = isOnboardingCompleted,
                        isNicknameSet = isNicknameSet
                    )
                }
            }
    }

    fun getOnboardingData() = viewModelScope.launch {
        getOnboardingDataUseCase()
            .catch { throwable ->
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
            .collect { onboardingData ->
                _uiState.update {
                    Timber.e("@@@@@@@@@@@@@@@@@@@ onboardingData : "+onboardingData)
                    it.copy(
                        hobbyId = onboardingData.hobbyId?.toInt(),
                        selectedHobbyId = onboardingData.hobbyInfoId?.toLong(),
                        selectedHobbyName = onboardingData.hobbyName,
                        selectedMinutes = onboardingData.hobbyTimeMinutes,
                        selectedPurpose = onboardingData.hobbyPurpose,
                        selectedFrequency = onboardingData.executionCount,
                        selectedJourneyMode = if (onboardingData.durationSet == true) JourneyMode.FORDAY_66 else JourneyMode.FREE
                    )
                }
            }
    }

    fun getIsNicknameDuplicate(nickName: String) = viewModelScope.launch {
        flow {
            emit(getIsNicknameDuplicateUseCase(nickName))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + throwable)
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { result ->
            Timber.e("@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + result.data.nickname + ", " + result.data.available + ", " + result.data.message)
            _uiState.update {
                it.copy(
                    selectedHobbyName = result.data.nickname,
                    nicknameCheckMessage = result.data.message,
                    isNicknameChecked = result.data.available
                )
            }
        }
    }

    fun getHobbyCardDataAgain() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        flow {
            emit(getHobbyCardDataAgainUseCase().toPresentation())
        }.catch { throwable ->
            _uiState.update { it.copy(isLoading = false) }
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            _uiState.update {
                it.copy(
                    hobbies = data.hobbies,
                    isLoading = false
                )
            }
        }
    }

    fun resetNicknameCheck() {
        _uiState.update {
            it.copy(
                nicknameCheckMessage = "",
                isNicknameChecked = false
            )
        }
    }

    fun registerNickname(nickName: String?) = viewModelScope.launch {
        flow {
            emit(registerNicknameUseCase(nickName))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            _uiState.update {
                it.copy(
                    nicknameRegisterSuccess = data.isSuccess
                )
            }
            nickName?.let { saveNicknameUseCase(it) }
        }
    }

    fun saveOnboardingData(  //온보딩 데이터 로컬 저장..?하는 메서드
        selectedHobbyId: Long?,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        selectedPeriod: JourneyMode?
    ) = viewModelScope.launch {
        Timber.e("@@@@@@@@@@ saveOnboardingData "+selectedHobbyId+", "+selectedHobbyName+", "+selectedMinutes+", "+selectedPurpose+", "+selectedFrequency+", "+selectedPeriod)
        runCatching {
            saveOnboardingDataUseCase(
                selectedHobbyId,
                selectedHobbyName,
                selectedMinutes,
                selectedPurpose,
                selectedFrequency,
                selectedPeriod == JourneyMode.FORDAY_66
            )
        }.onFailure { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }
    }

    fun createHobby(  // 여정일 쪽에서 저장
        selectedHobbyId: Long? = null,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        selectedPeriod: JourneyMode?
    ) = viewModelScope.launch {
        flow {
            emit(
                createHobbyUseCase(
                    selectedHobbyId,
                    selectedHobbyName,
                    selectedMinutes,
                    selectedPurpose,
                    selectedFrequency,
                    selectedPeriod == JourneyMode.FORDAY_66
                )
            )
        }.httpCatch("createHobby") { errorData ->
            Timber.e("1@@@@@@@@@@@@@@@@"+errorData.errorClassName)
            if (errorData.errorClassName == "DUPLICATE_HOBBY_REQUEST") {
                Timber.e("2@@@@@@@@@@@@@@@@"+errorData.errorClassName)
                handleDuplicateHobby(
                    selectedHobbyId, selectedHobbyName, selectedMinutes,
                    selectedPurpose, selectedFrequency, selectedPeriod
                )
            } else {
                snackbarManager.show(errorData.message)
            }
        }.collect { result ->
            _uiState.update {
                it.copy(
                    isOnboardingDataSaved = result.isSuccess,
                    hobbyId = result.data.hobbyId
                )
            }
            if (result.isSuccess == true) {
                saveCreatedHobbyIdUseCase(result.data.hobbyId.toLong())
                saveOnboardingDataUseCase(
                    selectedHobbyId,
                    selectedHobbyName,
                    selectedMinutes,
                    selectedPurpose,
                    selectedFrequency,
                    selectedPeriod == JourneyMode.FORDAY_66
                )
            }
        }
    }

    fun resetOnboardingState() = viewModelScope.launch {
        // 1. DataStore 초기화
        runCatching {
            removeOnboardingDataUseCase()
        }.onFailure { throwable ->
            Timber.e("removeOnboardingData error: $throwable")
        }

        // 2. UiState 온보딩 관련 필드 초기화
        _uiState.update {
            it.copy(
                hobbyId = null,
                selectedHobbyId = null,
                selectedHobbyName = "",
                customHobbyText = "",
                selectedMinutes = null,
                selectedPurpose = "",
                customPurposeText = "",
                selectedFrequency = null,
                selectedJourneyMode = null,
                isOnboardingDataSaved = false,
                isHobbyRecreated = false
            )
        }
    }

    private suspend fun autoGuestLoginIfNeeded() {
        runCatching {
            val accessToken = getAccessTokenUseCase().first()
            val socialType = getSocialTypeUseCase().first()
            val guestUserId = getGuestUserIdUseCase().first()
            if (accessToken == null && socialType == "GUEST" && guestUserId != null) {
                Timber.d("autoGuestLoginIfNeeded: auto re-login as guest (guestUserId=$guestUserId)")
                guestLoginUseCase()
            }
        }.onFailure { throwable ->
            Timber.e("autoGuestLoginIfNeeded error: $throwable")
        }
    }

    fun guestAutoReLogin() = viewModelScope.launch {
        guestLoginUseCase()
            .onSuccess {
                _sideEffectChannel.send(OnboardingSideEffect.GuestAutoReLoginSuccess)
            }
            .onFailure {
                _sideEffectChannel.send(OnboardingSideEffect.GuestAutoReLoginFailure)
            }
    }

    fun resetForNewSession() = viewModelScope.launch {
        // 1. DataStore 온보딩 데이터 초기화
        runCatching {
            removeOnboardingDataUseCase()
        }.onFailure { throwable ->
            Timber.e("removeOnboardingData error: $throwable")
        }

        // 2. 온보딩 선택값 + 로그인 세션 관련 필드 초기화
        _uiState.update {
            it.copy(
                hobbyId = null,
                selectedHobbyId = null,
                selectedHobbyName = "",
                customHobbyText = "",
                selectedMinutes = null,
                selectedPurpose = "",
                customPurposeText = "",
                selectedFrequency = null,
                selectedJourneyMode = null,
                isOnboardingDataSaved = false,
                isHobbyRecreated = false,
                isLoginSuccess = false,
                isNewUser = null,
                nicknameCheckMessage = "",
                isNicknameChecked = false,
                isNicknameAvailable = false,
                nicknameRegisterSuccess = false,
                errorData = null,
                error = ""
            )
        }
    }

    private suspend fun handleDuplicateHobby(
        selectedHobbyId: Long?,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        selectedPeriod: JourneyMode?
    ) {
        Timber.e("3@@@@@@@@@@@@@@@@ handleDuplicateHobby")
        try {
            val onboardingData = getOnboardingDataUseCase().first()
            val hobbyId = onboardingData.hobbyId?.toLong()

            val result = recreateHobbyUseCase(
                hobbyId = hobbyId,
                hobbyInfoId = selectedHobbyId,
                hobbyName = selectedHobbyName,
                hobbyPurpose = selectedPurpose,
                hobbyTimeMinutes = selectedMinutes,
                executionCount = selectedFrequency,
                durationSet = selectedPeriod == JourneyMode.FORDAY_66
            )

            val isSuccess = result.status == 200 && result.isSuccess
            if (isSuccess) {
                saveCreatedHobbyIdUseCase(result.data.hobbyId)
                saveOnboardingDataUseCase(
                    selectedHobbyId,
                    selectedHobbyName,
                    selectedMinutes,
                    selectedPurpose,
                    selectedFrequency,
                    selectedPeriod == JourneyMode.FORDAY_66
                )
                _uiState.update {
                    it.copy(isOnboardingDataSaved = true, isHobbyRecreated = true)
                }
            } else {
                snackbarManager.show("취미 수정에 실패했어요. 잠시 후 다시 시도해주세요.")
                _uiState.update { it.copy(isHobbyRecreated = false) }
            }
        } catch (e: Exception) {
            Timber.e("handleDuplicateHobby error: $e")
            _uiState.update { it.copy(isHobbyRecreated = false) }
            snackbarManager.show(e.toUserMessage(UserMessageCategory.AUTH))
        }
    }

    fun recreateHobby(
        hobbyId: Long?,
        hobbyInfoId: Long?,
        hobbyName: String?,
        hobbyPurpose: String?,
        hobbyTimeMinutes: Int?,
        executionCount: Int?,
        durationSet: Boolean?
    ) = viewModelScope.launch {
        flow {
            emit(
                recreateHobbyUseCase(
                    hobbyId = hobbyId,
                    hobbyInfoId = hobbyInfoId,
                    hobbyName = hobbyName,
                    hobbyPurpose = hobbyPurpose,
                    hobbyTimeMinutes = hobbyTimeMinutes,
                    executionCount = executionCount,
                    durationSet = durationSet
                )
            )
        }.catch { throwable ->
            _uiState.update { it.copy(isHobbyRecreated = false) }
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { result ->
            val isSuccess = result.status == 200 && result.isSuccess
            if (!isSuccess) {
                snackbarManager.show("취미 수정에 실패했어요. 잠시 후 다시 시도해주세요.")
            }
            _uiState.update { it.copy(isHobbyRecreated = isSuccess) }
        }
    }

    fun loginWithGuest() = viewModelScope.launch {
        Log.d("@#@#@#@ ", "######## loginWithGuest")
        Timber.e("@#@#@#@#@#@#@#@#@ ")
        _uiState.update { it.copy(isLoading = true, error = "") }
        try {
            guestLoginUseCase()
                .onSuccess { data ->
                    Timber.d("guestLogin success: isNewUser=${data.isNewUser}, onboardingCompleted=${data.onboardingCompleted}, nicknameSet=${data.nicknameSet}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNewUser = data.isNewUser,
                            isOnboardingCompleted = data.onboardingCompleted,
                            isNicknameSet = data.nicknameSet,
                            isLoginSuccess = true
                        )
                    }
                }
                .onFailure { error ->
                    Log.d("@#@#@#@ ", "########error "+error)
                    Timber.e("@#@#@#@#@#@#@#@#@ error "+error)
                    Timber.d("error " + error.message)
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = false,  // 로그인 실패
                            error = errorMessage
                        )
                    }
                    snackbarManager.show(errorMessage)
                }
        } catch (e: Exception) {

            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            Log.d("@#@#@#@ ", "########Exception "+e)
            Timber.e("@#@#@#@#@#@#@#@#@ errorMessage "+errorMessage)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoginSuccess = false,  // 로그인 실패
                    error = errorMessage
                )
            }
            snackbarManager.show(errorMessage)
        }
    }


    fun loginWithKakao(context: Context) {
        val kakao = UserApiClient.instance
        Log.e(
            "OnboardingViewModel",
            "isKakaoTalkLoginAvailable: ${kakao.isKakaoTalkLoginAvailable(context)}"
        )

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            Log.e("OnboardingViewModel", "========== Kakao Account 콜백 ==========")
            if (error != null || token == null) {
                Log.e("OnboardingViewModel", "카카오 계정 로그인 실패")
                Log.e("OnboardingViewModel", "Error type: ${error?.javaClass?.simpleName}")
                Log.e("OnboardingViewModel", "Error message: ${error?.message}")
                if (error != null) {
                    Log.e("OnboardingViewModel", "Error stacktrace:", error)
                }

                val errorMessage = error?.toUserMessage(UserMessageCategory.AUTH)
                    ?: "로그인에 실패했어요. 잠시 후 다시 시도해주세요."
                Log.e("OnboardingViewModel", "User error message: $errorMessage")
                snackbarManager.show(errorMessage)
            } else {
                Log.e("OnboardingViewModel", "카카오 계정 로그인 성공")
                Log.e("OnboardingViewModel", "accessToken: ${token.accessToken}")
                loginIntoApp(token.accessToken)
            }
        }

        if (kakao.isKakaoTalkLoginAvailable(context)) {
            Log.e("OnboardingViewModel", "========== 카카오톡으로 로그인 시도 ==========")
            kakao.loginWithKakaoTalk(context) { token, error ->
                Log.e("OnboardingViewModel", "========== KakaoTalk 콜백 ==========")
                if (error != null || token == null) {
                    Log.e("OnboardingViewModel", "카카오톡 로그인 실패")
                    Log.e("OnboardingViewModel", "Error type: ${error?.javaClass?.simpleName}")
                    Log.e("OnboardingViewModel", "Error message: ${error?.message}")

                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.e("OnboardingViewModel", "사용자가 카카오톡 로그인 취소")
                        snackbarManager.show(error.toUserMessage(UserMessageCategory.AUTH))
                        return@loginWithKakaoTalk
                    }

                    Log.e("OnboardingViewModel", "========== 카카오 계정으로 로그인 재시도 ==========")
                    if (error != null) {
                        Log.e("OnboardingViewModel", "Error stacktrace:", error)
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else {
                    Log.e("OnboardingViewModel", "카카오톡 로그인 성공")
                    Log.e("OnboardingViewModel", "accessToken: ${token.accessToken}")
                    loginIntoApp(token.accessToken)
                }
            }
        } else {
            Log.e("OnboardingViewModel", "========== 카카오 계정으로 로그인 시도 ==========")
            kakao.loginWithKakaoAccount(context, callback = callback)
        }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    fun modifyHobbyTime(hobbyId: Long?, minutes: Int) = viewModelScope.launch {
        flow {
            emit(modifyHobbyTimeUseCase(hobbyId, minutes))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@modifyHobbyTime@@@@@@@" + throwable)
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@modifyHobbyTime@@@@@@@" + data)
            if (data.status != 200) {
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun modifyHobbyExecutionCount(hobbyId: Long?, executionCount: Int) = viewModelScope.launch {
        flow {
            emit(modifyHobbyExecutionCountUseCase(hobbyId, executionCount))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@modifyHobbyExecutionCount@@@@@@@" + throwable)
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@modifyHobbyExecutionCount@@@@@@@" + data)
            if (data.status != 200) {
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun modifyHobbyGoalDays(hobbyId: Long?, goalDays: Boolean) = viewModelScope.launch {
        flow {
            emit(modifyHobbyDurationUseCase(hobbyId, goalDays))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@modifyHobbyGoalDays@@@@@@@" + throwable)
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@modifyHobbyExecutionCount@@@@@@@" + data)
            if (data.status != 200) {
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun saveIsNicknameSet(isNicknameSet: Boolean) = viewModelScope.launch {
        runCatching {
            saveIsNicknameSetUseCase(isNicknameSet)
        }.onFailure { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }
    }

    fun saveIsOnboardingCompleted(isOnboardingCompleted: Boolean) = viewModelScope.launch {
        runCatching {
            saveIsOnboardingCompletedUseCase(isOnboardingCompleted)
        }.onFailure { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }
    }

    fun saveHasSeenIntro() = viewModelScope.launch {
        runCatching {
            saveHasSeenIntroUseCase(true)
        }
    }

    private fun loginIntoApp(kakaoAccessToken: String) = viewModelScope.launch {
        try {
            Log.e("OnboardingViewModel", "========== 카카오 로그인 시작 ==========")
            Log.e("OnboardingViewModel", "kakaoAccessToken: $kakaoAccessToken")

            kakaoLoginUseCase(kakaoAccessToken)
                .onSuccess { data ->
                    Log.e("OnboardingViewModel", "========== 카카오 로그인 성공 ==========")
                    Log.e("OnboardingViewModel", "Response data: $data")
                    Log.e("OnboardingViewModel", "isNewUser: ${data.data.isNewUser}")

                    val onboardingData = data.data.onboardingData
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNewUser = data.data.isNewUser,
                            isOnboardingCompleted = data.data.isOnboardingCompleted,
                            isNicknameSet = data.data.isNicknameSet,
                            isLoginSuccess = true,
                            hobbyId = onboardingData?.id,
                            selectedHobbyId = onboardingData?.hobbyCardId?.toLong(),
                            selectedHobbyName = onboardingData?.hobbyName,
                            selectedMinutes = onboardingData?.hobbyTimeMinutes,
                            selectedPurpose = onboardingData?.hobbyPurpose,
                            selectedFrequency = onboardingData?.executionCount,
                            selectedJourneyMode = onboardingData?.let { od ->
                                if (od.isDurationSet) JourneyMode.FORDAY_66 else JourneyMode.FREE
                            }
                        )
                    }
                    Log.e("OnboardingViewModel", "UI State 업데이트 완료 - isLoginSuccess: true")
                }
                .onFailure { error ->
                    Log.e("OnboardingViewModel", "========== 카카오 로그인 실패 (onFailure) ==========")
                    Log.e("OnboardingViewModel", "Error type: ${error.javaClass.name}")
                    Log.e("OnboardingViewModel", "Error message: ${error.cause}")
                    Log.e("OnboardingViewModel", "Error stacktrace:", error)

                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    Log.e("OnboardingViewModel", "User error message: $errorMessage")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = false
                        )
                    }
                    Log.e("OnboardingViewModel", "UI State 업데이트 완료 - isLoginSuccess: false")
                    snackbarManager.show(errorMessage)
                }
        } catch (e: Exception) {
            Log.e("OnboardingViewModel", "========== 카카오 로그인 실패 (Exception) ==========")
            Log.e("OnboardingViewModel", "Exception type: ${e.javaClass.simpleName}")
            Log.e("OnboardingViewModel", "Exception message: ${e.message}")
            Log.e("OnboardingViewModel", "Exception stacktrace:", e)

            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            Log.e("OnboardingViewModel", "User error message: $errorMessage")

            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = errorMessage,
                    isLoginSuccess = false
                )
            }
            Log.e("OnboardingViewModel", "UI State 업데이트 완료 - error: $errorMessage")
            snackbarManager.show(errorMessage)
        }
    }

    private suspend fun logoutFromKakao(): Boolean =
        suspendCoroutine { continuation ->
            // 로그아웃
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("TAG", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    continuation.resume(false)
                } else {
                    Log.i("TAG", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    continuation.resume(true)
                }
            }
        }

}