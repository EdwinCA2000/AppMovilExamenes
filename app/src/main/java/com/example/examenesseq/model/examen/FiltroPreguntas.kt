package com.example.examenesseq.model.examen

data class FiltroPreguntas(
    val idPregunta: Int,
    val numero: Int,
    val activo: Int,
    val idSeccion: Int,
    val idTipoPregunta: Int,
    val tipoPregunta: String
    )
