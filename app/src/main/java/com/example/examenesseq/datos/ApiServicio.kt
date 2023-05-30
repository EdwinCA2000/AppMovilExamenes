package com.example.examenesseq.datos


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.model.examen.Secciones
import com.example.examenesseq.model.usuario.Identidad
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServicio {

    @GET(value= "tecnologiaeducativa.PerfilExamen/jsonExamenesPerfil")
    fun obtenerExamenUsuario(@Query(value="IdUsuario")idUsuario: Int):
            Call <List<ExamenUsuario>>

    @POST(value="tecnologiaeducativa.Sesion/actionLogin")
    fun postLogin(@Query(value="Usuario")usuario: String, @Query(value="Password") password: String):
            Call <LoginRespuesta>

    @GET("jsonDatosSesion")
    fun getDatosSesion(): Call<Identidad>
    @GET("tecnologiaeducativa.ceneval/jsonExamenes")
    fun getExamenesDisponibles(): Call<List<Examen>>

    @GET("tecnologiaeducativa.ceneval/jsonSecciones")
    fun getSeccionesExamen(): Call<List<Secciones>>

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

    @GET("tecnologiaeducativa.Sesion/actionCerrarSesion")
    fun cerrarSesion(): Call<Unit>



    companion object Factory {
        private const val BASE_URL = "http://192.168.0.14:8080/Ceneval/"
        fun create(context: Context): ApiServicio {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val jsessionId = getJsessionIdFromStorage(context)
                    Log.d("ApiServicioJSSESIONID", jsessionId)
                    val modifiedRequest = originalRequest.newBuilder()
                        .header("Cookie", jsessionId)
                        .build()
                    chain.proceed(modifiedRequest)
                }
                .build()


            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(ApiServicio::class.java)
        }

        private fun getJsessionIdFromStorage(context: Context): String {
            val sharedPreferences: SharedPreferences = PreferenceHelper.defaultPrefs(context)
            val cookie = sharedPreferences.getString("JSESSIONID", "") ?: ""
            return if (cookie.isNotEmpty()) {
                val cleanedCookie = if (cookie.startsWith("JSESSIONID=")) cookie else "JSESSIONID=$cookie"
                cleanedCookie.substringBefore(";")
            } else {
                ""
            }

        }




    }
}
