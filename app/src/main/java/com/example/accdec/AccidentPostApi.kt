package com.example.accdec

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AccidentPostApi {

    @POST("http://192.168.42.59:3000/upload/")
    fun getAccidentResponse(
        @Body accident : Accident
    ): Single<Analysis>
}

interface AccidentApi {

    @GET("https://api.myjson.com/bins/18x78c/")
    fun getIssuesListResponse(
    ): Single<Analysis>
}