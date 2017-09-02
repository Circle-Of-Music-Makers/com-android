package com.sidzi.circleofmusic.restinterfaces


import com.sidzi.circleofmusic.models.ComTracksResponse
import retrofit2.Call
import retrofit2.http.GET

interface ComTracksListAPI {
    @GET("/getTracks")
    fun getTracks(): Call<ComTracksResponse>
}
