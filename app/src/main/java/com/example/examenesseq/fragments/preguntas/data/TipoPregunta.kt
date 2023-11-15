package com.example.examenesseq.fragments.preguntas.data

class TipoPregunta (val label: String, val tipoPregunta: Int) {
    companion object {
        val tipoPreguntaOpciones = listOf(
            TipoPregunta("Tipo de Pregunta", -1),
            TipoPregunta("Abierta", 1),
            TipoPregunta("Multiple", 2)
        )
    }
}