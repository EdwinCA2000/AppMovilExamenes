package com.example.examenesseq.fragments.preguntas.data

data class Pregunta (
    val IdPregunta : Int,
    val Numero: Int,
    var Activo: Int,
    val IdSeccion: Int,
    val IdTipoPregunta: Int,
    val TipoPregunta: String
)