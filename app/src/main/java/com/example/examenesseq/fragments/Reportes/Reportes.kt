package com.example.examenesseq.fragments.Reportes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.examenesseq.fragments.Reportes.Data.DatosExamen
import com.example.examenesseq.fragments.Reportes.Data.GraficasData
import com.example.examenesseq.fragments.Reportes.Adapters.GraficoDoughnutPagerAdapter
import com.example.examenesseq.fragments.Reportes.Adapters.GraficoPagerAdapter
import com.example.examenesseq.fragments.Reportes.Adapters.SpinnerExamen
import com.example.examenesseq.databinding.FragmentReportesBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.fragments.adminExamen.AdminExamenesData
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class Reportes : Fragment() {

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    private var graficoPagerAdapter: GraficoPagerAdapter? = null
    private var graficoDoughnutPagerAdapter: GraficoDoughnutPagerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReportesBinding.inflate(inflater, container, false)

        obtenerExamenes()
        return binding.root
    }



    private fun obtenerGraficosTipoBar(respuesta: List<List<GraficasData>>): List<GraficasData> {
        val graficosTipoBar = mutableListOf<GraficasData>()

        // Itera a través de la respuesta para encontrar gráficos de tipo bar
        for (listaGraficas in respuesta) {
            for (graficaData in listaGraficas) {
                if (graficaData.Tipo == "bar" && graficaData.Titulo == "Distribución de Respuestas elegidas") {
                    graficosTipoBar.add(graficaData)
                }
            }
        }

        return graficosTipoBar
    }

    private fun obtenerGraficosTipoBarExcluyendoDistribucion(respuesta: List<List<GraficasData>>): List<GraficasData> {
        val graficosTipoBarExcluyendoDistribucion = mutableListOf<GraficasData>()

        // Itera a través de la respuesta para encontrar gráficos de tipo bar excluyendo "Distribución de Respuestas elegidas"
        for (listaGraficas in respuesta) {
            for (graficaData in listaGraficas) {
                if (graficaData.Tipo == "bar" && graficaData.Titulo != "Distribución de Respuestas elegidas") {
                    graficosTipoBarExcluyendoDistribucion.add(graficaData)
                }
            }
        }

        return graficosTipoBarExcluyendoDistribucion
    }
    private fun generarGraficoDeBarra(
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

        // Desactivar la centración de las etiquetas
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
        container.gravity=Gravity.CENTER
    }



    private fun obtenerTitulos(respuesta: List<List<GraficasData>>?): List<String> {
        val titulos = mutableListOf<String>()

        respuesta?.forEachIndexed { index, listaGraficasData ->
            listaGraficasData.forEach { graficasData ->
                val titulo = graficasData.Titulo
                titulos.add(titulo)
            }
        }

        return titulos
    }

    private fun obtenerLabels(respuesta: List<List<GraficasData>>?): List<String> {
        val labels = mutableListOf<String>()

        respuesta?.forEachIndexed { index, listaGraficasData ->
            listaGraficasData.forEach { graficasData ->
                val label = graficasData.Label
                labels.add(label)
            }
        }

        return labels
    }

    private fun obtenerTipos(respuesta: List<List<GraficasData>>?): List<String> {
        val tipos = mutableListOf<String>()

        respuesta?.forEachIndexed { index, listaGraficasData ->
            listaGraficasData.forEach { graficasData ->
                val tipo = graficasData.Tipo
                tipos.add(tipo)
            }
        }

        return tipos
    }

    private fun obtenerBarColors(respuesta: List<List<GraficasData>>?): List<List<String>> {
        val barColorsList = mutableListOf<List<String>>()

        respuesta?.forEachIndexed { index, listaGraficasData ->
            val barColors = listaGraficasData.map { graficasData -> graficasData.barColors }
            barColorsList.addAll(barColors)
        }

        return barColorsList
    }

    private fun obteneryValues(respuesta: List<List<GraficasData>>?): List<List<Int>> {
        val yValuesList = mutableListOf<List<Int>>()

        respuesta?.forEachIndexed { index, listaGraficasData ->
            val yValues = listaGraficasData.map { graficasData -> graficasData.yValues }
            yValuesList.addAll(yValues)
        }

        return yValuesList
    }

    private fun obtenerxValues(respuesta: List<List<GraficasData>>?): List<List<String>> {
        val xValuesList = mutableListOf<List<String>>()

        respuesta?.forEachIndexed { index, listaGraficasData ->
            val xValues = listaGraficasData.map { graficasData -> graficasData.xValues }
            xValuesList.addAll(xValues)
        }

        return xValuesList
    }

    private fun obtenerLegends(respuesta: List<List<GraficasData>>?): List<Boolean> {
        val legends = mutableListOf<Boolean>()

        respuesta?.forEachIndexed { index, listaGraficasData ->
            listaGraficasData.forEach { graficasData ->
                val legend = graficasData.Legend
                legends.add(legend)
            }
        }

        return legends
    }

    private fun getColors(barColors: List<String>): List<Int> {
        val colors = mutableListOf<Int>()

        barColors.forEach { color ->
            colors.add(Color.parseColor(color))
        }

        return colors
    }

    private fun obtenerExamenes(){

        apiServicio.getExamenesAdmin().enqueue(object : Callback<List<AdminExamenesData>>{
            override fun onResponse(
                call: Call<List<AdminExamenesData>>,
                response: Response<List<AdminExamenesData>>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    val idsExamenes = obtenerDatosExamenes(respuesta)
                    Log.d("Ids Examenes", idsExamenes.toString())
                    val spinnerTituloExamen = binding.spinner2
                    val spinnerTituloAdapter = SpinnerExamen(requireContext(), idsExamenes)
                    spinnerTituloExamen.adapter = spinnerTituloAdapter

                    spinnerTituloExamen.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            // Obtener el ID del examen seleccionado
                            val idExamenSeleccionado = idsExamenes[position].idExamen

                            binding.containerGraficas.removeAllViews()
                            binding.containerGraficasDoughnut.removeAllViews()
                            binding.viewPagerDoughnuts.removeAllViews()
                            binding.viewPager2.removeAllViews()
                            actualizarGraficasSegunExamen(idExamenSeleccionado)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<AdminExamenesData>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }

    private fun actualizarGraficasSegunExamen(idExamen: Int) {
        apiServicio.getJsonGraficas(idExamen).enqueue(object : Callback<List<List<GraficasData>>> {
            override fun onResponse(
                call: Call<List<List<GraficasData>>>,
                response: Response<List<List<GraficasData>>>
            ) {
                if (response.isSuccessful) {
                    val respuesta = response.body()

                    val graficosTipoBar = respuesta?.let { obtenerGraficosTipoBarExcluyendoDistribucion(it) }
                    val container = binding.containerGraficas
                    val container2 = binding.containerGraficasDoughnut
                    if (graficosTipoBar != null) {
                        for (graficaData in graficosTipoBar) {
                            generarGraficoDeBarra(requireContext(), container, graficaData)
                        }
                    }

                    val graficosTipoDoughnut = respuesta?.let { obtenerGraficosTipoDoughnutExcluyendoAciertos(it) }
                    if (graficosTipoDoughnut != null) {
                        for (graficaData in graficosTipoDoughnut) {
                            generarGraficoDoughnut(requireContext(), container2, graficaData)
                        }
                    }

                    val viewPager = binding.viewPager2
                    val viewPager2 = binding.viewPagerDoughnuts
                    val graficosTipoDistribucion = respuesta?.let { obtenerGraficosTipoBar(it) }
                    val graficosTipoDoughnuts= respuesta?.let{obtenerGraficosTipoDoughnut(it)}

                    graficoPagerAdapter = graficosTipoDistribucion?.let {
                        GraficoPagerAdapter(requireContext(), it)
                    }

                    graficoDoughnutPagerAdapter= graficosTipoDoughnuts?.let {
                        GraficoDoughnutPagerAdapter(requireContext(),it)
                    }

                    // Establecer el adaptador
                    viewPager.adapter = graficoPagerAdapter
                    viewPager2.adapter=graficoDoughnutPagerAdapter
                }
            }

            override fun onFailure(call: Call<List<List<GraficasData>>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }
        })
    }

    private fun obtenerGraficosTipoDoughnut(respuesta: List<List<GraficasData>>): List<GraficasData> {
        val graficosTipoDoughnut = mutableListOf<GraficasData>()

        // Itera a través de la respuesta para encontrar gráficos de tipo doughnut
        for (listaGraficas in respuesta) {
            for (graficaData in listaGraficas) {
                if (graficaData.Tipo == "doughnut" && graficaData.Titulo=="Relacion de Aciertos") {
                    graficosTipoDoughnut.add(graficaData)
                }
            }
        }

        return graficosTipoDoughnut
    }

    private fun obtenerGraficosTipoDoughnutExcluyendoAciertos(respuesta: List<List<GraficasData>>): List<GraficasData> {
        val graficosTipoDoughnut = mutableListOf<GraficasData>()

        // Itera a través de la respuesta para encontrar gráficos de tipo doughnut excluyendo "Relación de Aciertos"
        for (listaGraficas in respuesta) {
            for (graficaData in listaGraficas) {
                if (graficaData.Tipo == "doughnut" && graficaData.Titulo != "Relacion de Aciertos") {
                    graficosTipoDoughnut.add(graficaData)
                }
            }
        }

        return graficosTipoDoughnut
    }




    private fun generarGraficoDoughnut(
        context: Context,
        container: LinearLayout,
        graficaData: GraficasData
    ) {
        val doughnutChart = PieChart(context)

        // Configurar los datos del gráfico
        val entries = mutableListOf<PieEntry>()
        for ((index, yValue) in graficaData.yValues.withIndex()) {
            entries.add(PieEntry(yValue.toFloat(), graficaData.xValues[index]))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = graficaData.barColors.map { Color.parseColor(it) }
        dataSet.valueTextSize = 12f

        val pieData = PieData(dataSet)

        doughnutChart.data = pieData

        // Configurar la apariencia del gráfico
        doughnutChart.description.isEnabled = false
        doughnutChart.legend.isEnabled = graficaData.Legend

        // Ajustar el tamaño del gráfico
        val layoutParams = LinearLayout.LayoutParams(800, 600)
        doughnutChart.layoutParams = layoutParams

        // Agregar el gráfico al contenedor
        container.addView(doughnutChart)

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
        container.gravity = Gravity.CENTER
    }


    private fun obtenerDatosExamenes(respuesta: List<AdminExamenesData>?): List<DatosExamen> {
        val datosExamenes = mutableListOf<DatosExamen>()

        respuesta?.forEach { adminExamenesData ->
            val tituloExamen = adminExamenesData.TituloExamen
            val idExamen = adminExamenesData.IdExamen

            // Agregar a la lista de DatosExamen
            datosExamenes.add(DatosExamen(tituloExamen, idExamen))
        }

        return datosExamenes
    }

}