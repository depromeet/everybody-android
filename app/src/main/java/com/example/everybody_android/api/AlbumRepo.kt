package com.example.everybody_android.api

import com.example.everybody_android.data.response.AlbumsResponse
import com.example.everybody_android.di.ApiModule
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

class AlbumRepo {

    interface AlbumApi {
        @GET("/albums")
        suspend fun getAlbums(): AlbumsResponse

        @POST("/albums")
        suspend fun createAlbum(@Body map:Map<String,String>): AlbumsResponse.Album
    }

    companion object {
        suspend fun getAlbums(): AlbumsResponse =
            ApiModule.provideApiAlbum().getAlbums()

        suspend fun createAlbum(map:Map<String,String>): AlbumsResponse.Album =
            ApiModule.provideApiAlbum().createAlbum(map)
    }

}