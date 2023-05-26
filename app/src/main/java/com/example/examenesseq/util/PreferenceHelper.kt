package com.example.examenesseq.util


import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.examenesseq.model.Examen
import com.example.examenesseq.model.usuario.Identidad
import com.google.gson.Gson
import java.sql.Date


object PreferenceHelper {

    fun defaultPrefs(context: Context): SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(context)

    fun customPrefs(context: Context, name: String): SharedPreferences
            = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun SharedPreferences.getJSessionId(): String? {
        return getString("JSESSIONID", null)
    }

    fun SharedPreferences.setJSessionId(jsessionid: String) {
        edit { it.putString("JSESSIONID", jsessionid) }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }



    /**
     * puts a value for the given [key].
     */
    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> {
                val jsonString = Gson().toJson(value)
                edit { it.putString(key, jsonString) }
            }
        }
    }

    fun SharedPreferences.saveIdentidad(identidad: Identidad) {
        edit {
            it.putInt("IdUsuario", identidad.IdUsuario)
            it.putString("CURP", identidad.CURP)
            it.putString("CorreoElectronico", identidad.CorreoElectronico)
            it.putString("Nombres", identidad.Nombres)
            it.putString("Apellido1", identidad.Apellido1)
            it.putString("Apellido2", identidad.Apellido2)
            it.putInt("IdPerfil", identidad.IdPerfil)
            it.putInt("ActivoUsuario", identidad.ActivoUsuario)
        }
    }

    fun SharedPreferences.getIdentidad(): Identidad? {
        val idUsuario = getInt("IdUsuario", -1)
        val curp = getString("CURP", null)
        val correoElectronico = getString("CorreoElectronico", null)
        val nombres = getString("Nombres", null)
        val apellido1 = getString("Apellido1", null)
        val apellido2 = getString("Apellido2", null)
        val idPerfil = getInt("IdPerfil", -1)
        val activoUsuario = getInt("ActivoUsuario", -1)

        return if (idUsuario != -1 && curp != null && correoElectronico != null && nombres != null &&
            apellido1 != null && apellido2 != null && idPerfil != -1
        ) {
            Identidad(idUsuario, curp, correoElectronico, nombres, apellido1, apellido2, idPerfil, activoUsuario)
        } else {
            null
        }
    }

    fun SharedPreferences.saveExamen(examen: Examen) {
        edit {
            it.putInt("IdExamen", examen.IdExamen)
            it.putString("TituloExamen", examen.TituloExamen)
            it.putString("DescripcionExamen", examen.DescripcionExamen)
            it.putLong("FechaInicio", examen.FechaInicio.time)
            it.putLong("FechaFinal", examen.FechaFinal.time)
            it.putInt("TiempoExamen", examen.TiempoExamen)
            it.putInt("EstadoExamen", examen.EstadoExamen)
        }
    }

    fun SharedPreferences.getExamen(): Examen? {
        val idExamen = getInt("IdExamen", -1)
        val tituloExamen = getString("TituloExamen", null)
        val descripcionExamen = getString("DescripcionExamen", null)
        val fechaInicio = getLong("FechaInicio", -1L)
        val fechaFinal = getLong("FechaFinal", -1L)
        val tiempoExamen = getInt("TiempoExamen", -1)
        val estadoExamen = getInt("EstadoExamen", -1)

        return if (idExamen != -1 && tituloExamen != null && descripcionExamen != null &&
            fechaInicio != -1L && fechaFinal != -1L && tiempoExamen != -1 && estadoExamen != -1
        ) {
            Examen(
                idExamen,
                tituloExamen,
                descripcionExamen,
                Date(fechaInicio),
                Date(fechaFinal),
                tiempoExamen,
                estadoExamen
            )
        } else {
            null
        }
    }





    /**
     * finds a preference based on the given [key].
     * [T] is the type of value
     * @param defaultValue optional defaultValue - will take a default defaultValue if it is not specified
     */
    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T? {
        val jsonString = getString(key, null)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, T::class.java)
        } else {
            defaultValue
        }
    }

}