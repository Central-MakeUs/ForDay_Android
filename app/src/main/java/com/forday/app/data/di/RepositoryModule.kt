package com.forday.app.data.di

import com.forday.app.data.impl.AppVersionPolicyRepositoryImpl
import com.forday.app.data.impl.AuthRepositoryImpl
import com.forday.app.data.impl.HobbyRepositoryImpl
import com.forday.app.data.impl.FileRepositoryImpl
import com.forday.app.data.impl.RoutineRepositoryImpl
import com.forday.app.data.impl.S3UploadRepositoryImpl
import com.forday.app.data.impl.SosikRepositoryImpl
import com.forday.app.data.impl.UserRepositoryImpl
import com.forday.app.domain.repository.AppVersionPolicyRepository
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.domain.repository.HobbyRepository
import com.forday.app.domain.repository.FileRepository
import com.forday.app.domain.repository.RoutineRepository
import com.forday.app.domain.repository.S3UploadRepository
import com.forday.app.domain.repository.SosikRepository
import com.forday.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppVersionPolicyRepository(repository: AppVersionPolicyRepositoryImpl): AppVersionPolicyRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindHobbyRepository(repository: HobbyRepositoryImpl): HobbyRepository

    @Binds
    @Singleton
    abstract fun bindFileRepository(repository: FileRepositoryImpl): FileRepository

    @Binds
    @Singleton
    abstract fun bindS3UploadRepository(repository: S3UploadRepositoryImpl): S3UploadRepository

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(repository: RoutineRepositoryImpl): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindSosikRepository(repository: SosikRepositoryImpl): SosikRepository
}