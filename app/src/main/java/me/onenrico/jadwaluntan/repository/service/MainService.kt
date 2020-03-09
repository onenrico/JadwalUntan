package me.onenrico.jadwaluntan.repository.service

import kotlinx.coroutines.Deferred
import me.onenrico.jadwaluntan.model.UserResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MainService {

    @GET("tampillirs/{idmhs}/{periode}")
    fun getJadwal(@Path("idmhs") id: String, @Path("periode") periode: String): Call<ResponseBody>

    @GET("loginmhs/{nim}/{password}")
    fun getUser(@Path("nim") nim: String, @Path("password") password: String): Deferred<UserResult>

}