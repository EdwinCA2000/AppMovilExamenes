package com.example.examenesseq.fragments.preguntas.data

data class ContenidoPregunta (
    val IdContenidoPregunta : Int,
    val ContenidoPregunta: String,
    val IdTipoContenido: Int,
    val IdPregunta:Int,
    val Orden: Int,
    val TipoContenido: String
)