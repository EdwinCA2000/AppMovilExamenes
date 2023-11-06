package com.example.examenesseq.fragments.crearExamen

import androidx.lifecycle.ViewModel

class CrearExamenViewModel : ViewModel() {
    var tituloExamen: String = ""
    var fechaInicio: String = ""
    var fechaFinal: String = ""
    var duracionExamen: Int = -1
    var estadoExamen: Int=-1
}