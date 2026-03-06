# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===================================
# General Android
# ===================================
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ===================================
# Kotlin
# ===================================
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Kotlin Result
-keep class kotlin.Result { *; }

# ===================================
# Kotlinx Serialization
# ===================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Polymorphic 직렬화 — MainNavigationState의 serializersConfig에서
# PolymorphicSerializer(NavKey::class) + SerializersModule { polymorphic(...) } 사용
-keep class kotlinx.serialization.PolymorphicSerializer { *; }
-keep class kotlinx.serialization.modules.** { *; }
-keepclassmembers class kotlinx.serialization.modules.** {
    *;
}

# 직렬화 가능한 클래스 보존
-keep,includedescriptorclasses class com.forday.app.**$$serializer { *; }
-keepclassmembers class com.forday.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.forday.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable 어노테이션이 붙은 모든 클래스 보존
-keep @kotlinx.serialization.Serializable class ** { *; }

# ===================================
# 프로젝트 데이터 모델 - 반드시 보존
# ===================================
# Keep entire model classes to prevent ClassCastException
-keep class com.forday.app.remote.model.request.** { *; }
-keep class com.forday.app.remote.model.response.** { *; }
-keep class com.forday.app.data.model.** { *; }
-keep class com.forday.app.domain.model.** { *; }

# Keep model field names (request/response) to avoid release-only API failures due to obfuscated JSON keys
-keepclassmembers class com.forday.app.remote.model.request.** { <fields>; }
-keepclassmembers class com.forday.app.remote.model.response.** { <fields>; }
-keepclassmembers class com.forday.app.data.model.** { <fields>; }
-keepclassmembers class com.forday.app.domain.model.** { <fields>; }

# ===================================
# Navigation 파라미터로 사용되는 UI 모델 - 필드명 보존 필수
# ===================================
-keep class com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel { *; }
-keep class com.forday.app.presentation.mypage.routinedetail.RoutineReactionUiModel { *; }
-keep class com.forday.app.presentation.mypage.routinedetail.RoutineUserReactionUiModel { *; }
-keep class com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams { *; }

-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel { <fields>; }
-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.RoutineReactionUiModel { <fields>; }
-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.RoutineUserReactionUiModel { <fields>; }
-keepclassmembers class com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams { <fields>; }

# ===================================
# Navigation 파라미터로 사용되는 Enum - 직렬화 필수
# ===================================
-keep enum com.forday.app.presentation.onboarding.timeselect.ScreenMode { *; }
-keepclassmembers enum com.forday.app.presentation.onboarding.timeselect.ScreenMode { <fields>; }

# ===================================
# NavKey 구현체 필드명 보존 (파라미터가 있는 라우트)
# ===================================
-keepclassmembers class com.forday.app.presentation.record.navigation.RecordRoutine { <fields>; }
-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail { <fields>; }
-keepclassmembers class com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine { <fields>; }
-keepclassmembers class com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine { <fields>; }
-keepclassmembers class com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime { <fields>; }
-keepclassmembers class com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek { <fields>; }
-keepclassmembers class com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod { <fields>; }
-keepclassmembers class com.forday.app.core.navigation.LoadingRoutines { <fields>; }
-keepclassmembers class com.forday.app.core.navigation.RoutineAiRecommend { <fields>; }
-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.navigation.SaveCard { <fields>; }

# ===================================
# Repository & UseCase
# ===================================
-keep class com.forday.app.domain.repository.** { *; }
-keep class com.forday.app.domain.usecase.** { *; }
-keep class com.forday.app.data.impl.** { *; }

# ===================================
# Retrofit
# ===================================
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Retrofit with R8 full mode
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ===================================
# OkHttp
# ===================================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# https://github.com/square/okhttp/pull/6792
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.bouncycastle.jsse.**

# ===================================
# Hilt
# ===================================
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }

-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
}

-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# ===================================
# Coroutines
# ===================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ===================================
# Jetpack Compose
# ===================================
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keepclassmembers class androidx.compose.** {
    <methods>;
}

# ===================================
# Firebase
# ===================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Crashlytics - keep exception info for better crash reports
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# ===================================
# DataStore
# ===================================
-keep class androidx.datastore.*.** {*;}

# ===================================
# Coil (Image Loading)
# ===================================
-keep class coil.** { *; }
-dontwarn coil.**

# ===================================
# Navigation (Navigation3)
# ===================================
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.**
-keepclassmembers class androidx.navigation.** {
    <methods>;
}

# Navigation3 패키지 (androidx.navigation3.*)
-keep class androidx.navigation3.** { *; }
-keepnames class androidx.navigation3.**
-keepclassmembers class androidx.navigation3.** {
    <methods>;
}

# SavedState — Navigation3의 rememberSerializable / SavedStateConfiguration / MutableStateSerializer 사용
-keep class androidx.savedstate.** { *; }
-keepclassmembers class androidx.savedstate.** {
    <methods>;
}

# ===================================
# Kakao SDK
# ===================================
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# ===================================
# Kotlinx DateTime
# ===================================
-keep class kotlinx.datetime.** { *; }

# ===================================
# Kotlinx Immutable Collections
# ===================================
-keep class kotlinx.collections.immutable.** { *; }

# ===================================
# Enum
# ===================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===================================
# Parcelable
# ===================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ===================================
# Serializable
# ===================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===================================
# Glance (App Widgets)
# ===================================
-keep class androidx.glance.** { *; }

# ===================================
# Timber (Logging)
# ===================================
-dontwarn org.jetbrains.annotations.**

# ===================================
# Lottie
# ===================================
-keep class com.airbnb.lottie.** { *; }

# ===================================
# R8 Optimization
# ===================================
-allowaccessmodification
-repackageclasses

-keep class com.forday.app.core.navigation.** { *; }
-keep class com.forday.app.presentation.**.navigation.** { *; }

# Navigator 및 NavigationState 보존
-keep class com.forday.app.presentation.main.Navigator { *; }
-keep class com.forday.app.presentation.main.MainNavigationState { *; }

# NavKey 구현체 보존
-keep class * implements androidx.navigation3.runtime.NavKey { *; }
-keepclassmembers class * implements androidx.navigation3.runtime.NavKey { *; }

# BottomNavItem 보존
-keep class com.forday.app.presentation.main.BottomNavItem { *; }

# ===================================
# Navigation 파라미터로 사용되는 Serializable 클래스
# ===================================
-keep class com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState { *; }
-keepclassmembers class com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState { <fields>; }

# ===================================
# 도메인/프레젠테이션 Enum 보존
# ===================================
-keep enum com.forday.app.domain.model.VisibilityType { *; }
-keepclassmembers enum com.forday.app.domain.model.VisibilityType { <fields>; }

-keep enum com.forday.app.presentation.onboarding.purposeselect.Purpose { *; }
-keepclassmembers enum com.forday.app.presentation.onboarding.purposeselect.Purpose { <fields>; }

# ===================================
# R8 Class Merging 명시적 방지
# proguard-android-optimize.txt에 포함되어 있으나 R8 full mode에서
# 명시적 선언이 더 안전. 동일 구조 data object NavKey들(Sosik, Register 등)이
# 수평 병합되면 when (backStackEntry) { is Sosik -> ... } 타입 매칭 실패
# → 간헐적 화면 전환 불가 증상 발생
# ===================================
-optimizations !class/merging/vertical,!class/merging/horizontal

# ===================================
# HiltViewModel 명시적 보호
# hiltViewModel()은 런타임 리플렉션으로 ViewModel 클래스를 조회.
# Hilt AAR에 consumer rule이 포함되어 있으나 릴리즈 빌드에서
# 클래스 타입을 찾지 못하는 간헐적 오류 방지를 위해 명시적으로 보호.
# ===================================
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ===================================
# 최근 추가된 NavKey - 필드명 명시적 보존
# com.forday.app.presentation.**.navigation.** 규칙으로 클래스 자체는 보존되나
# 직렬화 안정성을 위해 필드명을 명시적으로 선언
# ===================================
-keepclassmembers class com.forday.app.presentation.mypage.navigation.UserPage { <fields>; }
-keepclassmembers class com.forday.app.presentation.sosik.navigation.Sosik { <fields>; }
-keepclassmembers class com.forday.app.presentation.sosik.navigation.Register { <fields>; }