package com.example.examenesseq.datos.respuesta

import com.example.examenesseq.model.usuario.Identidad

data class LoginRespuesta(
    val Error: Int,
    val Objeto: Identidad,
    val Mensaje: String
)
