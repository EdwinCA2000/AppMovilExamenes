package com.example.examenesseq.model.usuario

data class Usuario(
    val IdUsuario: Int,
    val CURP: String,
    val Contrasena: String,
    val CorreoElectronico: String,
    val Nombres: String,
    val Apellido1: String,
    val Apellido2: String,
    val IdRolUsuario: Int,
    val ActivoUsuario: Int
)
