package com.example.examenesseq.datos.respuesta

data class RespuestaExamenUsuario (
    val tiempoTranscurrido: Int,
    val tiempoRestante: String,
    val intentos: Int
)