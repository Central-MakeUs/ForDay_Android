package com.forday.app.presentation.mypage.hobbyphotosetting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.dayn.forday.R
import com.forday.app.presentation.mypage.main.getHobbyIconByName
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.mypage.MyPageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

// 색상 정의
object HobbyPhotoColors {
    val Neutral900 = Color(0xFF1E1E1E)
    val Neutral800 = Color(0xFF3A3A3A)
    val Neutral600 = Color(0xFF7A7A7A)
    val Neutral500 = Color(0xFF9E9E9E)
    val Neutral400 = Color(0xFFB5B5B5)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF222222)
    val Background001 = Color(0xFFFFFFFF)
    val Stroke001 = Color(0xFFE5E5E5)
    val Action001 = Color(0xFFFF9447)
    val DimBackground = Color(0x80000000)  // 50% black
    val ToastBackground = Color(0xAD000000)  // 68% black
    val ToastSuccess = Color(0xFFD9F7E5)
}

// 데이터 모델
data class HobbyCategory(
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    val isActive: Boolean = true
)

data class Photo(
    val id: String,
    val imageUrl: String,
    val hobbyId: String,
    val quote: String = "",
    val hasGradient: Boolean = false,
    val gradientColors: List<Color> = emptyList()
)

enum class PhotoSourceType {
    ALBUM,      // 앨범에서 사진 선택
    ACTIVITY    // 내 활동 중에서 사진 선택
}

@Composable
fun HobbyPhotoManagementScreenRoot(
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit,
    viewModel: MyPageViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 상태 관리
    var selectedHobbyForUpload by remember { mutableStateOf<HobbyCategory?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadComplete by remember { mutableStateOf(false) }

    // ViewModel에서 초기 데이터 로드
    LaunchedEffect(Unit) {
        viewModel.getUserInfo(null)
        viewModel.getUsersProgressHobbyTabs(null)
        viewModel.getUserFeedList(
            hobbyIds = emptyList(),
            lastRecordId = null,
            feedSize = 100
        )
    }

    // ✅ 이미지 업로드 시작 함수
    fun startHobbyPhotoUpload(uri: Uri, hobby: HobbyCategory) {
        selectedImageUri = uri
        selectedHobbyForUpload = hobby
        isUploading = true
        uploadComplete = false

        Timber.d("Starting hobby photo upload for: ${hobby.name}")

        // Presigned URL 요청
        val imageInfo = listOf(
            mapOf(
                "fileName" to getFileName(context, uri),
                "contentType" to getContentType(context, uri),
                "usage" to "COVER_IMAGE",
                "order" to 1
            )
        )

        viewModel.getPresignedUrl(imageInfo)
    }

    // ✅ Photo Picker (Android 13+) - 단일 선택
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        Timber.d("Photo Picker result: $uri")
        uri?.let {
            selectedHobbyForUpload?.let { hobby ->
                startHobbyPhotoUpload(it, hobby)
            }
        }
    }

    // ✅ Legacy Gallery (Android 12 이하) - 단일 선택
    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Timber.d("Legacy Gallery result: ${result.data?.data}")
        result.data?.data?.let { uri ->
            selectedHobbyForUpload?.let { hobby ->
                startHobbyPhotoUpload(uri, hobby)
            }
        }
    }

    // ✅ Presigned URL 받은 후 S3 업로드
    LaunchedEffect(state.imageUploadState) {
        state.imageUploadState?.let { uploadState ->
            if (isUploading) {
                // uploadUrl이 있고 아직 업로드 시작 전인 경우
                if (!uploadState.isUploading &&
                    !uploadState.isSuccess &&
                    uploadState.uploadUrl != null) {

                    Timber.d("Presigned URL received: ${uploadState.uploadUrl}")

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
                            Timber.e("Failed to convert URI to File: $uri")
                            isUploading = false
                        }
                    }
                }

                // 업로드 완료 시
                if (uploadState.isSuccess && uploadState.fileUrl != null) {
                    Timber.d("Upload successful! File URL: ${uploadState.fileUrl}")
                    uploadComplete = true
                    isUploading = false

                    // 해당 취미의 대표사진 설정
                    selectedHobbyForUpload?.let { hobby ->
                        Timber.d("Setting hobby thumbnail for: ${hobby.name}")
                        viewModel.setHobbyMainImage(
                            hobbyId = hobby.id.toLongOrNull(),
                            imageUrl = uploadState.fileUrl,
                            recordId = null
                        )
                    }

                    // 3초 후 완료 상태 초기화
                    kotlinx.coroutines.delay(3000)
                    uploadComplete = false
                }
            }
        }
    }

    // 취미 목록
    val hobbies = state.userHobbyTabUiModel?.hobbyItems?.map { hobbyUiModel ->
        HobbyCategory(
            id = hobbyUiModel.hobbyId.toString(),
            name = hobbyUiModel.hobbyName ?: "",
            thumbnailUrl = hobbyUiModel.thumbnail ?: "",
            isActive = hobbyUiModel.status == "IN_PROGRESS"
        )
    } ?: emptyList()

    // 피드 목록 - selectedHobbyForUpload 사용
    val photos = state.userFeedUiModel?.feedList?.map { feedUiModel ->
        Photo(
            id = feedUiModel.recordId.toString(),
            imageUrl = feedUiModel.url,
            hobbyId = selectedHobbyForUpload?.id ?: "",  // ✅ 선택된 취미의 ID
            quote = feedUiModel.memo ?: "",
            hasGradient = feedUiModel.url.isEmpty(),
            gradientColors = when (feedUiModel.stickerIconRes) {
                com.dayn.forday.R.drawable.ic_sticker_smile -> listOf(
                    androidx.compose.ui.graphics.Color(0xFFFFE6D1),
                    androidx.compose.ui.graphics.Color(0xFFF4A261)
                )
                com.dayn.forday.R.drawable.ic_sticker_sad -> listOf(
                    androidx.compose.ui.graphics.Color(0xFFDDF2D8),
                    androidx.compose.ui.graphics.Color(0xFFA8D8A2),
                    androidx.compose.ui.graphics.Color(0xFFDDF2D8)
                )
                com.dayn.forday.R.drawable.ic_sticker_laugh -> listOf(
                    androidx.compose.ui.graphics.Color(0xFFC9DBFF),
                    androidx.compose.ui.graphics.Color(0xFF8FB3FF),
                    androidx.compose.ui.graphics.Color(0xFFC9DBFF)
                )
                com.dayn.forday.R.drawable.ic_sticker_angry -> listOf(
                    androidx.compose.ui.graphics.Color(0xFFFFF4CC),
                    androidx.compose.ui.graphics.Color(0xFFFFD966),
                    androidx.compose.ui.graphics.Color(0xFFFFF4CC)
                )
                else -> emptyList()
            }
        )
    } ?: emptyList()

    HobbyPhotoManagementScreen(
        hobbies = hobbies,
        photos = photos,
        onBackClick = {
            // ✅ 뒤로가기 시 전체 피드 재로드
            selectedHobbyForUpload = null
            viewModel.getUserFeedList(
                hobbyIds = emptyList(),
                lastRecordId = null,
                feedSize = 100,
                userId = null
            )
            onBackClick()
        },
        onCompleteClick = onCompleteClick,
        onPhotoSourceSelected = { hobby, sourceType ->
            when (sourceType) {
                PhotoSourceType.ALBUM -> {
                    Timber.d("Album selected for hobby: ${hobby.name}")
                    selectedHobbyForUpload = hobby

                    // ✅ 이제 photoPickerLauncher가 정의되어 있음
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
                }
                PhotoSourceType.ACTIVITY -> {
                    Timber.d("Activity selection for hobby: ${hobby.name}")

                    viewModel.getUserFeedList(
                        hobbyIds = listOf(hobby.id.toIntOrNull()),
                        lastRecordId = null,
                        feedSize = 12,
                        userId = null
                    )
                }
            }
        },
        onHobbySelected = { hobby ->
            selectedHobbyForUpload = hobby
        },
        onExitSelectionMode = {
            selectedHobbyForUpload = null
            viewModel.getUserFeedList(
                hobbyIds = emptyList(),
                lastRecordId = null,
                feedSize = 100,
                userId = null
            )
        },
        onCompletePhotoSelection = { hobbyId, recordId ->
            viewModel.setHobbyMainImage(
                hobbyId = hobbyId.toLongOrNull(),
                imageUrl = null,
                recordId = recordId.toLongOrNull()
            )
        },
        isUploading = isUploading,
        uploadComplete = uploadComplete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HobbyPhotoManagementScreen(
    hobbies: List<HobbyCategory>,
    photos: List<Photo>,
    onBackClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {},
    onPhotoSourceSelected: (HobbyCategory, PhotoSourceType) -> Unit = { _, _ -> },
    isUploading: Boolean = false,
    onHobbySelected: (HobbyCategory?) -> Unit = {},
    onExitSelectionMode: () -> Unit = {},
    onCompletePhotoSelection: (hobbyId: String, recordId: String) -> Unit = { _, _ -> },
    uploadComplete: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedHobby by remember { mutableStateOf<HobbyCategory?>(null) }
    var showPhotoSourceBottomSheet by remember { mutableStateOf(false) }
    var showPhotoSelectionMode by remember { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<String?>(null) }  // ✅ Set → String? (단일 선택)
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HobbyPhotoColors.Background001)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            HobbyPhotoHeader(
                title = "취미 대표사진 관리",
                onBackClick = {
                    if (showPhotoSelectionMode) {
                        showPhotoSelectionMode = false
                        selectedPhoto = null
                        onExitSelectionMode()
                    } else {
                        onBackClick()
                    }
                },
                onCompleteClick = {
                    if (showPhotoSelectionMode && selectedPhoto != null) {
                        selectedHobby?.let { hobby ->
                            onCompletePhotoSelection(hobby.id, selectedPhoto!!)
                        }
                        showPhotoSelectionMode = false
                        toastMessage = "${selectedHobby?.name} 대표사진 변경 완료!"
                        showToast = true
                        scope.launch {
                            delay(3000)
                            showToast = false
                            onCompleteClick()
                        }
                    } else {
                        onCompleteClick()
                    }
                }
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 18.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hobby Categories
                HobbyCategoriesRow(
                    hobbies = if (showPhotoSelectionMode && selectedHobby != null) {
                        listOf(selectedHobby!!)
                    } else {
                        hobbies
                    },
                    onCameraClick = { hobby ->
                        selectedHobby = hobby
                        showPhotoSourceBottomSheet = true
                        onHobbySelected(hobby)
                    }
                )

                // Photo Grid
                PhotoGrid(
                    photos = if (showPhotoSelectionMode && selectedHobby != null) {
                        photos.filter { it.hobbyId == selectedHobby!!.id }
                    } else {
                        photos
                    },
                    isSelectionMode = showPhotoSelectionMode,
                    selectedPhoto = selectedPhoto,  // ✅ 수정
                    onPhotoClick = { photo ->
                        if (showPhotoSelectionMode) {
                            // ✅ 단일 선택: 같은 사진 클릭 시 선택 해제, 다른 사진 클릭 시 선택 변경
                            selectedPhoto = if (selectedPhoto == photo.id) {
                                null
                            } else {
                                photo.id
                            }
                        }
                    }
                )
            }
        }

        // ✅ 업로드 중 로딩 오버레이
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HobbyPhotoColors.DimBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = HobbyPhotoColors.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "대표사진 업로드 중...",
                        color = HobbyPhotoColors.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Toast Message
        AnimatedVisibility(
            visible = showToast || uploadComplete,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 112.dp)
        ) {
            ToastMessage(
                message = if (uploadComplete) {
                    "${selectedHobby?.name} 대표사진 변경 완료!"
                } else {
                    toastMessage
                }
            )
        }
    }

    // Photo Source Selection Bottom Sheet
    if (showPhotoSourceBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSourceBottomSheet = false },
            sheetState = sheetState,
            containerColor = HobbyPhotoColors.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null
        ) {
            PhotoSourceSelectionBottomSheet(
                hobbyName = selectedHobby?.name ?: "",
                onAlbumClick = {
                    showPhotoSourceBottomSheet = false
                    selectedHobby?.let {
                        onPhotoSourceSelected(it, PhotoSourceType.ALBUM)
                    }
                },
                onActivityClick = {
                    showPhotoSourceBottomSheet = false
                    showPhotoSelectionMode = true
                    selectedHobby?.let {
                        onPhotoSourceSelected(it, PhotoSourceType.ACTIVITY)
                    }
                }
            )
        }
    }
}

@Composable
fun HobbyPhotoHeader(
    title: String,
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = rememberThrottledClick(onClick = onBackClick),
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "뒤로가기",
                tint = HobbyPhotoColors.Neutral800
            )
        }

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HobbyPhotoColors.Neutral800,
            textAlign = TextAlign.Center
        )

        TextButton(
            onClick = rememberThrottledClick(onClick = onCompleteClick),
            modifier = Modifier.height(44.dp)
        ) {
            Text(
                text = "완료",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = HobbyPhotoColors.Neutral800
            )
        }
    }
}

@Composable
fun HobbyCategoriesRow(
    hobbies: List<HobbyCategory>,
    onCameraClick: (HobbyCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        hobbies.forEach { hobby ->
            HobbyCategoryItem(
                hobby = hobby,
                onCameraClick = rememberThrottledClick {
                    onCameraClick(hobby)
                },
            )
        }
    }
}

@Composable
fun HobbyCategoryItem(
    hobby: HobbyCategory,
    onCameraClick: () -> Unit
) {
    val hasValidThumbnail = hobby.thumbnailUrl.isNotEmpty()
    val hobbyIcon = getHobbyIconByName(hobby.name)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(48.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.dp, HobbyPhotoColors.Stroke001, CircleShape)
                    .background(
                        color = when {
                            hasValidThumbnail -> Color.LightGray.copy(alpha = if (hobby.isActive) 1f else 0.4f)
                            hobbyIcon != null -> Color.White
                            else -> Color.LightGray.copy(alpha = if (hobby.isActive) 1f else 0.4f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    hasValidThumbnail -> {
                        SubcomposeAsyncImage(
                            model = hobby.thumbnailUrl,
                            contentDescription = hobby.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                                    contentDescription = "로딩 중",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            },
                            error = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                                    contentDescription = "로딩 실패",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        )
                        if (!hobby.isActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White.copy(alpha = 0.6f))
                            )
                        }
                    }
                    hobbyIcon != null -> {
                        Icon(
                            painter = painterResource(id = hobbyIcon),
                            contentDescription = hobby.name,
                            modifier = Modifier.size(24.dp),
                            tint = if (hobby.isActive) Color(0xFFFF9447) else Color(0xFFFF9447).copy(alpha = 0.4f)
                        )
                        if (!hobby.isActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White.copy(alpha = 0.6f))
                            )
                        }
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_placeholder),
                            contentDescription = "기본 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Camera Icon Button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(HobbyPhotoColors.Neutral800)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = rememberThrottledClick { onCameraClick() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_camera),
                    contentDescription = "사진 설정",
                    modifier = Modifier.size(16.dp),
                    tint = HobbyPhotoColors.White
                )
            }
        }

        // Hobby Name
        Text(
            text = hobby.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (hobby.isActive) {
                HobbyPhotoColors.Neutral800
            } else {
                HobbyPhotoColors.Neutral400
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PhotoGrid(
    photos: List<Photo>,
    isSelectionMode: Boolean,
    selectedPhoto: String?,
    onPhotoClick: (Photo) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Photo Count
        Text(
            text = "${photos.size}개",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = HobbyPhotoColors.Neutral500,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        //TODO 활동기록에서 이미지 선택해서 대표사진 설정하는거 로직 어떻게 되는지 다시 확인하기.그리고 이미지 없을때도 처리되어야 함)
        if (photos.isEmpty() && isSelectionMode) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.box_img),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp),
                    contentScale = ContentScale.Fit,
                    alpha = 0.3f
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(140.dp))
                    Text(
                        text = "이 취미의 활동기록이 없어요.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HobbyPhotoColors.Neutral900,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "활동을 기록한 후 대표사진을 설정해주세요.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = HobbyPhotoColors.Neutral600,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Photo Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(photos) { photo ->
                    PhotoGridItem(
                        photo = photo,
                        isSelectionMode = isSelectionMode,
                        isSelected = selectedPhoto == photo.id,
                        onClick = { onPhotoClick(photo) }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoGridItem(
    photo: Photo,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(106f / 128f)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = rememberThrottledClick { onClick() }
            )
    ) {
        // Photo Image or Gradient
        if (photo.hasGradient && photo.gradientColors.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(colors = photo.gradientColors)
                    )
            ) {
                // Quote Text
                if (photo.quote.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "\"",
                            fontSize = 16.sp,
                            color = HobbyPhotoColors.White
                        )
                        Text(
                            text = photo.quote,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = HobbyPhotoColors.White,
                            maxLines = 2
                        )
                    }
                }
            }
        } else {
            AsyncImage(
                model = photo.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // White dim overlay (선택 모드에서는 적용 안 함)
        if (!isSelectionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.4f))
            )
        }

        // ✅ Selection Mode Checkbox (수정)
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(end = 8.dp)
                    .size(20.dp)
                    .align(Alignment.TopEnd)

            ) {
                Icon(
                    painter = painterResource(
                        if (isSelected) R.drawable.ic_check_after else R.drawable.ic_check_before
                    ),
                    contentDescription = if (isSelected) "선택됨" else "선택 안됨",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    tint = Color.Unspecified  // ✅ 아이콘 원본 색상 사용
                )
            }
        }
    }
}
@Composable
fun PhotoSourceSelectionBottomSheet(
    hobbyName: String,
    onAlbumClick: () -> Unit,
    onActivityClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 40.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Text(
            text = "$hobbyName 대표사진 설정",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HobbyPhotoColors.Neutral900,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        // Options
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Album Option
            PhotoSourceOptionCard(
                text = "앨범에서 사진 선택",
                onClick = onAlbumClick
            )

            // Activity Option
            PhotoSourceOptionCard(
                text = "내 활동 중에서 사진 선택",
                onClick = onActivityClick
            )
        }
    }
}

@Composable
fun PhotoSourceOptionCard(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = rememberThrottledClick { onClick() }
            ),
        shape = RoundedCornerShape(12.dp),
        color = HobbyPhotoColors.White,
        border = BorderStroke(1.dp, HobbyPhotoColors.Stroke001)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),  // ✅ 위아래 16.dp, 좌우 16.dp
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontWeight = FontWeight.Medium,
                color = HobbyPhotoColors.Neutral800
            )
        }
    }
}

@Composable
fun ToastMessage(message: String) {
    Surface(
        modifier = Modifier
            .width(320.dp)
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = HobbyPhotoColors.ToastBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(HobbyPhotoColors.ToastSuccess),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFF00C853)
                )
            }

            // Message
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = HobbyPhotoColors.White
            )
        }
    }
}

// 유틸리티 함수들
private fun getFileName(context: Context, uri: Uri): String {
    var fileName = "hobby_thumbnail_${System.currentTimeMillis()}.jpg"

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

// Preview
@Preview
@Composable
fun PreviewHobbyPhotoManagementScreen() {
    val sampleHobbies = listOf(
        HobbyCategory(id = "1", name = "독서", thumbnailUrl = "", isActive = true),
        HobbyCategory(id = "2", name = "사진촬영", thumbnailUrl = "", isActive = true),
        HobbyCategory(id = "3", name = "요리", thumbnailUrl = "", isActive = false)
    )

    val samplePhotos = listOf(
        Photo(id = "1", imageUrl = "", hobbyId = "1"),
        Photo(id = "2", imageUrl = "", hobbyId = "1", hasGradient = true,
            gradientColors = listOf(Color(0xFFC9DBFF), Color(0xFF8FB3FF), Color(0xFFC9DBFF)),
            quote = "오늘은 어쩌고 저쩌고 어쩌고 저쩌고 어쩌고 저쩌고"),
        Photo(id = "3", imageUrl = "", hobbyId = "1"),
        Photo(id = "4", imageUrl = "", hobbyId = "1", hasGradient = true,
            gradientColors = listOf(Color(0xFFDDF2D8), Color(0xFFA8D8A2), Color(0xFFDDF2D8)),
            quote = "오늘은 어쩌고 저쩌고 어쩌고 저쩌고 어쩌고 저쩌고"),
        Photo(id = "5", imageUrl = "", hobbyId = "2"),
        Photo(id = "6", imageUrl = "", hobbyId = "2"),
        Photo(id = "7", imageUrl = "", hobbyId = "1"),
        Photo(id = "8", imageUrl = "", hobbyId = "1"),
        Photo(id = "9", imageUrl = "", hobbyId = "1", hasGradient = true,
            gradientColors = listOf(Color(0xFFFFE6D1), Color(0xFFF4A261))),
        Photo(id = "10", imageUrl = "", hobbyId = "1"),
        Photo(id = "11", imageUrl = "", hobbyId = "1"),
        Photo(id = "12", imageUrl = "", hobbyId = "1", hasGradient = true,
            gradientColors = listOf(Color(0xFFDDF2D8), Color(0xFFA8D8A2), Color(0xFFDDF2D8)),
            quote = "오늘은 어쩌고 저쩌고 어쩌고 저쩌고 어쩌고 저쩌고")
    )

    ForDayTheme {
        HobbyPhotoManagementScreen(
            hobbies = sampleHobbies,
            photos = samplePhotos
        )
    }
}