package com.petermunyao.mobileandroidchallenge.di

import com.petermunyao.mobileandroidchallenge.api.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RetrofitModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(Interceptor {
                val originalRequest = it.request()
                val originalUrl = originalRequest.url()
                val amendedUrl = originalUrl.newBuilder()
                    .addQueryParameter("access_key", "13cc221ea0e5c04caa708aa2a590fff3")
                    .build()
                val newRequest = originalRequest.newBuilder().url(amendedUrl).build()

                return@Interceptor it.proceed(newRequest)
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideApiInterface(okHttpClient: OkHttpClient): ApiInterface {
        return Retrofit.Builder()
            .baseUrl("http://api.currencylayer.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiInterface::class.java)
    }
}