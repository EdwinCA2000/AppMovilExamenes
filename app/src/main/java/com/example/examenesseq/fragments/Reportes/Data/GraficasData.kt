package com.example.examenesseq.fragments.Reportes.Data

data class GraficasData (
    val Canvas: String,
    val Label: String,
    val Titulo: String,
    val Tipo: String,
    val xValues: List<String>,
    val barColors: List<String>,
    val yValues: List<Int>,
    val Legend: Boolean
)