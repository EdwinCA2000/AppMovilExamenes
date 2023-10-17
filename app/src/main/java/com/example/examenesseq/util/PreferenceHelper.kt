package com.example.examenesseq.util


import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.model.examen.Secciones
import com.example.examenesseq.model.usuario.Identidad
import com.example.examenesseq.model.usuario.ModuloUsuario
import com.example.examenesseq.model.usuario.Usuario
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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

    fun SharedPreferences.saveUsuario(moduloUsuario: List<ModuloUsuario>) {
        edit {
            for (usuario in moduloUsuario){
                it.putInt("IdUsuario", usuario.IdUsuario)
                it.putString("CURP", usuario.CURP)
                it.putString("Contrasena", usuario.Contrasena)
                it.putString("CorreoElectronico", usuario.CorreoElectronico)
                it.putString("Nombres", usuario.Nombres)
                it.putString("Apellido1", usuario.Apellido1)
                it.putString("Apellido2", usuario.Apellido2)
                it.putInt("IdRolUsuario", usuario.IdRolUsuario)
                it.putInt("ActivoUsuario", usuario.ActivoUsuario)
            }

        }
    }

    fun SharedPreferences.getUser():ModuloUsuario? {
        val idUsuario = getInt("IdUsuario", -1)
        val curp = getString("CURP", null)
        val contrasena=getString("Contrasena",null)
        val correoElectronico = getString("CorreoElectronico", null)
        val nombres = getString("Nombres", null)
        val apellido1 = getString("Apellido1", null)
        val apellido2 = getString("Apellido2", null)
        val idPerfil = getInt("IdPerfil", -1)
        val activoUsuario = getInt("ActivoUsuario", -1)


        return if (idUsuario != -1 && curp != null && contrasena!=null &&correoElectronico != null && nombres != null &&
            apellido1 != null && apellido2 != null && idPerfil != -1
        ) {
            ModuloUsuario(idUsuario, curp,contrasena, correoElectronico, nombres, apellido1, apellido2, idPerfil, activoUsuario)
        } else {
            null
        }
    }

    fun SharedPreferences.TieneUser(): Boolean {
        val idUsuario = getInt("IdUsuario", -1)
        val curp = getString("CURP", null)
        val contrasena=getString("Contrasena",null)
        val correoElectronico = getString("CorreoElectronico", null)
        val nombres = getString("Nombres", null)
        val apellido1 = getString("Apellido1", null)
        val apellido2 = getString("Apellido2", null)
        val idPerfil = getInt("IdPerfil", -1)
        val activoUsuario = getInt("ActivoUsuario", -1)

        return idUsuario != -1 && curp != null && contrasena!=null && correoElectronico != null && nombres != null &&
                apellido1 != null && apellido2 != null && idPerfil != -1 && activoUsuario != -1
    }

    fun SharedPreferences.saveEstadoUser(objeto: Int) {
        edit { it.putInt("Objeto", objeto) }
    }

    fun SharedPreferences.getEstadoUser(): Int?{
        return getInt("Objeto", -1)
    }

    fun SharedPreferences.TieneEstadoUser(): Boolean{
        return contains("Objeto")
    }

    fun SharedPreferences.TieneSesion(): Boolean {
        return contains("JSESSIONID")
    }

    fun SharedPreferences.setTotalUser(totalUser: String){
        edit { it.putString("TotalUser", totalUser) }
    }

    fun SharedPreferences.TieneUsuarios(): Boolean {
        return contains("TotalUser")
    }

    fun SharedPreferences.getTotalUser(): String? {
        return getString("TotalUser", null)
    }

    fun SharedPreferences.setTotalExamenesComple(totalUser: String){
        edit { it.putString("TotalExamenes", totalUser) }
    }

    fun SharedPreferences.TieneExamenCompletados(): Boolean {
        return contains("TotalExamenes")
    }

    fun SharedPreferences.getTotalExamenComple(): String? {
        return getString("TotalExamenes", null)
    }



    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    fun SharedPreferences.saveModuloUsuarios(usuario: List<Usuario>) {
        val jsonString = Gson().toJson(usuario)
        edit { it.putString("Usuarios", jsonString) }
    }

    fun SharedPreferences.getModuloUsuarios(): List<Usuario>? {
        val jsonString = getString("Usuarios", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Usuario>>() {}.type
            Gson().fromJson(jsonString, type)
        } else {
            null
        }
    }

    fun SharedPreferences.TieneModuloUsuarios(): Boolean {
        return contains("Usuarios")
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
            if (identidad.IdRolUsuario==2){
                it.putInt("IdUsuario", identidad.IdUsuario)
                it.putString("CURP", identidad.CURP)
                it.putString("CorreoElectronico", identidad.CorreoElectronico)
                it.putString("Nombres", identidad.Nombres)
                it.putString("Apellido1", identidad.Apellido1)
                it.putString("Apellido2", identidad.Apellido2)
                it.putInt("IdPerfil", identidad.IdRolUsuario)
                it.putInt("ActivoUsuario", identidad.ActivoUsuario)
            }else{
                it.putInt("IdUsuario", identidad.IdUsuario)
                it.putString("CorreoElectronico", identidad.CorreoElectronico)
                it.putString("Nombres", identidad.Nombres)
                it.putString("Apellido1", identidad.Apellido1)
                it.putString("Apellido2", identidad.Apellido2)
                it.putInt("IdRolUsuario", identidad.IdRolUsuario)
                it.putInt("ActivoUsuario", identidad.ActivoUsuario)
            }

        }
    }



    fun SharedPreferences.getIdentidad(): Identidad? {
        val idUsuario = getInt("IdUsuario", -1)
        val correoElectronico = getString("CorreoElectronico", null)
        val nombres = getString("Nombres", null)
        val apellido1 = getString("Apellido1", null)
        val apellido2 = getString("Apellido2", null)
        val idPerfil = getInt("IdPerfil", -1)
        val activoUsuario = getInt("ActivoUsuario", -1)
        val curp = if (idPerfil == 2) getString("CURP", null) else ""

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

    fun SharedPreferences.saveSecciones(secciones: List<Secciones>) {
        val jsonString = Gson().toJson(secciones)
        edit { it.putString("Secciones", jsonString) }
    }

    fun SharedPreferences.getSecciones(): List<Secciones>? {
        val jsonString = getString("Secciones", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Secciones>>() {}.type
            Gson().fromJson(jsonString, type)
        } else {
            null
        }
    }

    fun SharedPreferences.saveCantidadPreguntas(cantidad: String?) {
        edit { it.putString("CantidadPreguntas", cantidad) }
    }

    fun SharedPreferences.getCantidadPreguntas(): String? {
        return getString("CantidadPreguntas", "")
    }


    fun SharedPreferences.TieneSecciones(): Boolean {
        return contains("Secciones")
    }

    fun SharedPreferences.TieneExamenes(): Boolean {
        return contains("Examenes")
    }

    fun SharedPreferences.TieneIdentidad(): Boolean {
        val idUsuario = getInt("IdUsuario", -1)
        val curp = getString("CURP", null)
        val correoElectronico = getString("CorreoElectronico", null)
        val nombres = getString("Nombres", null)
        val apellido1 = getString("Apellido1", null)
        val apellido2 = getString("Apellido2", null)
        val idPerfil = getInt("IdPerfil", -1)
        val activoUsuario = getInt("ActivoUsuario", -1)

        return idUsuario != -1 && curp != null && correoElectronico != null && nombres != null &&
                apellido1 != null && apellido2 != null && idPerfil != -1 && activoUsuario != -1
    }


    fun SharedPreferences.saveExamenesUsuario(examenes: List<ExamenUsuario>) {
        val jsonString = Gson().toJson(examenes)
        edit { it.putString("ExamenesUsuario", jsonString) }
    }
    fun SharedPreferences.TieneExamenesUsuario(): Boolean {
        return contains("ExamenesUsuario")
    }


    fun SharedPreferences.getExamenesUsuario(): List<ExamenUsuario>? {
        val jsonString = getString("ExamenesUsuario", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<ExamenUsuario>>() {}.type
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