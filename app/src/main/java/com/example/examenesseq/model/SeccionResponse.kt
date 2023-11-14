package com.example.examenesseq.model

data class SeccionResponse (
    val IdSeccion: Int=-1,
    val TituloSeccion: String,
    val DescripcionSeccion: String,
    val Activo: Int,
    val IdExamen: Int,
    val Orden: Int
)