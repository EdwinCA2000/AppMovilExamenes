package com.example.examenesseq.model.examen

data class PreguntasExamen(
    val idPregunta: Int,
    val Numero: Int,
    val IdTipoPregunta: Int,
    val contenidoPregunta: String,
    val opciones:List<OpcionesExamen>,
    val respuesta:RespuestasExamen
)
