package com.example.everybody_android.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.di.ApiModule
import com.example.everybody_android.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel(){
    val feedStatus = 0

}