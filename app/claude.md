# ForDay Android Project

## 프로젝트 개요

ForDay는 취미 활동 기록 및 관리 앱입니다.

- **프로젝트명**: ForDay
- **패키지명**: `com.forday.app` / `com.app.forday`
- **아키텍처**: Clean Architecture (Presentation - Domain - Data - Remote)
- **언어**: Kotlin
- **UI**: Jetpack Compose + Material3
- **DI**: Hilt
- **비동기**: Coroutines + Flow
- **네트워크**: Retrofit + OkHttp + Kotlinx Serialization
- **이미지**: Coil
- **내비게이션**: Navigation3 (Type-safe)
- **로컬 저장소**: DataStore
- **최소 SDK**: 28 (Android 9.0)
- **타겟 SDK**: 36
- **Java 버전**: 17

---

## 프로젝트 구조

### 디렉토리 구조

```
app/src/main/java/com/forday/app/
├── FordayApplication.kt
│
├── core/                          # 공통 모듈
│   ├── datastore/                 # DataStore (로컬 저장소)
│   │   ├── UserLocalDataSource.kt
│   │   ├── di/DataStoreModule.kt
│   │   └── model/
│   ├── designsystem/              # 디자인 시스템
│   │   ├── component/             # 재사용 가능한 UI 컴포넌트
│   │   │   ├── button/
│   │   │   ├── textfield/
│   │   │   ├── dropdown/
│   │   │   ├── layout/OnboardingLayout.kt
│   │   │   ├── topbar/TopAppBar.kt
│   │   │   └── ...
│   │   ├── dialog/
│   │   ├── theme/                 # 색상, 타이포그래피
│   │   └── toast/
│   ├── logger/                    # Firebase Analytics, Crashlytics
│   ├── navigation/Route.kt        # NavKey 인터페이스
│   └── util/
│
├── presentation/                  # UI Layer
│   ├── BaseViewModel.kt           # 공통 ViewModel
│   │
│   ├── onboarding/                # 온보딩 플로우
│   │   ├── OnboardingViewModel.kt
│   │   ├── OnboardingUiState.kt
│   │   ├── OnboardingSideEffect.kt
│   │   ├── splash/
│   │   ├── login/
│   │   ├── hobbyselect/
│   │   ├── timeselect/
│   │   ├── purposeselect/
│   │   ├── frequencyselect/
│   │   ├── periodselect/
│   │   ├── nicknameinput/
│   │   └── showpobbies/
│   │
│   ├── home/                      # 홈 화면
│   │   ├── HomeViewModel.kt
│   │   ├── HomeScreen.kt
│   │   ├── HomeSideEffect.kt
│   │   ├── model/HomeMapper.kt
│   │   └── navigation/Home.kt
│   │
│   ├── record/                    # 활동 기록
│   │   ├── RecordRoutineViewModel.kt
│   │   ├── RecordRoutineUiState.kt
│   │   ├── RecordMapper.kt
│   │   └── screen/RecordRoutineScreen.kt
│   │
│   ├── modifyhobby/               # 취미 관리
│   │   ├── ModifyHobbyViewModel.kt
│   │   ├── ModifyHobbyUiState.kt
│   │   └── screen/ModifyHobbyScreen.kt
│   │
│   ├── mypage/                    # 마이페이지
│   │   ├── MyPageViewModel.kt
│   │   ├── routinedetail/
│   │   └── ...
│   │
│   └── main/                      # 메인 네비게이션
│       ├── MainActivity.kt
│       ├── AppEntryPoint.kt
│       ├── MainFlow.kt
│       ├── MainNavigator.kt
│       └── MainNavigationState.kt
│
├── domain/                        # Domain Layer
│   ├── model/                     # Domain Models
│   │   ├── *Domain.kt
│   │   └── DomainEntity.kt
│   ├── repository/                # Repository Interfaces
│   │   ├── AuthRepository.kt
│   │   ├── HobbyRepository.kt
│   │   └── ...
│   └── usecase/                   # Use Cases (비즈니스 로직)
│       ├── CreateHobbyUseCase.kt
│       ├── GetHobbyDataUseCase.kt
│       └── ...
│
├── data/                          # Data Layer
│   ├── DataMapper.kt              # Entity → Domain Mapper
│   ├── di/
│   │   ├── RepositoryModule.kt
│   │   └── TokenModule.kt
│   ├── impl/                      # Repository 구현체
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── HobbyRepositoryImpl.kt
│   │   └── ...
│   ├── model/                     # Data Entities
│   │   ├── *Entity.kt
│   │   └── DataLayerEntity.kt
│   └── remote/                    # DataSource Interfaces
│       ├── AuthDataSource.kt
│       └── ...
│
└── remote/                        # Remote Layer (Network)
    ├── RemoteMapper.kt            # Response → Entity Mapper
    ├── api/
    │   ├── service/               # Retrofit API Interfaces
    │   │   ├── AuthApi.kt
    │   │   ├── HobbyApi.kt
    │   │   └── ...
    │   └── interceptor/
    │       ├── TokenInterceptor.kt
    │       └── TokenProvider.kt
    ├── di/
    │   ├── NetworkModule.kt
    │   └── RemoteDataSourceModule.kt
    ├── impl/                      # DataSource 구현체
    │   ├── AuthDataSourceImpl.kt
    │   └── ...
    └── model/
        ├── request/               # API Request Models
        └── response/              # API Response Models
```

---

## Clean Architecture 레이어

### 데이터 흐름

```
User Action (UI)
    ↓
[Presentation] Screen → ViewModel → UiState
    ↓
[Domain] UseCase → Repository Interface → Domain Model
    ↓
[Data] Repository Impl → DataSource → Entity
    ↓
[Remote] API Service → Request/Response
    ↓
Server
```

### Mapper 체인

```
API Response (JSON)
    ↓ RemoteMapper.toData()
Entity (data/model/)
    ↓ DataMapper.toDomain()
Domain Model (domain/model/)
    ↓ Presentation Mapper (.toPresentation())
UI Model (presentation/{feature}/model/)
```

---

## 코딩 규칙

### 1. ViewModel 패턴

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val useCase1: UseCase1,
    private val useCase2: UseCase2
) : BaseViewModel<MySideEffect>() {
    
    // UiState (MutableStateFlow → StateFlow)
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.toStateIn()
    
    // Init block
    init {
        fetchInitialData()
    }
    
    // 비즈니스 로직 메서드
    fun handleAction(action: MyAction) {
        _uiState.update { it.copy(isLoading = true) }
    }
    
    // API 호출 패턴
    fun fetchData() = viewModelScope.launch {
        flow {
            emit(useCase())
        }.catch { throwable ->
            _sideEffectChannel.send(MySideEffect.Exception(throwable))
        }.collect { result ->
            _uiState.update { it.copy(data = result) }
        }
    }
}
```

### 2. UiState 패턴

```kotlin
// 모든 필드에 기본값 제공
data class MyUiState(
    val data: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
    val selectedId: Long? = null,
    // Nullable 타입은 null이 의미있을 때만 사용
)
```

### 3. SideEffect 패턴

```kotlin
// Sealed Interface로 정의
sealed interface MySideEffect {
    data class DomainError(val message: String) : MySideEffect
    data class Exception(val throwable: Throwable) : MySideEffect
}
```

### 4. Screen 구조 (Root / Screen 분리)

```kotlin
// ScreenRoot: ViewModel 연결 + 상태 수집
@Composable
fun MyScreenRoot(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: MyViewModel = hiltViewModel()
) {
    viewModel.logEvent("my_screen")
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    // SideEffect 처리
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is MySideEffect.DomainError -> {
                    // Toast 표시
                }
                is MySideEffect.NavigateToNext -> onNext()
            }
        }
    }
    
    MyScreen(
        data = state.data,
        isLoading = state.isLoading,
        onAction = { viewModel.handleAction(it) },
        onNext = onNext,
        onBack = onBack
    )
}

// Screen: Pure Composable (ViewModel 없음, Preview 가능)
@Composable
fun MyScreen(
    data: List<Item>,
    isLoading: Boolean,
    onAction: (MyAction) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    // UI only - No ViewModel reference
}
```

### 5. Navigation (Type-safe)

```kotlin
// NavKey 정의
@Serializable
data class MyScreen(
    val params: MyParams? = null,
    val mode: ScreenMode = ScreenMode.ONBOARDING
) : NavKey

// 파라미터 전달용 data class
@Serializable
data class MyParams(
    val id: Int,
    val name: String
)

// EntryProvider에서 사용
entry<MyScreen> { backStackEntry ->
    MyScreenRoot(
        params = backStackEntry.params,
        mode = backStackEntry.mode,
        onNext = { navigator.navigate(NextScreen) },
        onBack = { navigator.goBack() }
    )
}
```

### 6. Mapper 구현

#### RemoteMapper (Response → Entity)

```kotlin
data class MyResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("items") val items: List<ItemResponse>? = emptyList()
) : RemoteMapper<MyEntity> {
    override fun toData(): MyEntity = MyEntity(
        id = id ?: 0,                          // Null 제거
        name = name ?: "",
        items = items?.map { it.toData() } ?: emptyList()
    )
}
```

#### DataMapper (Entity → Domain)

```kotlin
data class MyEntity(
    val id: Int,
    val name: String,
    val items: List<ItemEntity>
) : DataMapper<MyDomain> {
    override fun toDomain(): MyDomain = MyDomain(
        id = id,
        name = name,
        items = items.map { it.toDomain() }
    )
}
```

#### Presentation Mapper (Domain → UI)

```kotlin
// 확장 함수로 정의
fun MyDomain.toPresentation(): MyUiModel = MyUiModel(
    id = id,
    displayName = name,
    imageResId = getDrawableResId(imageCode)  // String → @DrawableRes
)
```

### 7. ScreenMode 패턴

```kotlin
enum class ScreenMode {
    ONBOARDING,  // 온보딩: 선택 즉시 ViewModel 저장
    DEFAULT      // 수정 모드: 로컬 상태 관리 → onNext에서만 저장
}

// 사용 예시
@Composable
fun MyScreenRoot(
    params: MyParams?,
    mode: ScreenMode,
    viewModel: MyViewModel
) {
    // 로컬 상태 (DEFAULT 모드용)
    val localValue = remember(params, viewModel.value) {
        mutableStateOf(
            if (mode == ScreenMode.DEFAULT) {
                params?.value  // 초기값 (params 기반)
            } else {
                null  // ONBOARDING은 null로 시작
            }
        )
    }
    
    // 현재 값 결정
    val currentValue = if (mode == ScreenMode.DEFAULT) {
        localValue.value
    } else {
        viewModel.value  // ONBOARDING은 ViewModel 값 사용
    }
    
    MyScreen(
        value = currentValue,
        onValueChange = { newValue ->
            if (mode == ScreenMode.DEFAULT) {
                localValue.value = newValue  // 로컬만 업데이트
            } else {
                viewModel.updateValue(newValue)  // ViewModel 즉시 저장
            }
        },
        onNext = { finalValue ->
            if (mode == ScreenMode.DEFAULT) {
                viewModel.updateValue(finalValue)  // onNext에서만 저장
            }
            onNext()
        }
    )
}
```

---

## 주요 컴포넌트

### OnboardingLayout (공통 레이아웃)

```kotlin
@Composable
fun OnboardingLayout(
    title: String,
    currentStep: Int,
    totalSteps: Int = 5,
    mode: ScreenMode = ScreenMode.ONBOARDING,
    onBack: () -> Unit = {},
    content: @Composable () -> Unit  // Slot API
)
```

**사용 예시:**
```kotlin
OnboardingLayout(
    title = "취미 선택",
    currentStep = 1,
    totalSteps = 5,
    onBack = onBack
) {
    // 화면 고유 콘텐츠
}
```

### BottomNextButton

```kotlin
@Composable
fun BottomNextButton(
    text: String = "다음",
    state: BottomButtonState,  // ENABLED, DISABLED
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

### Design System Colors

```kotlin
// 사용 방법
ForDayTheme.color.Primary001     // #FF9447 (주황)
ForDayTheme.color.Neutral900     // #1E1E1E (검정)
ForDayTheme.color.Neutral600     // #7A7A7A (회색)
ForDayTheme.color.Gray03         // Border
```

---

## 주요 기능

### 1. 온보딩 플로우 (7단계)

```
1. SplashScreen → 초기 라우팅 결정
2. LoginScreen → 카카오/게스트 로그인
3. SelectHobbyScreen → 취미 선택 (1/5)
4. SelectTimeScreen → 시간 선택 (2/5)
5. SelectPurposeScreen → 목적 선택 (3/5)
6. SelectFrequencyScreen → 주간 횟수 선택 (4/5)
7. SelectJourneyDaysScreen → 여정일 선택 (5/5)
8. OnboardingSuccessScreen → 완료 화면
9. InputNicknameScreen → 닉네임 입력
10. Home → 홈 화면
```

**초기 라우팅 로직:**
```kotlin
val route = when {
    state.accessToken == null -> Login
    state.isOnboardingCompleted == false -> SelectHobby
    state.isOnboardingCompleted == true && state.isNicknameSet == true -> Home
    else -> SelectPeriod(mode = ScreenMode.ONBOARDING)
}
```

### 2. 취미 관리

- **최대 2개** 취미 선택 가능
- **탭**: 진행중 / 보관함
- **수정 가능**: 시간, 주간 횟수, 여정일
- **보관/꺼내기**: 상태 변경

### 3. 활동 기록

- **루틴 선택**: 직접 입력 or AI 추천
- **스티커 선택** (필수)
- **메모 작성** (선택)
- **이미지 업로드**: 최대 3장 (S3)
- **공개범위 설정**: 전체공개 / 비공개

### 4. 이미지 업로드 플로우

```
1. 갤러리에서 이미지 선택
2. Presigned URL 요청 (GetPresignedUrlUseCase)
3. S3에 이미지 업로드 (UploadImageToS3UseCase)
4. 업로드 완료 → 체크마크 표시
5. 기록 생성 시 이미지 URL 전달
```

---

## API 구조

### UseCase 패턴

```kotlin
class MyUseCase @Inject constructor(
    private val repository: MyRepository
) {
    suspend operator fun invoke(params: MyParams): MyDomain {
        return repository.getData(params)
    }
}
```

### Repository 패턴

```kotlin
// Interface (domain/repository/)
interface MyRepository {
    suspend fun getData(params: MyParams): MyDomain
}

// Implementation (data/impl/)
class MyRepositoryImpl @Inject constructor(
    private val dataSource: MyDataSource
) : MyRepository {
    override suspend fun getData(params: MyParams): MyDomain {
        return dataSource.fetchData(params)
            .toData()      // Response → Entity
            .toDomain()    // Entity → Domain
    }
}
```

### API Service (Retrofit)

```kotlin
interface MyApi {
    @GET("api/endpoint")
    suspend fun getData(
        @Query("param") param: String
    ): MyResponse
    
    @POST("api/create")
    suspend fun createData(
        @Body request: MyRequest
    ): MyResponse
}
```

---

## 네비게이션 구조

### Navigator 클래스

```kotlin
class Navigator(val state: MainNavigationState) {
    fun navigate(route: NavKey)        // 화면 이동
    fun goBack()                        // 뒤로가기
    fun replaceWith(route: NavKey)      // 현재 화면 교체
}
```

### Top-level Destinations (Bottom Bar 탭)

```kotlin
val TOP_LEVEL_DESTINATIONS = mapOf(
    Home to BottomNavItem(...),
    Discovery to BottomNavItem(...),
    Story to BottomNavItem(...),
    MyPage to BottomNavItem(...)
)
```

### EntryProvider 패턴

```kotlin
val entryProvider = entryProvider<NavKey> {
    entry<MyScreen> { backStackEntry ->
        MyScreenRoot(
            params = backStackEntry.params,
            onNext = { navigator.navigate(NextScreen) },
            onBack = { navigator.goBack() }
        )
    }
}
```

---

## 데이터 저장 (DataStore)

### 저장 항목

```kotlin
// UserLocalDataSource.kt
- accessToken: String?
- refreshToken: String?
- isOnboardingCompleted: Boolean
- isNicknameSet: Boolean
- onboardingData: OnboardingDataEntity
```

### 사용 예시

```kotlin
// ViewModel
fun saveOnboardingData(...) = viewModelScope.launch {
    saveOnboardingDataUseCase(
        hobbyId, hobbyName, minutes, purpose, frequency, durationSet
    )
}
```

---

## Firebase 통합

### Analytics 로깅

```kotlin
// ViewModel에서
viewModel.logEvent("screen_name")
viewModel.logEvent("action_${parameter}")

// 예시
viewModel.logEvent("select_hobby_screen")
viewModel.logEvent("selected_hobby_card_독서")
```

### Crashlytics

```kotlin
// BaseViewModel에서 자동 처리
.catch { throwable ->
    _sideEffectChannel.send(SideEffect.Exception(throwable))
    // Crashlytics에 자동 리포트
}
```

---

## 테스트

### Unit Test

```kotlin
@Test
fun `취미 선택 시 UiState 업데이트 확인`() = runTest {
    // Given
    val viewModel = OnboardingViewModel(...)
    
    // When
    viewModel.saveHobbyInfo(1L, "독서")
    
    // Then
    assertEquals(1L, viewModel.uiState.value.selectedHobbyId)
    assertEquals("독서", viewModel.uiState.value.selectedHobbyName)
}
```

### Screenshot Test (Roborazzi)

```kotlin
@Test
fun selectHobbyScreenSnapshot() {
    composeTestRule.setContent {
        SelectHobbyScreen(
            hobbies = sampleHobbies,
            selectedHobbyId = null,
            // ...
        )
    }
    
    composeTestRule.onRoot().captureRoboImage()
}
```

---

## 체크리스트

### UI 작성 시
- [ ] ScreenRoot / Screen 분리 적용
- [ ] ScreenMode 분기 처리 (ONBOARDING/DEFAULT)
- [ ] Preview 함수 작성
- [ ] 모든 파라미터에 기본값 제공
- [ ] OnboardingLayout 사용 (온보딩 화면)
- [ ] BottomNextButton 상태 관리

### ViewModel 작성 시
- [ ] `@HiltViewModel` + `@Inject` 추가
- [ ] `BaseViewModel<SideEffect>` 상속
- [ ] UiState는 data class + 기본값
- [ ] StateFlow로 상태 노출 (`toStateIn()`)
- [ ] API 호출 시 `flow { }.catch { }.collect { }` 패턴
- [ ] 로깅 이벤트 호출 (`logEvent()`)

### API 연동 시
- [ ] Response → Entity → Domain 매핑 체인
- [ ] Nullable 필드 처리 (Elvis 연산자)
- [ ] 리스트 매핑 (`?.map { it.toData() } ?: emptyList()`)
- [ ] 에러 핸들링 (`catch` 블록)

### Navigation 시
- [ ] `@Serializable` 추가
- [ ] 복잡한 파라미터는 data class로 정의
- [ ] EntryProvider에 entry 추가
- [ ] Navigator로 화면 전환

### DataStore 사용 시
- [ ] UseCase 패턴 사용
- [ ] Flow로 데이터 수집
- [ ] ViewModel에서 구독

---

## 주의사항

### 1. Null Safety
- Response: nullable + 기본값
- Entity/Domain: non-null
- UI: nullable은 의미있을 때만

### 2. 상태 관리
- ONBOARDING: ViewModel 즉시 저장
- DEFAULT: 로컬 상태 → onNext에서만 저장
- onBack 시 로컬 상태는 초기값으로 복원

### 3. 컴포넌트 분리
- Screen: 전체 화면 레이아웃
- Section: 화면의 의미있는 섹션
- Component: 재사용 가능한 단위
- Atom: 최소 단위 (Button, Icon 등)

### 4. 네이밍 컨벤션
- ViewModel: `{Feature}ViewModel`
- UiState: `{Feature}UiState`
- SideEffect: `{Feature}SideEffect`
- Screen: `{Feature}Screen` + `{Feature}ScreenRoot`
- Navigation: `{Feature}` (data class)
- UseCase: `{Verb}{Feature}UseCase`

---

## 빌드 설정

### 환경 변수 (local.properties)

```properties
KAKAO_API_KEY=your_kakao_api_key
KAKAO_REDIRECT_URI=your_redirect_uri
BASE_URL=https://api.example.com/
```

### 주요 의존성

```kotlin
// Compose
androidx.compose.bom:2024.xx.xx
androidx.compose.material3
androidx.compose.ui

// Navigation3
androidx.navigation3.runtime
androidx.navigation3.ui

// Hilt
com.google.dagger:hilt-android
androidx.hilt:hilt-navigation-compose

// Network
com.squareup.retrofit2:retrofit
com.squareup.okhttp3:logging-interceptor
org.jetbrains.kotlinx:kotlinx-serialization-json

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-core
org.jetbrains.kotlinx:kotlinx-coroutines-android

// Coil
io.coil-kt:coil-compose

// Firebase
com.google.firebase:firebase-bom
com.google.firebase:firebase-crashlytics
com.google.firebase:firebase-analytics

// DataStore
androidx.datastore:datastore-preferences

// Kakao
com.kakao.sdk:v2-user
com.kakao.sdk:v2-share

// Timber
com.jakewharton.timber:timber

// Lottie
com.airbnb.android:lottie-compose

// Testing
junit:junit
io.mockk:mockk
app.cash.turbine:turbine
io.github.takahirom.roborazzi:roborazzi
```

---

## 참고 자료

### 프로젝트 내부 문서
- `/mnt/transcripts/` - 개발 히스토리 및 대화 기록
- 최근 구현: Differential State Management, 이미지 업로드, 공개범위 설정

### 외부 자료
- [Jetpack Compose 공식 문서](https://developer.android.com/jetpack/compose)
- [Hilt 공식 문서](https://dagger.dev/hilt/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Navigation3](https://github.com/raamcosta/compose-destinations) (참고용)

---

## 버전 히스토리

### v1.0.0 (현재)
- 온보딩 플로우 구현
- 취미 관리 기능
- 활동 기록 기능
- 이미지 업로드 (S3)
- 공개범위 설정

### 향후 계획
- Multi-module 구조 전환
- Offline-first 아키텍처
- 위젯 기능
- 소셜 기능 확장

---

## 문의 및 이슈

프로젝트 관련 문의사항이나 이슈는 개발팀에 문의하세요.

**최종 업데이트**: 2026-01-25