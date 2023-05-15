package com.example.examenesseq.datos.respuesta

import com.example.examenesseq.model.Usuario

data class LoginRespuesta(
    val success: Boolean,
    val usuario: Usuario,
    val jwt: String
)
