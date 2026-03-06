package com.forday.app.core.util

import com.dayn.forday.R

/**
 * 스티커 파일명을 Drawable Resource ID로 변환
 *
 * 서버: "smile.jpg" → 앱: R.drawable.ic_sticker_smile
 *
 * @param stickerFileName 서버에서 받은 파일명 (예: "smile.jpg", "sad.jpg")
 * @return Drawable Resource ID
 */
fun getStickerDrawableResId(stickerFileName: String): Int {
    // 확장자 제거 (.jpg, .png 등)
    val baseName = stickerFileName.substringBeforeLast(".").lowercase()

    return when (baseName) {
        "smile" -> R.drawable.ic_sticker_smile
        "sad" -> R.drawable.ic_sticker_sad
        "laugh" -> R.drawable.ic_sticker_laugh
        "angry" -> R.drawable.ic_sticker_angry
        else -> R.drawable.ic_main_character2  // 기본 캐릭터
    }
}

/**
 * 다음에 붙일 빈 스티커 이미지
 */
fun getEmptyStickerDrawableResId(): Int {
    return R.drawable.ic_empty_sticker
}

/**
 * 회색 빈 스티커 이미지 (아직 기록 안 된 칸)
 */
fun getGrayStickerDrawableResId(): Int {
    return R.drawable.ic_main_charac
}