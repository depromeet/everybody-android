package com.example.everybody_android.api

import com.example.everybody_android.data.response.UploadPictureResponse
import com.example.everybody_android.di.ApiModule
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

class PictureRepo {

    interface PictureApi {
        @Multipart
        @POST("pictures")
        suspend fun uploadPicture(
            @PartMap map: Map<String, @JvmSuppressWildcards RequestBody>,
            @Part file: MultipartBody.Part
        ): UploadPictureResponse
    }

    companion object {
        suspend fun uploadPicture(
            map: Map<String, RequestBody>,
            file: MultipartBody.Part
        ): UploadPictureResponse =
            ApiModule.provideApiPicture().uploadPicture(map, file)
    }

}