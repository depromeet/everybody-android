package com.example.everybody_android.api

import com.example.everybody_android.data.response.AlbumResponse
import com.example.everybody_android.data.response.AlbumsResponse
import com.example.everybody_android.di.ApiModule
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class AlbumRepo {

    interface AlbumApi {
        @GET("/albums")
        suspend fun getAlbums(): AlbumsResponse

        @POST("/albums")
        suspend fun createAlbum(@Body map: Map<String, String>): AlbumsResponse.Album

        @GET("/albums/{id}")
        suspend fun getAlbum(@Path("id") id: String): AlbumResponse
    }

    companion object {
        suspend fun getAlbums(): AlbumsResponse =
            ApiModule.provideApiAlbum().getAlbums()

        suspend fun getAlbum(id: String): AlbumResponse =
            ApiModule.provideApiAlbum().getAlbum(id)

        suspend fun createAlbum(map: Map<String, String>): AlbumsResponse.Album =
            ApiModule.provideApiAlbum().createAlbum(map)
    }

}