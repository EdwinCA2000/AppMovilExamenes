package com.example.examenesseq.fragments.preguntas.data


class EstadoPregunta(val label: String, val estadoPregunta: Int) {
    companion object {
        val estadoPreguntaOpciones = listOf(
            EstadoPregunta("Estado de Pregunta", -1),
            EstadoPregunta("Activo", 1),
            EstadoPregunta("Inactivo", 0)
        )
    }
}