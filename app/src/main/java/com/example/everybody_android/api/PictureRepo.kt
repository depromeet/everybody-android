package com.def.everybody_android.api

import com.def.everybody_android.data.response.UploadPictureResponse
import com.def.everybody_android.di.ApiModule
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

class PictureRepo {

    interface PictureApi {
        @Multipart
        @POST("pictures")
        suspend fun uploadPicture(
            @PartMap map: Map<String, @JvmSuppressWildcards RequestBody>,
            @Part file: MultipartBody.Part
        ): UploadPictureResponse

        @DELETE("/pictures/{picture_id}")
        suspend fun deletePicture(@Path("picture_id") id: String): Response<Unit>
    }

    companion object {
        suspend fun uploadPicture(
            map: Map<String, RequestBody>,
            file: MultipartBody.Part
        ): UploadPictureResponse =
            ApiModule.provideApiPicture().uploadPicture(map, file)

        suspend fun deletePicture(id: String): Response<Unit> =
        ApiModule.provideApiPicture().deletePicture(id)
    }

}