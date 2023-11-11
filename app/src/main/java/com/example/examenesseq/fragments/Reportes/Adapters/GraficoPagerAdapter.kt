package com.example.examenesseq.fragments.Reportes.Adapters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.examenesseq.fragments.Reportes.Data.GraficasData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class GraficoPagerAdapter(
    private val context: Context,
    private val graficos: List<GraficasData>
) : PagerAdapter() {

    override fun getCount(): Int {
        return graficos.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val graficoData = graficos[position]
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.layoutParams = layoutParams

        // Agregar el contador de preguntas
        val numeroPregunta = TextView(context)
        numeroPregunta.text = "Pregunta ${position + 1}"  // Sumar 1 para mostrar el número correcto
        numeroPregunta.textSize = 18f
        numeroPregunta.gravity = Gravity.CENTER
        linearLayout.addView(numeroPregunta)

        // Generar el gráfico de distribución de respuestas elegidas
        generarGraficoDeDistribucion(context, linearLayout, graficoData)

        container.addView(linearLayout)
        return linearLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}


private fun generarGraficoDeDistribucion(
    context: Context,
    container: LinearLayout,
    graficaData: GraficasData
) {
    val barChart = BarChart(context)

    // Configurar los datos del gráfico
    val entries = mutableListOf<BarEntry>()
    for ((index, yValue) in graficaData.yValues.withIndex()) {
        entries.add(BarEntry(index.toFloat(), yValue.toFloat()))
    }

    val dataSet = BarDataSet(entries, "Puntaje Promedio")
    dataSet.colors = graficaData.barColors.map { Color.parseColor(it) }
    dataSet.valueTextSize = 12f

    val barData = BarData(dataSet)
    barData.barWidth = 0.9f

    barChart.data = barData

    // Configurar la apariencia del gráfico
    barChart.description.isEnabled = false
    barChart.legend.isEnabled = graficaData.Legend
    barChart.setFitBars(true)

    // Configurar el eje X
    val xAxis = barChart.xAxis
    xAxis.valueFormatter = IndexAxisValueFormatter(graficaData.xValues)
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.granularity = 1f
    xAxis.setCenterAxisLabels(false)

    // Ajustar el tamaño del gráfico
    val layoutParams = LinearLayout.LayoutParams(800, 600)
    barChart.layoutParams = layoutParams

    // Agregar el gráfico al contenedor
    container.addView(barChart)

    // Configurar el título y la etiqueta
    val titulo = TextView(context)
    titulo.text = graficaData.Titulo
    titulo.textSize = 18f
    titulo.gravity = Gravity.CENTER
    container.addView(titulo)

    val label = TextView(context)
    label.text = graficaData.Label
    label.textSize = 14f
    label.gravity = Gravity.CENTER
    container.addView(label)
}
