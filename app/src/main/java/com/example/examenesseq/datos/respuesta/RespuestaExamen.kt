package com.example.examenesseq.datos.respuesta

import com.example.examenesseq.model.examen.SeccionesExamen

data class RespuestaExamen (
    val idExamen: Int,
    val tituloExamen: String,
    val tiempoExamen: Int,
    val idExamenUsuario: Int,
    val tiempoTranscurrido: Int,
    val Secciones: List<SeccionesExamen>
)