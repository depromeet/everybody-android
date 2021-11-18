package com.example.everybody_android.di

import android.content.Context
import com.example.everybody_android.pref.LocalStorage
import com.example.everybody_android.pref.SharedPrefStorage
import com.example.everybody_android.ui.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {

    @Singleton
    @Provides
    fun provideSharedPrefStorage(@ApplicationContext context: Context) : LocalStorage{
        return SharedPrefStorage(context = context)
    }
}