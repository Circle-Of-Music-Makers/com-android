package com.sidzi.circleofmusic

import com.sidzi.circleofmusic.models.ComTracksResponse
import com.sidzi.circleofmusic.restinterfaces.ComTracksListAPI
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RestAPI {
    private var tracksApiAPI: ComTracksListAPI

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(config.com_url)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        tracksApiAPI = retrofit.create(ComTracksListAPI::class.java)
    }

    fun getTracks(): Call<ComTracksResponse> {
        return tracksApiAPI.getTracks()
    }
}