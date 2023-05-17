package com.example.examenesseq.datos

import com.example.examenesseq.datos.respuesta.LoginRespuesta
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServicio {


    @POST(value="tecnologiaeducativa.Sesion/actionLogin")
    fun postLogin(@Query(value="Usuario")usuario: String, @Query(value="Password") password: String):
            Call<LoginRespuesta>

    @POST(value="tecnologiaeducativa.Sesion/actionRegistroUsuario")

    fun postRegistro(
        @Query(value="IdUsuario")IdUsuario:Int=1,
        @Query(value="CURP")CURP:String,
        @Query(value="Contrasena")Contrasena:String,
        @Query(value="CorreoElectronico")CorreoElectronico: String,
        @Query(value="Nombres")Nombres: String,
        @Query(value="Apellido1")Apellido1:String,
        @Query(value="Apellido2")Apellido2: String,
    ):Call<LoginRespuesta>
    companion object Factory{
        private const val BASE_URL="http://192.168.0.3:8080/Ceneval/"
        fun create(): ApiServicio{

            val retrofit= Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiServicio::class.java)

        }
    }
}