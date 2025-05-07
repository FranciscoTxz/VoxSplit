package com.samaxz.voxsplitapp.di

import com.samaxz.voxsplitapp.data.network.ResultApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES) // connection wait time
        .readTimeout(5, TimeUnit.MINUTES)    // response wait time
        .writeTimeout(5, TimeUnit.MINUTES)   // write wait time
        .build()


    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://voxvox2.free.beeceptor.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // .baseUrl("http://10.0.2.2:8000/")
    // .baseUrl("https://voxvox2.free.beeceptor.com/")

    @Singleton
    @Provides
    fun provideQuoteApiClient(retrofit: Retrofit): ResultApiClient {
        return retrofit.create(ResultApiClient::class.java)

    }
}
