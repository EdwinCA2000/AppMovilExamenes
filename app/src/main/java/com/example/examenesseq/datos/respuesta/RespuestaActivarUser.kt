package com.example.examenesseq.datos.respuesta

import com.example.examenesseq.model.usuario.ModuloUsuario

data class RespuestaActivarUser(
    val Error: Int,
    val Objeto: Int,
    val Mensaje: String
)
