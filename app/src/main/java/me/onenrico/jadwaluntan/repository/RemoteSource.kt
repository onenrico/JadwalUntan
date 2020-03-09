package me.onenrico.jadwaluntan.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import me.onenrico.jadwaluntan.model.Jadwal
import me.onenrico.jadwaluntan.model.UserLogin
import me.onenrico.jadwaluntan.repository.service.MainService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteSource {
    companion object {
        const val BASE_URL = "http://203.24.50.30:7475/datasnap/rest/tservermethods1/"
        val gson = Gson()
    }

    val jadwalData = MutableLiveData<List<Jadwal>>()
    lateinit var mainService: MainService

    init {
        setup()
    }

    private fun setup() {
        mainService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(MainService::class.java)
    }

    suspend fun getUser(nim: String, password: String): UserLogin? {
        val user = mainService.getUser(nim.trim(), "x${password.trim()}").await()
        return user.result[0]
    }

    fun getJadwal(id: String, periode: String): MutableLiveData<List<Jadwal>> {
        mainService.getJadwal(id, periode).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("MEOW","Failed")
                getJadwal(id, periode)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("MEOW","Response")
                response.body()?.string().let { body ->
                    if (body == null) {
                        Log.i("MEOW","Tidak Ada?")
                        jadwalData.value = listOf()
                    } else {
                        Log.i("MEOW","Body?")
                        val jsonElement = gson.fromJson(body, JsonElement::class.java)

                        val unfiltered =
                            jsonElement.asJsonObject.get("result").asJsonArray[0].asJsonArray.map {
                                gson.fromJson(it, Jadwal::class.java)
                            }
                        val result = arrayListOf<Jadwal>()
                        unfiltered.forEach {
                            if (it.waktu.contains("\n")) {
                                it.waktu.split("\n").forEach { w ->
                                    result.add(it.copy(waktu = w.trim()))
                                }
                            } else {
                                result.add(it)
                            }
                        }
                        jadwalData.value = result
                    }
                }
            }
        })
        return jadwalData
    }

}