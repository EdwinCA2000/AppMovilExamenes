package com.example.examenesseq.model.examen

import java.sql.Timestamp

data class ExamenUsuario(
    val IdExamenUsuario: Int,
    val FechaInicio: Timestamp,
    val FechaFinal: Timestamp,
    val TiempoTranscurrido: Int,
    val TotalCalificacion: Int,
    val IdExamen: Int,
    val IdUsuario: Int,
    val Estado: Int,
    val TituloExamen: String
)
