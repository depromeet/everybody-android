package com.def.everybody_android.api

import com.def.everybody_android.data.response.AlbumResponse
import com.def.everybody_android.data.response.AlbumsResponse
import com.def.everybody_android.di.ApiModule
import com.def.everybody_android.dto.response.CreateAlbumResponse
import com.def.everybody_android.dto.response.MainFeedResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

class AlbumRepo {

    interface AlbumApi {
        @GET("/albums")
        suspend fun getAlbums(): AlbumsResponse

        @POST("/albums")
        suspend fun createAlbum(@Body map: Map<String, String>): AlbumsResponse.Album

        @PUT("/albums/{id}")
        suspend fun editAlbum(@Path("id") id: String, @Body map: Map<String, String>): Response<ResponseBody>

        @DELETE("/albums/{id}")
        suspend fun deleteAlbum(@Path("id") id: String): Response<ResponseBody>

        @GET("/albums/{id}")
        suspend fun getAlbum(@Path("id") id: String): AlbumResponse

        @GET("/albums")
        suspend fun getMainFeed(): List<MainFeedResponse>

        @POST("/albums")
        suspend fun createAlbums(@Body name: Map<String, String>): CreateAlbumResponse

        @Streaming
        @POST("/videos/download")
        suspend fun downloadAlbums(@Body map: HashMap<String, Any>): Response<ResponseBody>
    }

    companion object {
        suspend fun getAlbums(): AlbumsResponse =
            ApiModule.provideApiAlbum().getAlbums()

        suspend fun getAlbum(id: String): AlbumResponse =
            ApiModule.provideApiAlbum().getAlbum(id)

        suspend fun createAlbum(map: Map<String, String>): AlbumsResponse.Album =
            ApiModule.provideApiAlbum().createAlbum(map)

        suspend fun editAlbum(id: String, map: Map<String, String>): Response<ResponseBody> =
            ApiModule.provideApiAlbum().editAlbum(id, map)

        suspend fun deleteAlbum(id: String): Response<ResponseBody> =
            ApiModule.provideApiAlbum().deleteAlbum(id)

        suspend fun getMainFeed(): List<MainFeedResponse> =
            ApiModule.provideApiAlbum().getMainFeed()

        suspend fun createAlbums(name: Map<String, String>): CreateAlbumResponse =
            ApiModule.provideApiAlbum().createAlbums(name)

        suspend fun downloadAlbums(map: HashMap<String, Any>): Response<ResponseBody> =
            ApiModule.provideApiAlbum().downloadAlbums(map)
    }

}