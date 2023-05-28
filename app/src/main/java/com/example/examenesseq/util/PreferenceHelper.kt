package com.example.examenesseq.util


import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.model.usuario.Identidad
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Date
import java.sql.Timestamp


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

    fun SharedPreferences.saveExamenes(examenes: List<Examen>) {
        val jsonString = Gson().toJson(examenes)
        edit { it.putString("Examenes", jsonString) }
    }

    fun SharedPreferences.getExamenes(): List<Examen>? {
        val jsonString = getString("Examenes", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Examen>>() {}.type
            Gson().fromJson(jsonString, type)
        } else {
            null
        }
    }

    fun SharedPreferences.TieneExamenes(): Boolean {
        return contains("Examenes")
    }

    fun SharedPreferences.saveExamenesUsuario(examenes: List<ExamenUsuario>) {
        val jsonString = Gson().toJson(examenes)
        edit { it.putString("ExamenesUsuario", jsonString) }
    }

    fun SharedPreferences.getExamenesUsuario(): List<ExamenUsuario>? {
        val jsonString = getString("ExamenesUsuario", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Examen>>() {}.type
            Gson().fromJson(jsonString, type)
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