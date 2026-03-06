package com.forday.app.data.remote

import com.forday.app.data.model.BlockUserEntity
import com.forday.app.data.model.IsNicknameDuplicateEntity
import com.forday.app.data.model.ProfileEntity
import com.forday.app.data.model.ProfileImageEntity
import com.forday.app.data.model.RegisterNicknameEntity
import com.forday.app.data.model.ReportUserEntity
import com.forday.app.data.model.UserDataEntity

interface UserDataSource {
    suspend fun checkNicknameDuplicate(nickname: String): IsNicknameDuplicateEntity
    suspend fun registerNickname(nickname: String?): RegisterNicknameEntity
    suspend fun getUserInfo(userId: String? = null): ProfileEntity
    suspend fun setProfileImage(imageUrl: String?): ProfileImageEntity
    suspend fun blockUser(userId: String): BlockUserEntity
    suspend fun reportUser(userId: String, reason: String): ReportUserEntity
}