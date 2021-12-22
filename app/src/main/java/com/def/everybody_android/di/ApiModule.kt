package com.def.everybody_android.di

import com.def.everybody_android.api.AlarmRepo
import com.def.everybody_android.api.AlbumRepo.AlbumApi
import com.def.everybody_android.api.PictureRepo
import com.def.everybody_android.api.SignRepo
import com.def.everybody_android.api.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL = "https://api.noonbody.me"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addInterceptor {
                it.proceed(
                    it.request().newBuilder().addHeader(
                        "Authorization",
                        "Bearer ${HiltApplication.token}"
                    ).build()
                )
            }
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(providesOkHttpClient(providesHttpLoggingInterceptor()))
        .build()

    @Singleton
    @Provides
    fun provideApiAlbum(): AlbumApi = provideRetrofit().create(AlbumApi::class.java)

    @Singleton
    @Provides
    fun provideApiPicture(): PictureRepo.PictureApi =
        provideRetrofit().create(PictureRepo.PictureApi::class.java)

    @Singleton
    @Provides
    fun provideApiUser(): UserRepo.UserApi = provideRetrofit().create(UserRepo.UserApi::class.java)

    @Singleton
    @Provides
    fun provideApiSign(): SignRepo.SignApi = provideRetrofit().create(SignRepo.SignApi::class.java)

    @Singleton
    @Provides
    fun provideApiAlarm(): AlarmRepo.AlarmApi = provideRetrofit().create(AlarmRepo.AlarmApi::class.java)

}