package com.example.everybody_android.api

import com.example.everybody_android.di.ApiModule
import com.example.everybody_android.dto.AlarmData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

class AlarmRepo {

    interface AlarmApi{

        @GET("/notification-configs/me")
        suspend fun getAlarm() : AlarmData

        @PUT("/notification-configs/me")
        suspend fun setAlarm(
            @Body alarmData : AlarmData
        ) : AlarmData
    }

    companion object{
        suspend fun getAlarm() : AlarmData =
            ApiModule.provideApiAlarm().getAlarm()

        suspend fun setAlarm(alarmData: AlarmData) =
            ApiModule.provideApiAlarm().setAlarm(alarmData)
    }

}