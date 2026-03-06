package com.forday.app.core.util

import com.dayn.forday.R

object ImageCodeMapper {

    private val imageCodeMap = mapOf(
        "drawing.png" to R.drawable.hobby_draw,
        "gym.png" to R.drawable.hobby_health,
        "reading.png" to R.drawable.hobby_book,
        "music.png" to R.drawable.hobby_music,
        "running.png" to R.drawable.hobby_running,
        "cooking.png" to R.drawable.hobby_cook,
        "cafe.png" to R.drawable.hobby_cafe,
        "movie.png" to R.drawable.hobby_movie,
        "photo.png" to R.drawable.hobby_camera,
        "writing.png" to R.drawable.hobby_pencil
    )

    fun getDrawableResId(imageCode: String): Int {
        return imageCodeMap[imageCode] ?: R.drawable.hobbycard_gym
    }
}