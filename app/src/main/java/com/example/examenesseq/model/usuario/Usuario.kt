package com.example.examenesseq.model.usuario

data class Usuario(
    var IdUsuario: Int,
    var CURP: String,
    var Contrasena: String,
    var CorreoElectronico: String,
    var Nombres: String,
    var Apellido1: String,
    var Apellido2: String,
    var IdPerfil: String,
    var ActivoUsuario: String
)
