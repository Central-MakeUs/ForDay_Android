package com.forday.app.remote.di

import com.forday.app.data.remote.AppVersionPolicyDataSource
import com.forday.app.data.remote.AuthDataSource
import com.forday.app.data.remote.HobbyDataSource
import com.forday.app.data.remote.FileDataSource
import com.forday.app.data.remote.RoutineDataSource
import com.forday.app.data.remote.SosikDataSource
import com.forday.app.data.remote.UserDataSource
import com.forday.app.remote.impl.AppVersionPolicyDataSourceImpl
import com.forday.app.remote.impl.AuthDataSourceImpl
import com.forday.app.remote.impl.HobbyDataSourceImpl
import com.forday.app.remote.impl.FileDataSourceImpl
import com.forday.app.remote.impl.RoutineDataSourceImpl
import com.forday.app.remote.impl.SosikDataSourceImpl
import com.forday.app.remote.impl.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAppVersionPolicyRemoteDataSource(source: AppVersionPolicyDataSourceImpl): AppVersionPolicyDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(source: AuthDataSourceImpl): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(source: UserDataSourceImpl): UserDataSource

    @Binds
    @Singleton
    abstract fun bindHobbyRemoteDataSource(source: HobbyDataSourceImpl): HobbyDataSource

    @Binds
    @Singleton
    abstract fun bindFileRemoteDataSource(source: FileDataSourceImpl): FileDataSource

    @Binds
    @Singleton
    abstract fun bindRoutineRemoteDataSource(source: RoutineDataSourceImpl): RoutineDataSource

    @Binds
    @Singleton
    abstract fun bindSosikRemoteDataSource(source: SosikDataSourceImpl): SosikDataSource
}