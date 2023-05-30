package com.example.examenesseq.datos.examen

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.Secciones
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.getExamenes
import com.example.examenesseq.util.PreferenceHelper.getSecciones

class DaoExamen {
    fun obtenerCantidadSecciones(context: Context, idExamen: Int): Int {
        val examenes = getExamenes(context)
        val secciones = getSecciones(context)

        val examen = examenes?.find { it.IdExamen == idExamen }
        if (examen != null) {
            val seccionesExamen = secciones?.filter { it.IdExamen == idExamen }
            return seccionesExamen?.size ?: 0
        }

        return 0
    }

    fun obtenerTitulosSecciones(context: Context, idExamen: Int): List<String> {
        val secciones = getSecciones(context)
        return secciones?.filter { it.IdExamen == idExamen }?.map { it.TituloSeccion } ?: emptyList()
    }

    private fun getExamenes(context: Context): List<Examen>? {
        val preferences = PreferenceHelper.defaultPrefs(context)
        return preferences.getExamenes()
    }

    private fun getSecciones(context: Context): List<Secciones>? {
        val preferences = PreferenceHelper.defaultPrefs(context)
        return preferences.getSecciones()
    }
}