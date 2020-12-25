package com.petermunyao.mobileandroidchallenge.di

import android.content.Context
import androidx.room.Room
import com.petermunyao.mobileandroidchallenge.api.ApiInterface
import com.petermunyao.mobileandroidchallenge.database.CurrenciesDao
import com.petermunyao.mobileandroidchallenge.database.CurrencyDatabase
import com.petermunyao.mobileandroidchallenge.repository.LocalData
import com.petermunyao.mobileandroidchallenge.repository.RemoteData
import com.petermunyao.mobileandroidchallenge.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            appContext,
            CurrencyDatabase::class.java, "currency-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: CurrencyDatabase): CurrenciesDao {
        return database.getCurrenciesDao()
    }


    @Provides
    @Singleton
    fun provideLocalDataSource(currenciesDao: CurrenciesDao): LocalData {
        return LocalData(currenciesDao)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apiInterface: ApiInterface): RemoteData {
        return RemoteData(apiInterface)
    }

    @Provides
    @Singleton
    fun provideRepository(
        localData: LocalData,
        remoteData: RemoteData
    ): Repository {
        return Repository(localData, remoteData)
    }
}