package com.example.examenesseq.model.examen
import java.sql.Timestamp
import java.util.Date

data class Examen(
    val IdExamen: Int=-1,
    val TituloExamen: String,
    val DescripcionExamen: String,
    val Activo: Int,
    val FechaCreacion: Timestamp,
    val FechaInicio: Timestamp,
    val FechaFinal: Timestamp,
    val TiempoExamen: Int,
    val TextoBienvenida: String

)
