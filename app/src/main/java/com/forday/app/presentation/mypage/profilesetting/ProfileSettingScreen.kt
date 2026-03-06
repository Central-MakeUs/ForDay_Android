package com.forday.app.presentation.mypage.profilesetting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.heightIn
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dayn.forday.R
import com.forday.app.presentation.mypage.MyPageViewModel
import android.provider.OpenableColumns
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

// 색상 정의
object ProfileSettingColors {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Neutral900 = Color(0xFF1E1E1E)
    val Neutral800 = Color(0xFF3A3A3A)
    val Neutral500 = Color(0xFF9E9E9E)
    val Stroke001 = Color(0xFFE5E5E5)
    val Background002 = Color(0xFFF9F9F9)
}

@Composable
fun ProfileSettingScreenRoot(
    goBack: () -> Unit,
    viewModel: MyPageViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 상태 관리
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadComplete by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showProfilePhotoBottomSheet by remember { mutableStateOf(false) }

    // 추가: 삭제 예정 플래그
    var isMarkedForDeletion by remember { mutableStateOf(false) }

    // 닉네임 관련 상태
    var nickname by remember { mutableStateOf(state.userInfo?.nickName ?: "") }
    var nicknameErrorMessage by remember { mutableStateOf("") }
    var nicknameSuccessMessage by remember { mutableStateOf("") }
    var isNicknameChanged by remember { mutableStateOf(false) }
    var hasFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun validateNickname(): Boolean {
        if (hasFocused) {
            val message = when {
                nickname.isBlank() -> "필수 입력 항목입니다."
                !nickname.matches(Regex("^[가-힣a-zA-Z0-9]+$")) -> "한글, 영어, 숫자만 사용할 수 있습니다."
                else -> ""
            }
            if (message.isNotEmpty()) {
                nicknameErrorMessage = message
                nicknameSuccessMessage = ""
            } else {
                nicknameErrorMessage = ""
            }
            return message.isEmpty()
        }
        return false
    }

    val coroutineScope = rememberCoroutineScope()
    var showUploadingToast by remember { mutableStateOf(false) }

    // 화면 진입 시 이전 닉네임 중복 확인 결과 초기화
    LaunchedEffect(Unit) {
        viewModel.resetNicknameCheck()
    }

    // 닉네임 중복 확인 결과 반응
    LaunchedEffect(state.nicknameCheckMessage, state.isNicknameChecked) {
        when {
            state.nicknameCheckMessage.isEmpty() -> {
                nicknameErrorMessage = ""
                nicknameSuccessMessage = ""
            }
            state.isNicknameChecked == true -> {
                nicknameSuccessMessage = state.nicknameCheckMessage
                nicknameErrorMessage = ""
            }
            state.isNicknameChecked == false -> {
                nicknameErrorMessage = state.nicknameCheckMessage
                nicknameSuccessMessage = ""
            }
        }
    }

    // 닉네임 등록 성공 시 goBack
    LaunchedEffect(state.nicknameRegisterSuccess) {
        if (state.nicknameRegisterSuccess) {
            viewModel.resetNicknameRegisterSuccess()
            goBack()
        }
    }

    // 이미지 업로드 시작 함수
    fun startProfileImageUpload(uri: Uri) {
        selectedImageUri = uri
        isUploading = true
        uploadComplete = false
        showError = false

        val imageInfo = listOf(
            mapOf(
                "fileName" to getFileName(context, uri),
                "contentType" to getContentType(context, uri),
                "usage" to "PROFILE_IMAGE",
                "order" to 1
            )
        )

        viewModel.getPresignedUrl(imageInfo)
    }

    // Presigned URL 받은 후 S3 업로드
    LaunchedEffect(state.imageUploadState) {
        state.imageUploadState.let { uploadState ->
            Timber.d("ImageUploadState: isUploading=${uploadState.isUploading}, isSuccess=${uploadState.isSuccess}, uploadUrl=${uploadState.uploadUrl}, fileUrl=${uploadState.fileUrl}")

            if (isUploading) {
                // S3 업로드 시작
                if (!uploadState.isUploading &&
                    !uploadState.isSuccess &&
                    uploadState.uploadUrl != null) {

                    selectedImageUri?.let { uri ->
                        val file = uriToFile(context, uri)

                        if (file != null) {
                            Timber.d("Starting S3 upload for file: ${file.name}")
                            viewModel.uploadImageToS3(
                                file = file,
                                uploadUrl = uploadState.uploadUrl,
                                contentType = getContentType(context, uri),
                                order = 1
                            )
                        } else {
                            Timber.e("Failed to convert Uri to File")
                            isUploading = false
                            showError = true
                            errorMessage = "이미지 파일을 불러올 수 없습니다."
                        }
                    }
                }

                // 업로드 완료
                if (uploadState.isSuccess && uploadState.fileUrl != null) {
                    Timber.d("Upload success! fileUrl: ${uploadState.fileUrl}")
                    uploadComplete = true
                    isUploading = false

//                    viewModel.setProfileImage(uploadState.fileUrl)
                }
            }
        }
    }

    // 타임아웃 체크
    LaunchedEffect(isUploading) {
        if (isUploading) {
            kotlinx.coroutines.delay(10000)
            if (isUploading) {
                Timber.e("Upload timeout")
                isUploading = false
                showError = true
                errorMessage = "이미지 업로드 시간이 초과되었습니다."
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { startProfileImageUpload(it) }
    }

    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            startProfileImageUpload(uri)
        }
    }

    val isCompleteEnabled = !isNicknameChanged || state.isNicknameChecked == true

    ProfileSettingScreen(
        profileImageUrl = state.userInfo?.profileImageUrl,
        nickName = state.userInfo?.nickName ?: "",
        onBackClick = {
            isMarkedForDeletion = false
            goBack()
        },
        onCompleteClick = {
            // ✅ 완료 버튼: 모든 변경사항 적용
            if (isUploading) {
                if (!showUploadingToast) {
                    showUploadingToast = true
                    coroutineScope.launch {
                        delay(2000)
                        showUploadingToast = false
                    }
                }
                return@ProfileSettingScreen
            }
            // 1. 삭제가 예정되어 있으면 실제 삭제 실행
            if (isMarkedForDeletion) {
                val imageUrlToDelete = state.userInfo?.profileImageUrl

                if (!imageUrlToDelete.isNullOrEmpty()) {
                    val actualUrl = imageUrlToDelete.replace("/temp/", "/")

                    Timber.d("🔴 Deleting profile image on complete")
                    Timber.d("   Original: $imageUrlToDelete")
                    Timber.d("   Actual:   $actualUrl")

                    // S3에서 삭제
                    viewModel.deleteS3Image(actualUrl)
                    // 서버에 빈 URL 저장
                    viewModel.setProfileImage()
                }
            }
            // 2. 새 이미지 업로드가 있으면 서버에 저장
            else if (uploadComplete) {
                state.imageUploadState.fileUrl?.let {
                    viewModel.setProfileImage(it)
                }
            }

            // 3. 닉네임이 변경됐으면 등록 (성공 시 LaunchedEffect에서 goBack)
            if (isNicknameChanged && state.isNicknameChecked == true) {
                viewModel.registerNickname(nickname)
                return@ProfileSettingScreen
            }

            // 4. 화면 닫기
            goBack()
        },
        onProfileImageClick = {
            showProfilePhotoBottomSheet = true
        },
        onDuplicateCheckClick = {
            viewModel.getIsNicknameDuplicate(nickname)
        },
        onNickNameChange = { newNickname ->
            nickname = newNickname
            isNicknameChanged = newNickname != (state.userInfo?.nickName ?: "")
            viewModel.resetNicknameCheck()
            nicknameErrorMessage = ""
            nicknameSuccessMessage = ""
        },
        isUploading = isUploading,
        isNicknameCheckLoading = state.isNicknameCheckLoading,
        isMarkedForDeletion = isMarkedForDeletion,
        uploadComplete = uploadComplete,
        selectedImageUri = selectedImageUri,
        showError = showError,
        errorMessage = errorMessage,
        onDismissError = { showError = false },
        showUploadingToast = showUploadingToast,
        nicknameErrorMessage = nicknameErrorMessage,
        nicknameSuccessMessage = nicknameSuccessMessage,
        nickname = nickname,
        onOutsideClick = {
            validateNickname()
            focusManager.clearFocus()
        },
        onNicknameFocusChanged = { isFocused ->
            if (isFocused) {
                hasFocused = true
                if (state.isNicknameChecked == null) {
                    nicknameErrorMessage = ""
                    nicknameSuccessMessage = ""
                }
            }
        },
        onNicknameEnterPressed = {
            val isValid = validateNickname()
            if (isValid) {
                focusManager.clearFocus()
            }
        },
        isCompleteEnabled = isCompleteEnabled
    )

    if (showProfilePhotoBottomSheet) {
        ProfilePhotoBottomSheet(
            onDismiss = { showProfilePhotoBottomSheet = false },
            onSelectFromAlbum = {
                showProfilePhotoBottomSheet = false
                isMarkedForDeletion = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                    }
                    legacyGalleryLauncher.launch(intent)
                }
            },
            onSetDefaultImage = {
                showProfilePhotoBottomSheet = false
                isMarkedForDeletion = true
                selectedImageUri = null
                uploadComplete = false
            }
        )
    }

    if (showUploadingToast) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color(0xCC000000),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "이미지 업로드 중입니다…",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }
    }
}


@Composable
fun ProfileSettingScreen(
    showUploadingToast: Boolean = false,
    modifier: Modifier = Modifier,
    profileImageUrl: String? = null,
    nickname: String = "",
    nickName: String = "",
    onNickNameChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onDuplicateCheckClick: () -> Unit = {},
    isUploading: Boolean = false,
    isNicknameCheckLoading: Boolean = false,
    uploadComplete: Boolean = false,
    selectedImageUri: Uri? = null,
    showError: Boolean = false,
    errorMessage: String = "",
    onDismissError: () -> Unit = {},
    isMarkedForDeletion: Boolean = false,
    nicknameErrorMessage: String = "",
    nicknameSuccessMessage: String = "",
    onOutsideClick: () -> Unit = {},
    onNicknameFocusChanged: (Boolean) -> Unit = {},
    onNicknameEnterPressed: () -> Unit = {},
    isCompleteEnabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                onClick = { onOutsideClick() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileSettingColors.White)
        ) {
            // Header
            ProfileSettingHeader(
                onBackClick = onBackClick,
                onCompleteClick = onCompleteClick,
                isCompleteEnabled = isCompleteEnabled
            )

            Spacer(modifier = Modifier.height(68.dp))

            // Profile Image Section
            ProfileImageSection(
                profileImageUrl = profileImageUrl,
                selectedImageUri = selectedImageUri,
                onProfileImageClick = onProfileImageClick,
                isMarkedForDeletion = isMarkedForDeletion,  // ✅ 전달
                uploadComplete = uploadComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Nickname Input Field
            NicknameInputField(
                nickname = nickname,
                onNicknameChange = onNickNameChange,
                onDuplicateCheckClick = onDuplicateCheckClick,
                isLoading = isNicknameCheckLoading,
                errorMessage = nicknameErrorMessage,
                successMessage = nicknameSuccessMessage,
                modifier = Modifier.fillMaxWidth(),
                onFocusChanged = onNicknameFocusChanged,
                onEnterPressed = onNicknameEnterPressed
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // 업로드 중 로딩 오버레이
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "프로필 이미지 업로드 중...",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 에러 다이얼로그
        if (showError) {
            AlertDialog(
                onDismissRequest = onDismissError,
                title = {
                    Text(text = "업로드 실패")
                },
                text = {
                    Text(text = errorMessage)
                },
                confirmButton = {
                    TextButton(onClick = onDismissError) {
                        Text("확인")
                    }
                }
            )
        }
    }
}


@Composable
fun ProfileSettingHeader(
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit,
    isCompleteEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_chevron_left),
                contentDescription = "뒤로가기",
                tint = ProfileSettingColors.Neutral800
            )
        }

        // Title
        Text(
            text = "내 프로필 설정",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileSettingColors.Neutral800
        )

        // Complete Button
        Text(
            text = "완료",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isCompleteEnabled) ProfileSettingColors.Neutral800 else ProfileSettingColors.Neutral500,
            modifier = if (isCompleteEnabled) {
                Modifier.clickable(onClick = rememberThrottledClick { onCompleteClick() })
            } else {
                Modifier
            }
        )
    }
}

@Composable
fun ProfileImageSection(
    profileImageUrl: String?,
    selectedImageUri: Uri? = null,
    onProfileImageClick: () -> Unit,
    uploadComplete: Boolean = false,
    isMarkedForDeletion: Boolean = false,  // ✅ 추가
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.dp, ProfileSettingColors.Stroke001, CircleShape)
                    .background(ProfileSettingColors.Stroke001)
                    .clickable(onClick = rememberThrottledClick { onProfileImageClick() }),
                contentAlignment = Alignment.Center
            ) {
                when {
                    // ✅ 우선순위 1: 삭제 예정이면 기본 아이콘 표시
                    isMarkedForDeletion -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_profile_empty),
                            contentDescription = "프로필 이미지",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Unspecified
                        )
                    }
                    // ✅ 우선순위 2: 새로 선택한 이미지 (미리보기)
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "선택한 프로필 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // ✅ 우선순위 3: 기존 프로필 이미지
                    !profileImageUrl.isNullOrEmpty() -> {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "프로필 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // ✅ 우선순위 4: 기본 아이콘
                    else -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_profile_empty),
                            contentDescription = "프로필 이미지",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                // 업로드 완료 체크 표시
//                if (uploadComplete && selectedImageUri != null) {
//                    Box(
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                            .size(32.dp)
//                            .background(Color(0xFF4CAF50), CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = "업로드 완료",
//                            tint = Color.White,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
            }

            // 카메라 아이콘
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .clip(CircleShape)
                    .background(ProfileSettingColors.White)
                    .clickable(onClick = rememberThrottledClick { onProfileImageClick() }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "프로필 사진 변경",
                    modifier = Modifier.size(16.dp),
                    tint = ProfileSettingColors.Neutral800
                )
            }
        }
    }
}

@Composable
fun NicknameInputField(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onDuplicateCheckClick: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String = "",
    successMessage: String = "",
    modifier: Modifier = Modifier,
    onFocusChanged: (Boolean) -> Unit = {},
    onEnterPressed: () -> Unit = {}
) {

    Column(
        modifier = modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ProfileSettingColors.Background002)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "닉네임",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ProfileSettingColors.Neutral500,
                        lineHeight = 16.8.sp
                    )

                    BasicTextField(
                        value = nickname,
                        onValueChange = { if (it.length <= 10) onNicknameChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                onFocusChanged(focusState.isFocused)
                            },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (nickname.isEmpty()) ProfileSettingColors.Neutral500
                            else ProfileSettingColors.Neutral900,
                            lineHeight = 19.2.sp
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onEnterPressed() }),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box {
                                if (nickname.isEmpty()) {
                                    Text(
                                        text = "닉네임을 입력해 주세요.",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProfileSettingColors.Neutral500,
                                        lineHeight = 19.2.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (nickname.isNotEmpty() && !isLoading)
                                ProfileSettingColors.Neutral900
                            else
                                ProfileSettingColors.Neutral500
                        )
                        .clickable(
                            enabled = nickname.isNotEmpty() && !isLoading,
                            onClick = rememberThrottledClick { onDuplicateCheckClick() }
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "중복확인",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ProfileSettingColors.White,
                        lineHeight = 16.8.sp
                    )
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFFF0000),
                lineHeight = 16.8.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF00AA00),
                lineHeight = 16.8.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var fileName = "profile_${System.currentTimeMillis()}.jpg"

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                fileName = cursor.getString(displayNameIndex)
            }
        }
    }

    return fileName
}

private fun getContentType(context: Context, uri: Uri): String {
    return context.contentResolver.getType(uri) ?: "image/jpeg"
}

private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val contentResolver = context.contentResolver
        val fileName = getFileName(context, uri)
        val file = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        Timber.e(e, "Failed to convert Uri to File")
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePhotoBottomSheet(
    onDismiss: () -> Unit,
    onSelectFromAlbum: () -> Unit,
    onSetDefaultImage: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ProfileSettingColors.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 32.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "프로필 사진 설정",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ProfileSettingColors.Neutral900
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = rememberThrottledClick { onSelectFromAlbum() }),
                shape = RoundedCornerShape(12.dp),
                color = ProfileSettingColors.White,
                border = BorderStroke(1.dp, ProfileSettingColors.Stroke001)
            ) {
                Text(
                    text = "앨범에서 사진 선택",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = ProfileSettingColors.Neutral900,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = rememberThrottledClick { onSetDefaultImage() }),
                shape = RoundedCornerShape(12.dp),
                color = ProfileSettingColors.White,
                border = BorderStroke(1.dp, ProfileSettingColors.Stroke001)
            ) {
                Text(
                    text = "기본 이미지로 설정",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = ProfileSettingColors.Neutral900,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileSettingScreenPreview() {
    ProfileSettingScreen(
        nickName = "유지",
        onNickNameChange = {},
        onBackClick = {},
        onCompleteClick = {},
        onProfileImageClick = {},
        onDuplicateCheckClick = {},
        modifier = TODO(),
        profileImageUrl = TODO(),
    )
}