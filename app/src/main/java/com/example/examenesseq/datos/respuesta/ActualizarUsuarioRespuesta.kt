package com.example.examenesseq.datos.respuesta

import com.example.examenesseq.model.usuario.ModuloUsuario

data class ActualizarUsuarioRespuesta (
    val Error: Int,
    val Objeto: ModuloUsuario
)
