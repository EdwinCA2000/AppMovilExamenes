package com.example.examenesseq.fragments.crearExamen.data

data class EstadoExamenData (val label: String, val estadoExamen: Int) {
    companion object {
        val estadoExamenOpciones = listOf(
            EstadoExamenData("Estado del examen", -1),
            EstadoExamenData("Activo", 1),
            EstadoExamenData("Inactivo", 0)
        )
    }
}