package com.forday.app.data.impl

import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.data.remote.UserDataSource
import com.forday.app.domain.model.BlockUserDomain
import com.forday.app.domain.model.ReportUserDomain
import com.forday.app.domain.model.IsNicknameDuplicateDomain
import com.forday.app.domain.model.OnboardingDataDomain
import com.forday.app.domain.model.ProfileDomain
import com.forday.app.domain.model.ProfileImageDomain
import com.forday.app.domain.model.RegisterNicknameDomain
import com.forday.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userLocalDataSource: UserLocalDataSource
): UserRepository {

    override suspend fun checkNicknameDuplicate(nickname: String): IsNicknameDuplicateDomain =
        userDataSource.checkNicknameDuplicate(nickname).toDomain()


    override suspend fun registerNickname(nickname: String?): RegisterNicknameDomain =
        userDataSource.registerNickname(nickname).toDomain()

    override suspend fun saveOnboardingData(selectedHobbyId: Long?, selectedHobbyName: String?, selectedMinutes: Int?, selectedPurpose: String?, selectedFrequency: Int?, selectedPeriod: Boolean) {
        userLocalDataSource.saveOnboardingData(
            selectedHobbyId = selectedHobbyId,
            selectedHobbyName = selectedHobbyName,
            selectedMinutes = selectedMinutes,
            selectedPurpose = selectedPurpose,
            selectedFrequency = selectedFrequency,
            selectedPeriod = selectedPeriod
        )
    }

    override suspend fun getOnboardingData(): Flow<OnboardingDataDomain> =
        userLocalDataSource.getOnboardingData().map { it.toDomain() }

    override suspend fun getUserInfo(userId: String?): ProfileDomain =
        userDataSource.getUserInfo(userId).toDomain()

    override suspend fun setProfileImage(imageUrl: String?): ProfileImageDomain =
        userDataSource.setProfileImage(imageUrl).toDomain()

    override suspend fun getUserNickname(): Flow<String?> =
        userLocalDataSource.getUserNickname()

    override suspend fun saveNickname(nickname: String) =
        userLocalDataSource.saveUserNickname(nickname)

    override suspend fun saveCreatedHobbyId(hobbyId: Long) {
        userLocalDataSource.saveCreatedHobbyId(hobbyId)
        Timber.e("@@@@@@@@@@@@@@@@@@@saveCreatedHobbyId : "+hobbyId)
    }

    override suspend fun removeOnboardingData() {
        userLocalDataSource.removeOnboardingData()
    }

    override suspend fun blockUser(userId: String): BlockUserDomain =
        userDataSource.blockUser(userId).toDomain()

    override suspend fun reportUser(userId: String, reason: String): ReportUserDomain =
        userDataSource.reportUser(userId, reason).toDomain()

}