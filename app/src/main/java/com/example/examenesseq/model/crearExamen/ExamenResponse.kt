package com.example.examenesseq.model.crearExamen


data class ExamenResponse (
    val IdExamen: Int=-1,
    val TituloExamen: String,
    val Activo: Int,
    val FechaCreacion: String,
    val FechaInicio: String,
    val FechaFinal: String,
    val TiempoExamen: Int,
    val DescripcionExamen: String,
    val TextoBienvenida: String

)