package com.example.examenesseq.datos.respuesta

import com.example.examenesseq.model.crearExamen.ExamenResponse


data class crearExamenResponse (
    val Error: Int,
    val Objeto: ExamenResponse,
    val Mensaje: String
)