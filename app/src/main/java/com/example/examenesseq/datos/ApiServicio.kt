package com.example.examenesseq.datos


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.examenesseq.fragments.preguntas.data.ContenidoPregunta
import com.example.examenesseq.fragments.preguntas.data.Pregunta
import com.example.examenesseq.fragments.Reportes.Data.GraficasData
import com.example.examenesseq.fragments.adminExamen.AdminExamenesData
import com.example.examenesseq.datos.respuesta.ActualizarUsuarioRespuesta
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.datos.respuesta.ModuloUsuarioRespuesta
import com.example.examenesseq.datos.respuesta.RespuestaActivarPregunta
import com.example.examenesseq.datos.respuesta.RespuestaActivarSeccion
import com.example.examenesseq.datos.respuesta.RespuestaActivarUser
import com.example.examenesseq.datos.respuesta.RespuestaEliminarExamen
import com.example.examenesseq.datos.respuesta.RespuestaEliminarPregunta
import com.example.examenesseq.datos.respuesta.RespuestaEliminarSeccion
import com.example.examenesseq.datos.respuesta.RespuestaEliminarUser
import com.example.examenesseq.datos.respuesta.RespuestaExamen
import com.example.examenesseq.datos.respuesta.RespuestaExamenUsuario
import com.example.examenesseq.datos.respuesta.Respuestas
import com.example.examenesseq.model.SeccionResponse
import com.example.examenesseq.model.crearExamen.ExamenResponse
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.model.examen.Secciones
import com.example.examenesseq.model.usuario.Identidad
import com.example.examenesseq.model.usuario.ModuloUsuario
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

    @GET(value="tecnologiaeducativa.ceneval/jsonTotalPreguntas")
    fun obtenerTotalPreguntas(@Query(value="IdSeccion")idSeccion: Int):
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
        @Query(value="Apellido2")Apellido2: String
    ):Call<LoginRespuesta>

    @POST(value="tecnologiaeducativa.ceneval/actionEditarModuloUsuario")
    fun actualizarUsuario(
        @Query(value="IdUsuario")IdUsuario: Int,
        @Query(value="Nombres")Nombres: String,
        @Query(value="Apellido1")Apellido1:String,
        @Query(value="Apellido2")Apellido2: String,
        @Query(value="CURP")CURP:String,
        @Query(value="CorreoElectronico")CorreoElectronico: String
    ):Call<ActualizarUsuarioRespuesta>

    @GET(value="tecnologiaeducativa.ceneval/actionActivarUsuario")
    fun actualizarEstadoUser(
        @Query(value="IdUsuario")IdUsuario: Int,
        @Query(value="ActivoUsuario")ActivoUsuario:Int
    ):Call<RespuestaActivarUser>

    @GET("tecnologiaeducativa.Sesion/actionCerrarSesion")
    fun cerrarSesion(): Call<Unit>

    @GET("tecnologiaeducativa.ceneval/jsonTotalModuloUsuarios")
    fun obtenerTotalUsuariosRegistrados(): Call <LoginRespuesta>

    @GET("tecnologiaeducativa.ceneval/jsonExamenesCompletados")
    fun obtenerTotalExamenCompletados(): Call <LoginRespuesta>

    @GET("tecnologiaeducativa.ceneval/jsonExamenesUsuario")
    fun obtenerExamenesUsuario(): Call <List<ExamenUsuario>>

    @GET("tecnologiaeducativa.ceneval/jsonModuloUsuarios")
    fun obtenerModuloUsuarios(): Call <ModuloUsuarioRespuesta>

    @GET("tecnologiaeducativa.Examen/jsonExamenCeneval")
    fun obtenerExamen(@Query(value="IdExamen")idExamen: Int): Call <RespuestaExamen>

    @POST("tecnologiaeducativa.Examen/actionGuardarExamenUsuario")
    fun guardarExamenUsuario(
        @Query(value="time")time: Int,
        @Query(value="calif")calif: Int,
        @Query(value="idexamen")idexamen: Int
    ): Call <LoginRespuesta>

    @GET("tecnologiaeducativa.Examen/jsonExamenUsuario")
    fun obtenerDatosExamenUsuario(
        @Query(value="IdExamen")idExamen: Int,
        @Query(value="IdExamenUsuario")idExamenusuario: Int): Call <RespuestaExamenUsuario>

    @POST("tecnologiaeducativa.Examen/actionGuardarEditarRespuesta")
    fun guardarRespuestaUsuario(
        @Query(value="idRespuesta")idRespuesta: Int,
        @Query(value="idPregunta")idPregunta: Int,
        @Query(value="idOpcion")idOpcion: Int,
        @Query(value="idExamen")idexamen: Int

    ): Call <Respuestas>

    @POST("tecnologiaeducativa.Examen/actionFinalizarExamen")
    fun finalizarExamenUsuario(
        @Query(value="idexamenU")idexamenU: Int,
        @Query(value="intentos")intentos: Int,
        @Query(value="estado")estado: Int,
        @Query(value="tiempoRestante")tiempoRestante: String,
        @Query(value="time")time: Int

    ): Call <LoginRespuesta>
    @POST("tecnologiaeducativa.ceneval/actionGuardarEditarExamenes")
    fun crearExamen(
        @Query(value="IdExamen")idExamen: Int,
        @Query(value="TituloExamen")tituloExamen: String,
        @Query(value="Activo")activo: Int,
        @Query(value="FechaCreacion")fechaCreacion: String,
        @Query(value="FechaInicio")fechaInicio: String,
        @Query(value="FechaFinal")fechaFinal: String,
        @Query(value="TiempoExamen")tiempoExamen: Int,
        @Query(value="DescripcionExamen")descripcionExamen: String,
        @Query(value="TextoBienvenida")textoBienvenida: String
    ): Call <ExamenResponse>

    @POST("tecnologiaeducativa.ceneval/actionGuardarEditarSecciones")
    fun guardarEditarSecciones(
        @Query(value="IdSeccion")idSeccion: Int,
        @Query(value="TituloSeccion")tituloSeccion: String,
        @Query(value="Activo")activo: Int,
        @Query(value="IdExamen")idExamen: Int,
        @Query(value="Orden")orden: Int,
        @Query(value="DescripcionSeccion")descripcionSeccion: String
    ): Call <SeccionResponse>

    @POST("tecnologiaeducativa.ceneval/actionGuardarEditarPreguntas")
    fun guardarEditarPreguntas(
        @Query(value="IdPregunta")idPregunta: Int,
        @Query(value="Numero")numero: Int,
        @Query(value="Activo")activo: Int,
        @Query(value="IdSeccion")idSeccion: Int,
        @Query(value="IdTipoPregunta")idTipoPregunta: Int
    ): Call <Pregunta>

    @POST("tecnologiaeducativa.ceneval/actionActivarSeccion")
    fun activarDesactivarSeccion(
        @Query(value="IdSeccion")idSeccion: Int,
        @Query(value="Activo")activo: Int
    ): Call <RespuestaActivarSeccion>

    @POST("tecnologiaeducativa.ceneval/actionActivarPregunta")
    fun activarDesactivarPregunta(
        @Query(value="IdPregunta")idPregunta: Int,
        @Query(value="Activo")activo: Int
    ): Call <RespuestaActivarPregunta>

    @GET("tecnologiaeducativa.ceneval/jsonExamenesAdmin")
    fun getExamenesAdmin(): Call<List<AdminExamenesData>>

    @POST("tecnologiaeducativa.ceneval/jsonGraficas")
    fun getJsonGraficas(@Query(value="IdExamen")idExamen: Int): Call<List<List<GraficasData>>>

    @POST("tecnologiaeducativa.ceneval/actionEliminarUsuario")
    fun eliminarUsuario(@Query(value="IdUsuario")idUsuario: Int): Call <RespuestaEliminarUser>

    @POST("tecnologiaeducativa.ceneval/actionEliminarExamen")
    fun deleteExamen(@Query(value="IdExamen")idExamen: Int): Call <RespuestaEliminarExamen>
    @POST("tecnologiaeducativa.ceneval/actionEliminarSeccion")
    fun deleteSeccion(@Query(value="IdSeccion")idSeccion: Int): Call <RespuestaEliminarSeccion>

    @POST("tecnologiaeducativa.ceneval/actionEliminarPregunta")
    fun deletePregunta(@Query(value="IdPregunta")idPregunta: Int): Call <RespuestaEliminarPregunta>

    @POST("tecnologiaeducativa.ceneval/actionAgregarUsuarios")
    fun agregarUsuario(
        @Query(value="CURP")curp: String,
        @Query(value="Contrasena")contrasena: String,
        @Query(value="CorreoElectronico")correo: String,
        @Query(value="Nombres")nombres: String,
        @Query(value="Apellido1")apellido1: String,
        @Query(value="Apellido2")apellido2: String
    ): Call <ModuloUsuario>

    @GET("tecnologiaeducativa.ceneval/jsonSecciones")
    fun getSeccionesDeExamen(@Query(value="IdExamen")idExamen: Int): Call<List<Secciones>>

    @GET("tecnologiaeducativa.ceneval/jsonPreguntas")
    fun getPreguntasDeSeccion(@Query(value="IdSeccion")idSeccion: Int): Call<List<Pregunta>>

    @GET("tecnologiaeducativa.ceneval/jsonContenidoPreguntas")
    fun getContenidoDePreguntas(@Query(value="IdPregunta")idPregunta: Int): Call<List<ContenidoPregunta>>
    companion object Factory {
        private const val BASE_URL = "http://192.168.0.10:8080/Ceneval/"
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
