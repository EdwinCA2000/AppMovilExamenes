package com.example.examenesseq.fragments.secciones.data

class EstadoSeccionData (val label: String, val estadoSeccion: Int) {
    companion object {
        val estadoSeccionOpciones = listOf(
            EstadoSeccionData("Estado de Sección", -1),
            EstadoSeccionData("Activo", 1),
            EstadoSeccionData("Inactivo", 0)
        )
    }
}