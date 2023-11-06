package com.example.examenesseq.fragments.crearExamen.data

data class DuracionExamenData(val label: String, val duracionExamen: Int) {
    companion object {
        val duracionExamenOpciones = listOf(
            DuracionExamenData("Duración del examen", -1),
            DuracionExamenData("1 hora", 60),
            DuracionExamenData("2 horas", 120),
            DuracionExamenData("3 horas", 180)
        )
    }
}
