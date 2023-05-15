package com.example.examenesseq.datos

import com.example.examenesseq.datos.respuesta.LoginRespuesta
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServicio {
    @POST(value="login")
    fun postLogin(@Query(value="email")email: String, @Query(value="password") password: String):
            Call<LoginRespuesta>

    companion object Factory{
        private const val BASE_URL="http://192.168.0.11:8080/api"
        fun create(): ApiServicio{

            val retrofit= Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiServicio::class.java)

        }
    }
}