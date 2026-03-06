package com.forday.app.domain.repository

import com.forday.app.domain.model.BlockUserDomain
import com.forday.app.domain.model.ReportUserDomain
import com.forday.app.domain.model.IsNicknameDuplicateDomain
import com.forday.app.domain.model.OnboardingDataDomain
import com.forday.app.domain.model.ProfileDomain
import com.forday.app.domain.model.ProfileImageDomain
import com.forday.app.domain.model.RegisterNicknameDomain
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun checkNicknameDuplicate(nickname: String): IsNicknameDuplicateDomain
    suspend fun registerNickname(nickname: String?): RegisterNicknameDomain
    suspend fun saveOnboardingData(selectedHobbyId: Long?, selectedHobbyName: String?, selectedMinutes: Int?, selectedPurpose: String?, selectedFrequency: Int?, selectedPeriod: Boolean)
    suspend fun getOnboardingData(): Flow<OnboardingDataDomain>
    suspend fun getUserInfo(userId: String? = null): ProfileDomain
    suspend fun setProfileImage(imageUrl: String?): ProfileImageDomain
    suspend fun getUserNickname(): Flow<String?>
    suspend fun saveNickname(nickname: String)
    suspend fun saveCreatedHobbyId(hobbyId: Long)
    suspend fun removeOnboardingData()
    suspend fun blockUser(userId: String): BlockUserDomain
    suspend fun reportUser(userId: String, reason: String): ReportUserDomain
}