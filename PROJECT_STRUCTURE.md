# ForDay Android — 프로젝트 구조 문서

> **최종 업데이트**: 2026-02-22
> **현재 브랜치**: refactor/second_develop
> **패키지명**: `com.forday.app` / App ID: `com.dayn.forday`

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [아키텍처 개요](#2-아키텍처-개요)
3. [전체 디렉토리 구조](#3-전체-디렉토리-구조)
4. [레이어별 상세 설명](#4-레이어별-상세-설명)
   - [Remote Layer](#41-remote-layer)
   - [Data Layer](#42-data-layer)
   - [Domain Layer](#43-domain-layer)
   - [Presentation Layer](#44-presentation-layer)
   - [Core Module](#45-core-module)
5. [네비게이션 구조](#5-네비게이션-구조)
6. [핵심 패턴 및 규칙](#6-핵심-패턴-및-규칙)
7. [리소스 구조](#7-리소스-구조)
8. [빌드 설정](#8-빌드-설정)
9. [통계 요약](#9-통계-요약)

---

## 1. 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 앱 이름 | ForDay |
| 설명 | 취미 활동 기록 및 관리 앱 |
| 언어 | Kotlin |
| UI 프레임워크 | Jetpack Compose + Material3 |
| DI | Hilt |
| 비동기 | Coroutines + Flow |
| 네트워크 | Retrofit + OkHttp + Kotlinx Serialization |
| 네비게이션 | Navigation3 (Type-safe, `@Serializable`) |
| 로컬 저장소 | DataStore (Preferences) |
| 이미지 로딩 | Coil |
| 로깅 | Timber + Firebase Crashlytics + Analytics |
| 소셜 로그인 | Kakao SDK |
| Min SDK | 28 (Android 9.0) |
| Target SDK | 36 |
| Compile SDK | 36 |
| Java 버전 | 17 |
| 앱 버전 | 1.0.0 (versionCode 2) |

---

## 2. 아키텍처 개요

```
┌─────────────────────────────────────────────────┐
│           PRESENTATION LAYER                    │
│   Screen(Composable) ↔ ViewModel ↔ UiState     │
│   SideEffect (one-time events via Channel)      │
└───────────────────┬─────────────────────────────┘
                    │ UseCase 호출
┌───────────────────▼─────────────────────────────┐
│              DOMAIN LAYER                       │
│   UseCase → Repository Interface → DomainModel  │
└───────────────────┬─────────────────────────────┘
                    │ DataSource 호출
┌───────────────────▼─────────────────────────────┐
│               DATA LAYER                        │
│   RepositoryImpl → DataSource → Entity          │
└───────────────────┬─────────────────────────────┘
                    │ API 호출
┌───────────────────▼─────────────────────────────┐
│              REMOTE LAYER                       │
│   Retrofit API Service → Request/Response DTO   │
└───────────────────┬─────────────────────────────┘
                    │ HTTP
              Backend Server
```

### 데이터 흐름 (Mapper 체인)

```
API Response (JSON)
    ↓  RemoteMapper.toData()
Entity  (data/model/*Entity.kt)
    ↓  DataMapper.toDomain()
Domain Model  (domain/model/*Domain.kt)
    ↓  .toPresentation()  (확장 함수)
UI Model  (presentation/*/model/*UiModel.kt)
    ↓
Screen (Composable)
```

---

## 3. 전체 디렉토리 구조

```
app/src/main/java/com/forday/app/
│
├── FordayApplication.kt                    # Application 클래스 (Hilt, Timber 초기화)
│
├── core/                                   # 공통 모듈 (전 레이어에서 사용)
│   ├── datastore/
│   │   ├── di/DataStoreModule.kt
│   │   ├── model/
│   │   └── UserLocalDataSource.kt          # 토큰, 온보딩 완료 여부 등 로컬 저장
│   ├── designsystem/
│   │   ├── component/
│   │   │   ├── bottomsheet/
│   │   │   │   ├── AiRecommendationBottomSheet.kt
│   │   │   │   └── OnboardingBottomSheet.kt
│   │   │   ├── button/
│   │   │   │   ├── AddHobbyButton.kt
│   │   │   │   ├── AIRecommendationButton.kt
│   │   │   │   ├── BottomNavigationButtons.kt
│   │   │   │   ├── BottomNextButton.kt
│   │   │   │   ├── DirectInputButton.kt
│   │   │   │   ├── FordayCheckButton.kt
│   │   │   │   ├── FordayNextButton.kt
│   │   │   │   ├── MainButton.kt
│   │   │   │   └── RadioButton.kt
│   │   │   ├── checkbox/
│   │   │   │   ├── CheckBoxRound.kt
│   │   │   │   ├── CheckBoxSquare.kt
│   │   │   │   └── CheckIcon.kt
│   │   │   ├── clickable/
│   │   │   │   └── ThrottledClick.kt       # 중복 클릭 방지
│   │   │   ├── dropdown/
│   │   │   │   ├── PrivacySettingDropdown.kt
│   │   │   │   ├── RoutineDropdown.kt
│   │   │   │   ├── SelectField.kt
│   │   │   │   └── SettingsDropdown.kt
│   │   │   ├── layout/
│   │   │   │   └── OnboardingLayout.kt     # 온보딩 공통 레이아웃 (진행률 포함)
│   │   │   ├── navigationbar/
│   │   │   │   └── BottomNavigationbar.kt
│   │   │   ├── photo/
│   │   │   │   └── PhotoPickerBottomSheet.kt
│   │   │   ├── progressbar/
│   │   │   │   └── ForDayProgressBar.kt
│   │   │   ├── state/
│   │   │   │   ├── ErrorContent.kt
│   │   │   │   └── ErrorDataUiState.kt
│   │   │   ├── textfield/
│   │   │   │   ├── HobbyTextField.kt
│   │   │   │   ├── NicknameTextField.kt
│   │   │   │   ├── RoutineTextField.kt
│   │   │   │   └── TextFieldState.kt
│   │   │   ├── toggle/
│   │   │   │   ├── FolderItemToggle.kt
│   │   │   │   └── ToggleSwitch.kt
│   │   │   ├── tooltip/
│   │   │   │   └── HintBubble.kt
│   │   │   └── topbar/
│   │   │       └── TopAppBar.kt
│   │   ├── dialog/
│   │   │   ├── CommonDialog.kt
│   │   │   ├── HobbyInputDialog.kt
│   │   │   ├── HobbyLimitDialog.kt
│   │   │   ├── LogoutDialog.kt
│   │   │   └── RoutineOnlyOneHaveDialog.kt
│   │   ├── theme/
│   │   │   ├── FordayColor.kt
│   │   │   ├── ForDayGradients.kt
│   │   │   ├── FordayTypography.kt
│   │   │   └── Theme.kt
│   │   └── toast/
│   │       ├── ErrorToast.kt
│   │       └── ToastPopup.kt
│   ├── exception/
│   │   └── Exception.kt
│   ├── extension/
│   │   └── LifecycleOwnerExt.kt
│   ├── firebase/
│   ├── logger/
│   │   ├── analytics/
│   │   │   ├── AnalyticsManager.kt         # 인터페이스
│   │   │   └── AnalyticsManagerImpl.kt     # Firebase Analytics 구현체
│   │   ├── crashlytics/
│   │   │   ├── CrashlyticsManager.kt
│   │   │   └── CrashlyticsManagerImpl.kt
│   │   ├── di/
│   │   │   └── LoggerModule.kt
│   │   └── timber/
│   │       ├── CrashlyticsTree.kt
│   │       ├── DebugLogTree.kt
│   │       └── TimberInitializer.kt
│   ├── navigation/
│   │   └── Route.kt                        # NavKey 정의 (Navigation3 타입 안전 라우팅)
│   ├── session/
│   │   └── AuthEventBus.kt                 # 전역 로그아웃/인증 만료 이벤트
│   └── util/
│       ├── ImageCodeMapper.kt
│       ├── ServerErrorMessage.kt
│       ├── StickerImageMapper.kt
│       └── ThrowableUserMessage.kt
│
├── remote/                                 # Remote Layer (네트워크)
│   ├── RemoteMapper.kt                     # interface: toData(): Entity
│   ├── api/
│   │   ├── service/
│   │   │   ├── AuthApi.kt
│   │   │   ├── FileApi.kt
│   │   │   ├── HobbyApi.kt
│   │   │   ├── RoutineApi.kt
│   │   │   ├── SosikApi.kt
│   │   │   ├── TokenApi.kt
│   │   │   └── UserApi.kt
│   │   └── interceptor/
│   │       ├── TokenInterceptor.kt         # 요청마다 Authorization 헤더 주입
│   │       └── TokenProvider.kt
│   ├── di/
│   │   ├── NetworkModule.kt                # Retrofit, OkHttp, Moshi 설정
│   │   └── RemoteDataSourceModule.kt
│   ├── impl/
│   │   ├── AuthDataSourceImpl.kt
│   │   ├── FileDataSourceImpl.kt
│   │   ├── HobbyDataSourceImpl.kt
│   │   ├── RoutineDataSourceImpl.kt
│   │   ├── SosikDataSourceImpl.kt
│   │   └── UserDataSourceImpl.kt
│   └── model/
│       ├── request/                        # API 요청 DTO (23개)
│       │   ├── BlockUserRequest.kt
│       │   ├── CancelAccountRequest.kt
│       │   ├── CreateHobbyRequest.kt
│       │   ├── CreateRoutinesRequest.kt
│       │   ├── ExtendHobbyPeriodRequest.kt
│       │   ├── GuestLoginRequest.kt
│       │   ├── KakaoLoginRequest.kt
│       │   ├── ModifyHobbyDurationRequest.kt
│       │   ├── ModifyHobbyExecutionCountRequest.kt
│       │   ├── ModifyHobbyTimeRequest.kt
│       │   ├── ModifyPostingRequest.kt
│       │   ├── ModifyPostingVisibilityRequest.kt
│       │   ├── ModifyRoutineRequest.kt
│       │   ├── ProfileImageRequest.kt
│       │   ├── RecreateHobbyRequest.kt
│       │   ├── RegisterNicknameRequest.kt
│       │   ├── ReportPostingRequest.kt
│       │   ├── UpdateHobbyStatusRequest.kt
│       │   ├── WriteRoutineRequest.kt
│       │   └── ...
│       └── response/                       # API 응답 DTO (65개+)
│           ├── AiRecommendedRoutinesResponse.kt
│           ├── BlockUserResponse.kt
│           ├── HomeHobbyResponse.kt
│           ├── HobbyRoutineListResponse.kt
│           ├── PreviousAiRecommendResponse.kt
│           ├── ReportPostingResponse.kt
│           ├── RoutineRecordDetailResponse.kt
│           ├── SosikResponse.kt
│           ├── UserFeedResponse.kt
│           └── ...
│
├── data/                                   # Data Layer
│   ├── DataMapper.kt                       # interface: toDomain(): DomainModel
│   ├── AuthTokenProvider.kt
│   ├── di/
│   │   ├── RepositoryModule.kt             # Repository 바인딩 (Hilt)
│   │   └── TokenModule.kt
│   ├── impl/
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── FileRepositoryImpl.kt
│   │   ├── HobbyRepositoryImpl.kt
│   │   ├── RoutineRepositoryImpl.kt
│   │   ├── S3UploadRepository.kt
│   │   ├── SosikRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   ├── model/                              # Data Entity (56개)
│   │   ├── (인증) KakaoLoginEntity, GuestLoginEntity, LogoutEntity
│   │   ├── (취미) CreateHobbyEntity, HobbyEntity, HomeHobbyEntity,
│   │   │         RecreateHobbyEntity, UpdateHobbyStatusEntity, ...
│   │   ├── (루틴) CreateRoutinesEntity, RoutineListEntity,
│   │   │         RoutineRecordDetailEntity, WriteRoutineEntity, ...
│   │   ├── (소셜) ReactionEntity, ScrapEntity, ScrapListEntity, ...
│   │   ├── (유저) UserDataEntity, ProfileEntity, ProfileImageEntity, ...
│   │   ├── (기타) OnboardingDataEntity, PresignedUrlEntity, ErrorBodyEntity,
│   │   │         BlockUserEntity, PreviousAiRecommendEntity,
│   │   │         ReportPostingEntity, SosikEntity, ...
│   │   └── DataLayerEntity.kt              # 기본 Entity 인터페이스
│   └── remote/                             # DataSource 인터페이스 (6개)
│       ├── AuthDataSource.kt
│       ├── FileDataSource.kt
│       ├── HobbyDataSource.kt
│       ├── RoutineDataSource.kt
│       ├── SosikDataSource.kt
│       └── UserDataSource.kt
│
├── domain/                                 # Domain Layer
│   ├── model/                              # Domain 모델 (57개)
│   │   ├── (인증) KakaoLoginDomain, GuestLoginDomain, LogoutDomain
│   │   ├── (취미) CreateHobbyDomain, HobbyDomain, HomeHobbyDomain,
│   │   │         RecreateHobbyDomain, MyHobbyListDomain, ...
│   │   ├── (루틴) CreateRoutinesDomain, RoutineListDomain,
│   │   │         RoutineRecordDetailDomain, WriteRoutineDomain, ...
│   │   ├── (소셜) ReactionDomain, ScrapDomain, SearchHobbyMateRoutinesDomain, ...
│   │   ├── (유저) UserData, ProfileDomain, ProfileImageDomain, ...
│   │   ├── (기타) BlockUserDomain, PreviousAiRecommendDomain,
│   │   │         ReportPostingDomain, SosikDomain, ...
│   │   └── DomainEntity.kt                 # 기본 Domain 인터페이스
│   ├── repository/                         # Repository 인터페이스 (7개)
│   │   ├── AuthRepository.kt
│   │   ├── FileRepository.kt
│   │   ├── HobbyRepository.kt
│   │   ├── RoutineRepository.kt
│   │   ├── S3UploadRepository.kt
│   │   ├── SosikRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/                            # Use Case (71개)
│       ├── (인증) GetAccessTokenUseCase, KakaoLoginUseCase,
│       │         GuestLoginUseCase, RemoveAccessTokenUseCase
│       ├── (취미) CreateHobbyUseCase, ChangeHobbyStatusUseCase,
│       │         GetHobbyDataUseCase, GetMyHobbyListUseCase,
│       │         ModifyHobbyDurationUseCase, ModifyHobbyTimeUseCase,
│       │         ModifyHobbyExecutionCountUseCase, RecreateHobbyUseCase,
│       │         SetHobbyMainImageUseCase, ExtendHobbyPeriodUseCase, ...
│       ├── (루틴) CreateRoutinesUseCase, WriteRoutineUseCase,
│       │         GetAiRecommendedRoutinesUseCase,
│       │         GetAiRecommendedRoutinesAgainUseCase,
│       │         GetSpecificRoutineListUseCase,
│       │         GetMyRoutineRecordDetailUseCase,
│       │         GetPeopleRoutineListUseCase,
│       │         ModifyHobbyRoutineUseCase,
│       │         DeleteHobbyRoutineUseCase, ...
│       ├── (소셜) ReactionToRoutinePostingUseCase, CancelMyReactionUseCase,
│       │         ScrapPostingUseCase, CancelScrapPostingUseCase,
│       │         GetReactionUsersUseCase, GetUserScrapListUseCase, ...
│       ├── (유저/프로필) RegisterNicknameUseCase, SaveNicknameUseCase,
│       │         GetUserNicknameUseCase, GetIsNicknameDuplicateUseCase,
│       │         SetProfileImageUseCase, DeleteS3ImageUseCase,
│       │         GetUserInfoUseCase, SwitchAccountUseCase, ...
│       ├── (DataStore) SaveOnboardingDataUseCase, GetOnboardingDataUseCase,
│       │         SaveIsOnboardingCompletedUseCase, GetIsOnboardingCompletedUseCase,
│       │         SaveIsNicknameSetUseCase, GetIsNicknameSetUseCase,
│       │         SaveCreatedHobbyIdUseCase, ...
│       ├── (파일/이미지) GetPresignedUrlUseCase, UploadImageToS3UseCase
│       ├── (피드) GetUserFeedListUseCase, GetStickersUseCase,
│       │         GetHomeHobbyUseCase, GetUsersProgressHobbyTabsUseCase
│       └── (기타) BlockUserUseCase, ReportPostingUseCase,
│                 DeletePostingUseCase, ModifyPostingUseCase,
│                 ModifyPostingVisibilityUseCase, CancelAccountUseCase
│
└── presentation/                           # Presentation Layer
    ├── BaseViewModel.kt                    # SideEffect Channel + StateFlow 유틸
    ├── FlowHttpCatch.kt                    # HTTP 에러 처리 Flow 확장 함수
    ├── common/
    │   ├── AppSideEffect.kt
    │   ├── SnackbarHostViewModel.kt
    │   └── SnackbarManager.kt
    ├── main/                               # 앱 진입점 및 네비게이션 루트
    │   ├── MainActivity.kt
    │   ├── AppEntryPoint.kt                # 초기 라우트 결정 (토큰/온보딩 상태)
    │   ├── MainFlow.kt                     # 전체 화면 entryProvider 정의
    │   ├── MainNavigationState.kt          # 탭별 독립 백스택 관리
    │   ├── MainNavigator.kt                # navigate / goBack / resetTo / replaceWith
    │   └── MainEventViewModel.kt           # 전역 AuthEvent 처리
    │
    ├── onboarding/                         # 온보딩 플로우
    │   ├── OnboardingViewModel.kt
    │   ├── OnboardingUiState.kt
    │   ├── OnboardingAction.kt
    │   ├── splash/
    │   │   ├── SplashScreen.kt
    │   │   └── navigation/Splash.kt
    │   ├── login/
    │   │   ├── LoginScreen.kt
    │   │   └── navigation/Login.kt
    │   ├── hobbyselect/
    │   │   ├── SelectHobbyScreen.kt
    │   │   ├── HobbyItem.kt
    │   │   ├── DirectInputHobbyDialog.kt
    │   │   └── navigation/
    │   │       ├── SelectHobby.kt
    │   │       └── SelectHobbyFromModify.kt
    │   ├── timeselect/
    │   │   ├── SelectTimeScreen.kt
    │   │   ├── ScreenMode.kt               # ONBOARDING / DEFAULT (수정모드)
    │   │   └── navigation/SelectPerTime.kt
    │   ├── purposeselect/
    │   │   ├── SelectPurposeScreen.kt
    │   │   ├── Purpose.kt
    │   │   └── navigation/SelectPurpose.kt
    │   ├── frequencyselect/
    │   │   ├── SelectFrequencyScreen.kt
    │   │   └── navigation/SelectPerWeek.kt
    │   ├── periodselect/
    │   │   ├── SelectJourneyDaysScreen.kt
    │   │   └── navigation/SelectPeriod.kt
    │   ├── showpobbies/
    │   │   ├── ShowPobbiesScreen.kt
    │   │   ├── OnboardingSuccessScreen.kt
    │   │   └── navigation/
    │   │       ├── ShowPobbies.kt
    │   │       └── OnboardingSuccess.kt
    │   └── nicknameinput/
    │       ├── InputNicknameScreen.kt
    │       └── navigation/InputNickname.kt
    │
    ├── home/                               # 홈 화면
    │   ├── HomeViewModel.kt
    │   ├── HomeSideEffect.kt
    │   ├── HomeScreen.kt
    │   ├── component/
    │   │   ├── FloatingMenuPopup.kt
    │   │   └── header/
    │   │       ├── HomeHeaderScreen.kt
    │   │       ├── HomeHeaderState.kt
    │   │       └── HomeHeaderViewModel.kt
    │   ├── model/
    │   │   ├── HomeState.kt
    │   │   ├── HomeMapper.kt
    │   │   ├── HomeHobbyUiModel.kt
    │   │   ├── MyHobbyUiModel.kt
    │   │   ├── RoutineUiModel.kt
    │   │   ├── WriteRoutineUiModel.kt
    │   │   ├── HobbyStickerHistoryUiModel.kt
    │   │   ├── PreviousAiRecommendUiModel.kt
    │   │   ├── StickerInfoUiModel.kt
    │   │   └── AiRoutineItemState.kt
    │   └── navigation/Home.kt
    │
    ├── record/                             # 활동 기록 화면
    │   ├── RecordRoutineViewModel.kt
    │   ├── RecordRoutineUiState.kt
    │   ├── RecordRoutineUiModel.kt
    │   ├── RecordMapper.kt
    │   ├── DeleteImageUiModel.kt
    │   ├── PresignedUrlItemUiModel.kt
    │   ├── navigation/RecordRoutine.kt
    │   └── screen/RecordRoutineScreen.kt
    │
    ├── inputhobbyroutines/                 # 루틴 직접입력 & AI 추천
    │   ├── InputRoutinesAndAiRecommendViewModel.kt
    │   ├── InputRoutinesAndAiRecommendSideEffect.kt
    │   ├── RoutinesState.kt
    │   ├── model/PreviousAiRecommendUiModel.kt
    │   ├── navigation/InputRoutine.kt
    │   └── screen/
    │       ├── InputRoutineScreen.kt
    │       ├── AIRecommendationRoutinesScreen.kt
    │       └── LoadingRoutinesScreen.kt
    │
    ├── modifyhobby/                        # 취미 정보 수정
    │   ├── ModifyHobbyViewModel.kt
    │   ├── ModifyHobbySideEffect.kt
    │   ├── ModifyHobbyUiState.kt
    │   ├── HobbiesMapper.kt
    │   ├── navigation/ModifyHobby.kt
    │   └── screen/ModifyHobbyScreen.kt
    │
    ├── modifyroutine/                      # 루틴 수정
    │   ├── ModifyRoutineViewModel.kt
    │   ├── ModifyRoutineSideEffect.kt
    │   ├── RoutinesUiState.kt
    │   ├── RoutinesMapper.kt
    │   ├── navigation/ModifyRoutine.kt
    │   └── screen/ModifyRoutineScreen.kt
    │
    ├── mypage/                             # 마이페이지
    │   ├── MyPageViewModel.kt
    │   ├── MyPageSideEffect.kt
    │   ├── MyPageUiState.kt
    │   ├── MyPageMapper.kt
    │   ├── PresignedUrlItemUiModel.kt
    │   ├── main/
    │   │   ├── MyPageScreen.kt
    │   │   ├── FeedUiModel.kt
    │   │   ├── ScrapListUiModel.kt
    │   │   ├── UserHobbyTabUiModel.kt
    │   │   ├── UserInfoUiModel.kt
    │   │   └── navigation/MyPage.kt        # (core/navigation/Route.kt 에 정의됨)
    │   ├── profilesetting/
    │   │   ├── ProfileSettingScreen.kt
    │   │   └── navigation/ProfileSetting.kt
    │   ├── routinedetail/
    │   │   ├── RoutineRecordDetailUiModel.kt
    │   │   ├── HobbyMainImageUiModel.kt
    │   │   ├── ModifyPostingUiModel.kt
    │   │   ├── navigation/
    │   │   │   ├── RoutineDetail.kt
    │   │   │   └── SaveCard.kt
    │   │   └── screen/
    │   │       ├── RoutineDetailScreen.kt
    │   │       └── SaveCardScreen.kt
    │   └── hobbyphotosetting/
    │       ├── HobbyPhotoManagementScreen.kt
    │       └── navigation/HobbyPhotoSetting.kt
    │
    ├── discovery/                          # 둘러보기
    │   └── navigation/Discovery.kt
    │
    ├── sosik/                              # 소식 피드 (구 story)
    │   ├── SosikViewModel.kt
    │   ├── SosikSideEffect.kt
    │   ├── model/SosikUiState.kt
    │   ├── navigation/
    │   │   ├── Sosik.kt
    │   │   └── Register.kt
    │   └── screen/
    │       ├── SosikScreen.kt
    │       └── RegisterScreen.kt
    │
    └── allsettings/                        # 전체 설정
        ├── SettingsViewModel.kt
        ├── SettingsUiState.kt
        ├── SettingsSideEffect.kt
        ├── settings/
        │   ├── screen/SettingsScreen.kt
        │   └── navigation/Settings.kt
        ├── termsofservice/
        │   ├── screen/TermsOfServiceScreen.kt
        │   └── navigation/TermsOfService.kt
        ├── privacypolicy/
        │   ├── screen/PrivacyPolicyScreen.kt
        │   └── navigation/PrivacyPolicy.kt
        └── cancelaccount/
            ├── screen/CancelAccountScreen.kt
            └── navigation/CancelAccount.kt
```

---

## 4. 레이어별 상세 설명

### 4.1 Remote Layer

API 통신 및 응답 DTO 처리를 담당합니다.

**Retrofit API 서비스 목록:**

| 파일 | 담당 API 그룹 |
|------|--------------|
| `AuthApi.kt` | 카카오 로그인, 게스트 로그인, 토큰 갱신 |
| `FileApi.kt` | Presigned URL 발급 |
| `HobbyApi.kt` | 취미 생성/수정/삭제/상태 변경 |
| `RoutineApi.kt` | 루틴 CRUD, AI 추천, 기록 작성 |
| `SosikApi.kt` | 소식 피드, 신고 |
| `TokenApi.kt` | 토큰 재발급 |
| `UserApi.kt` | 프로필 조회/수정, 계정 관리, 차단 |

**주요 패턴:**

```kotlin
// RemoteMapper 인터페이스 — 모든 Response가 구현
interface RemoteMapper<T> {
    fun toData(): T
}

// 예시
data class HomeHobbyResponse(
    @SerialName("hobbies") val hobbies: List<HobbyItemResponse>? = null
) : RemoteMapper<HomeHobbyEntity> {
    override fun toData() = HomeHobbyEntity(
        hobbies = hobbies?.map { it.toData() } ?: emptyList()
    )
}
```

---

### 4.2 Data Layer

Repository 구현체와 Entity 모델을 포함합니다.

**Repository 구현체 목록:**

| 파일 | 담당 기능 |
|------|---------|
| `AuthRepositoryImpl.kt` | 인증 (로그인/로그아웃/토큰) |
| `FileRepositoryImpl.kt` | Presigned URL 발급 |
| `HobbyRepositoryImpl.kt` | 취미 전반 관리 |
| `RoutineRepositoryImpl.kt` | 루틴 CRUD 및 AI 추천 |
| `S3UploadRepository.kt` | S3 이미지 업로드 |
| `SosikRepositoryImpl.kt` | 소식 피드 |
| `UserRepositoryImpl.kt` | 유저 프로필/계정 관리 |

**DataMapper 인터페이스:**

```kotlin
interface DataMapper<T> {
    fun toDomain(): T
}
```

---

### 4.3 Domain Layer

비즈니스 로직의 핵심으로, 프레임워크에 의존하지 않습니다.

**UseCase 호출 패턴:**

```kotlin
class CreateHobbyUseCase @Inject constructor(
    private val repository: HobbyRepository
) {
    suspend operator fun invoke(params: CreateHobbyParams): CreateHobbyDomain {
        return repository.createHobby(params)
    }
}
```

**주요 UseCase 그룹 (총 71개):**

| 그룹 | 개수 | 예시 |
|------|------|------|
| 인증 | 4 | KakaoLoginUseCase, GetAccessTokenUseCase |
| 취미 | 15+ | CreateHobbyUseCase, RecreateHobbyUseCase |
| 루틴 | 12+ | WriteRoutineUseCase, GetAiRecommendedRoutinesUseCase |
| 소셜 | 8+ | ScrapPostingUseCase, ReactionToRoutinePostingUseCase |
| 유저 | 10+ | SetProfileImageUseCase, RegisterNicknameUseCase |
| DataStore | 10+ | SaveOnboardingDataUseCase |
| 파일 | 2 | GetPresignedUrlUseCase, UploadImageToS3UseCase |
| 기타 | 6+ | BlockUserUseCase, ReportPostingUseCase |

---

### 4.4 Presentation Layer

**BaseViewModel — 공통 기반:**

```kotlin
open class BaseViewModel<SIDE_EFFECT> : ViewModel() {
    protected val _sideEffectChannel = Channel<SIDE_EFFECT>(Channel.BUFFERED)
    val sideEffect = _sideEffectChannel.receiveAsFlow()

    // StateFlow를 viewModelScope에 바인딩
    protected fun <T> MutableStateFlow<T>.toStateIn(): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), value)
}
```

**FlowHttpCatch — HTTP 에러 처리:**

```kotlin
// 모든 API 호출에서 사용하는 확장 함수
flow {
    emit(useCase())
}.httpCatch(tag = "tagName") { errorData ->
    // HttpException (4xx, 5xx) 처리
    when (errorData.errorClassName) {
        "AI_CALL_LIMIT_EXCEEDED" -> handleAiLimitError()
        "VALIDATION_ERROR" -> handleValidationError(errorData.message)
    }
}.collect { result ->
    _uiState.update { it.copy(data = result) }
}
```

**Screen 구조 — Root / Screen 분리:**

```kotlin
// Root: ViewModel 연결 + 상태 수집 (hiltViewModel 사용)
@Composable
fun MyScreenRoot(
    onNext: () -> Unit,
    viewModel: MyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { /* ... */ }
    }
    MyScreen(state = state, onNext = onNext)
}

// Screen: Pure Composable (Preview 가능, ViewModel 없음)
@Composable
fun MyScreen(state: MyUiState, onNext: () -> Unit) { /* UI only */ }
```

**ScreenMode — 온보딩 vs 수정 모드:**

```kotlin
enum class ScreenMode {
    ONBOARDING,  // 선택 즉시 ViewModel에 저장
    DEFAULT      // 로컬 상태로 관리 → onNext에서만 ViewModel에 저장
}
```

---

### 4.5 Core Module

**UserLocalDataSource — DataStore 저장 항목:**

| 키 | 타입 | 설명 |
|----|------|------|
| `accessToken` | String? | 액세스 토큰 |
| `refreshToken` | String? | 리프레시 토큰 |
| `isOnboardingCompleted` | Boolean | 온보딩 완료 여부 |
| `isNicknameSet` | Boolean | 닉네임 설정 여부 |
| `onboardingData` | OnboardingDataEntity | 취미/시간/목적/주기/기간 |

**AuthEventBus — 전역 인증 이벤트:**

```kotlin
sealed class AuthEvent {
    object Expired : AuthEvent()  // 토큰 만료 → 강제 로그인 화면 이동
}
```

---

## 5. 네비게이션 구조

### 5.1 Bottom Navigation Tab (Top-level Destinations)

```kotlin
val TOP_LEVEL_DESTINATIONS = mapOf(
    Home      to BottomNavItem("홈", ...),
    Discovery to BottomNavItem("둘러보기", ...),
    Sosik     to BottomNavItem("소식", ...),
    MyPage    to BottomNavItem("마이페이지", ...)
)
```

### 5.2 Navigator 클래스

```kotlin
class Navigator(val state: MainNavigationState) {
    fun navigate(route: NavKey)     // 화면 이동 (탭 전환 or 스택 push)
    fun goBack(): Boolean           // 뒤로가기 (스택 pop, 성공 여부 반환)
    fun resetTo(route: NavKey)      // 모든 스택 초기화 후 해당 탭으로 이동
    fun replaceWith(route: NavKey)  // 현재 화면 교체 (LoadingRoutines → RoutineAiRecommend 등)
    fun handleBack(): Boolean       // 시스템 back 처리
}
```

---

### 5.2.1 `resetTo` 상세 설명

`resetTo`는 **뒤로가기로 이전 화면에 절대 돌아갈 수 없어야 하는 상황**에서 사용합니다.
일반 `navigate`는 기존 스택에 화면을 쌓지만, `resetTo`는 모든 스택을 완전히 비우고 새 출발점으로 만듭니다.

**내부 동작 순서:**

```
1. 모든 탭의 백스택 초기화
   state.backStacks.forEach { (key, stack) ->
       while (stack.removeLastOrNull() != null) { }  // 전부 제거
       stack.add(key)                                  // 루트 항목만 다시 추가
   }

2. 현재 탭(topLevelRoute) 변경
   state.topLevelRoute = route

3. startRoute 갱신 (핵심 차이점)
   state.startRoute = route
   → 뒤로가기 시 이 route가 새로운 기준점이 됨
   → 이전 온보딩/로그인 화면으로 돌아가지 못하도록 차단

4. 대상 스택에 route가 없으면 추가
   if (targetStack.lastOrNull() != route) targetStack.add(route)

5. notifyNavChanged() 호출 → UI 갱신
```

**`navigate` vs `resetTo` 비교:**

| 항목 | `navigate(Home)` | `resetTo(Home)` |
|------|-----------------|-----------------|
| 기존 스택 | 유지 (스택에 추가) | 모두 초기화 |
| 뒤로가기 | 이전 화면으로 돌아감 | 이전 화면 없음 |
| `startRoute` 변경 | 변경 안 됨 | 변경됨 |
| 사용 목적 | 일반 화면 전환 | 플로우 완전 종료 후 이동 |

**실제 사용 사례:**

| 호출 위치 | 코드 | 이유 |
|-----------|------|------|
| 온보딩 완료 후 | `navigator.resetTo(Home)` | 온보딩 플로우 전체를 백스택에서 제거 |
| 닉네임 입력 완료 후 | `navigator.resetTo(Home)` | 로그인~닉네임 전 과정 제거 |
| 활동 기록 완료 후 | `navigator.resetTo(MyPage)` | 기록 플로우를 제거하고 MyPage로 이동 |
| 로그아웃/계정탈퇴 후 | `navigator.resetTo(Login)` | 앱 전체 상태 초기화 후 로그인 화면으로 |
| 토큰 만료(강제 로그아웃) | `navigator.resetTo(Login)` | `AuthEventBus`에서 수신 후 강제 이동 |
| 신고 완료 후 | `navigator.resetTo(Sosik)` | 신고 플로우를 제거하고 소식 탭으로 복귀 |

**주의 사항:**

- `resetTo` 호출 후에는 시스템 뒤로가기를 눌러도 이전 화면으로 돌아갈 수 없습니다.
- `startRoute`까지 갱신하기 때문에 `handleBack()`의 기준점도 바뀝니다.
- 탭이 아닌 route (예: `Login`, `SelectHobby`)를 전달하면 해당 route가 새 startRoute가 됩니다.

---

### 5.3 초기 라우팅 로직

```
SplashScreen 진입
    ↓
AppEntryPoint에서 DataStore 상태 확인
    ↓
accessToken == null           → Login
isOnboardingCompleted == false → SelectHobby
isNicknameSet == false        → SelectPeriod (온보딩 재개)
이상 없음                      → Home
```

### 5.4 전체 NavKey 목록

| 그룹 | NavKey |
|------|--------|
| **Bottom Bar** | `Home`, `Discovery`, `Sosik`, `MyPage` |
| **온보딩** | `Splash`, `Login`, `SelectHobby`, `SelectHobbyFromModify`, `SelectPerTime`, `SelectPurpose`, `SelectPerWeek`, `SelectPeriod`, `OnboardingSuccess`, `ShowPobbies`, `InputNickname` |
| **메인 앱** | `RecordRoutine`, `InputRoutine`, `LoadingRoutines`, `RoutineAiRecommend`, `ModifyRoutine`, `ModifyHobby`, `RoutineDetail`, `SaveCard`, `ProfileSetting`, `HobbyPhotoSetting`, `Register` |
| **설정** | `Settings`, `TermsOfService`, `PrivacyPolicy`, `CancelAccount` |

---

## 6. 핵심 패턴 및 규칙

### 6.1 ViewModel 패턴

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val useCase: MyUseCase
) : BaseViewModel<MySideEffect>() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.toStateIn()

    fun fetchData() = viewModelScope.launch {
        flow {
            emit(useCase())
        }.httpCatch(tag = "fetchData") { errorData ->
            _sideEffectChannel.send(MySideEffect.Error(errorData.message))
        }.collect { result ->
            _uiState.update { it.copy(data = result) }
        }
    }
}
```

### 6.2 UiState 패턴

```kotlin
data class MyUiState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val errorData: ErrorDataUiState? = null,
    val selectedId: Long? = null
)
```

### 6.3 SideEffect 패턴

```kotlin
sealed interface MySideEffect {
    data class Error(val message: String) : MySideEffect
    data object NavigateToNext : MySideEffect
}
```

### 6.4 이미지 업로드 플로우 (S3)

```
1. 갤러리에서 이미지 선택
2. GetPresignedUrlUseCase → Presigned URL 요청
3. UploadImageToS3UseCase → S3에 직접 업로드 (PUT)
4. 업로드 완료 → 체크마크 표시
5. 기록 작성 API 호출 시 이미지 URL 목록 전달
```

---

## 7. 리소스 구조

```
app/src/main/res/
├── drawable/                    # 200+ 아이콘 및 이미지
│   ├── (앱 아이콘) app_icon.xml, logo_forday.xml
│   ├── (취미 아이콘 10종) hobby_book.xml, hobby_cafe.xml, ...
│   ├── (취미 카드) hobbycard_*.png (10종)
│   ├── (하단 탭) home.xml, home_selected.xml, discovery.xml, ...
│   ├── (스티커 4종) ic_sticker_angry.xml, ic_sticker_laugh.xml, ...
│   ├── (리액션) ic_cool_selected/unselected.xml, ic_fire_*.xml, ...
│   ├── (UI 컨트롤) ic_check*.xml, ic_chevron*.xml, ic_close*.xml, ...
│   ├── (로고) ic_kakao.xml, ic_ai*.xml, ic_logo_text.xml, ...
│   ├── (캐릭터) character_one.xml ~ character_four.xml, ic_pobby.png
│   └── (스플래시 애니메이션) splash_logo*.xml (3종)
├── font/
│   └── pretendard_std_variable.ttf    # 메인 폰트
├── mipmap-*/                           # 앱 아이콘 (hdpi ~ xxxhdpi)
├── raw/                                # Lottie 애니메이션
│   ├── loading_dots.json
│   ├── onboarding_success.json
│   └── record_complete.json
├── values/
│   ├── colors.xml
│   ├── strings.xml
│   └── themes.xml
└── xml/
    ├── backup_rules.xml
    ├── data_extraction_rules.xml
    └── filepaths.xml                   # FileProvider 경로 설정
```

---

## 8. 빌드 설정

### 8.1 app/build.gradle.kts 주요 의존성

```kotlin
// Compose
implementation(libs.androidx.compose.bom)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.ui)

// Navigation3
implementation(libs.navigation3.runtime)
implementation(libs.navigation3.ui)
implementation(libs.androidx.lifecycle.viewmodel.navigation3)

// Hilt DI
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
implementation(libs.hilt.navigation.compose)

// Network
implementation(libs.retrofit)
implementation(libs.okhttp.logging.interceptor)
implementation(libs.kotlinx.serialization.json)

// Coroutines
implementation(libs.kotlinx.coroutines.core)
implementation(libs.kotlinx.coroutines.android)

// Image
implementation(libs.coil.compose)

// Firebase
implementation(platform(libs.firebase.bom))
implementation(libs.firebase.crashlytics)
implementation(libs.firebase.analytics)

// DataStore
implementation(libs.datastore.preferences)

// Kakao
implementation(libs.kakao.user)

// Logging & Animation
implementation(libs.timber)
implementation(libs.lottie.compose)
```

### 8.2 환경 변수 (local.properties)

```properties
KAKAO_API_KEY=your_kakao_api_key
BASE_URL=https://api.example.com/
```

---

## 9. 통계 요약

| 항목 | 수량 |
|------|------|
| Kotlin 소스 파일 | 472개 |
| Data Entity 클래스 | 56개 |
| Domain Model 클래스 | 57개 |
| Repository 인터페이스 | 7개 |
| Use Case 클래스 | 71개 |
| API Service 인터페이스 | 7개 |
| API Response DTO | 65개+ |
| API Request DTO | 23개+ |
| Presentation 화면 (Screen) | 35개+ |
| ViewModel | 12개 |
| NavKey (화면 키) | 28개 |
| Drawable 리소스 | 200개+ |
| Lottie 애니메이션 | 3개 |
| Design System 컴포넌트 | 50개+ |
| Dialog 종류 | 5개 |
| 총 리소스 파일 | 217개+ |
