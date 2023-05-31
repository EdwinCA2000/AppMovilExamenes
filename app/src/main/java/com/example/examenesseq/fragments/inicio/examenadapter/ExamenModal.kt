package com.example.examenesseq.fragments.inicio.examenadapter

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.examenesseq.R
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.examen.DaoExamen
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.Secciones
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.getCantidadPreguntas
import com.example.examenesseq.util.PreferenceHelper.getSecciones
import com.example.examenesseq.util.PreferenceHelper.saveCantidadPreguntas
import com.example.examenesseq.util.PreferenceHelper.saveSecciones
import com.example.examenesseq.util.PreferenceHelper.setJSessionId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ExamenModal(private val examen: Examen) : DialogFragment() {

    val daoExamen = DaoExamen()

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_examen, null)


        view.findViewById<TextView>(R.id.tituloModal).text = examen.TituloExamen
        val descripcionSinEtiquetas=
            examen.DescripcionExamen.replace("<p>".toRegex(), "").replace("</p>".toRegex(), "")

        if (examen.DescripcionExamen.contains("<p>") && examen.DescripcionExamen.contains("</p>")){
            view.findViewById<TextView>(R.id.descripcionModal).text = descripcionSinEtiquetas
        }else{
            view.findViewById<TextView>(R.id.descripcionModal).text = examen.DescripcionExamen
        }

        //fechas
        val fechaInicioExamen=examen.FechaInicio.toString()
        val fechaFinalExamen=examen.FechaFinal.toString()

        view.findViewById<TextView>(R.id.fechaInicio).text = parsearFecha(fechaInicioExamen)
        view.findViewById<TextView>(R.id.fechaFinal).text = parsearFecha(fechaFinalExamen)

        //Tiempo transcurrido
        val duracionExamen= examen.TiempoExamen
        conversorHoras(duracionExamen)

        val idExamen= examen.IdExamen
        val cantidadSecciones= daoExamen.obtenerCantidadSecciones(requireContext(),idExamen)






        if(cantidadSecciones==1){
            view.findViewById<TextView>(R.id.cantidadPreguntas).text="$cantidadSecciones sección"
        }else{
            view.findViewById<TextView>(R.id.cantidadPreguntas).text="$cantidadSecciones secciones"
        }

        val contenedorSecciones = view.findViewById<LinearLayout>(R.id.contenedorTitulos)
        val titulosSecciones = daoExamen.obtenerTitulosSecciones(requireContext(), idExamen)

        val idSecciones=daoExamen.obtenerIdsSecciones(requireContext(),idExamen)


        for (i in 0 until titulosSecciones.size) {
            val titulo = titulosSecciones[i]
            val idSeccion = idSecciones[i]

            val textView = TextView(requireContext())
            textView.text = "- $titulo"  // Agregar un guion antes del título
            obtenerTotalPreguntas(idSeccion) { totalPreguntas ->
                val texto = "- $titulo ($totalPreguntas preguntas)"
                textView.text = texto
            }

            contenedorSecciones.addView(textView)
        }

        builder.setView(view)
            .setTitle("Detalles del examen")
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }

    private fun obtenerTotalPreguntas(idSeccion: Int, callback: (Int) -> Unit) {
        apiServicio.obtenerTotalPreguntas(idSeccion).enqueue(object : Callback<LoginRespuesta> {
            override fun onResponse(call: Call<LoginRespuesta>, response: Response<LoginRespuesta>) {
                if (response.isSuccessful) {
                    val loginRespuesta = response.body()
                    val totalPreguntas = loginRespuesta?.Mensaje
                    if (totalPreguntas != null) {
                        val preferences = PreferenceHelper.defaultPrefs(requireContext())
                        preferences.saveCantidadPreguntas(totalPreguntas)
                        callback(totalPreguntas.toInt())
                    }
                } else {
                    Toast.makeText(requireContext(), "Hubo un error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginRespuesta>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }
        })
    }
    fun conversorHoras(duracionExam: Int){
        if(duracionExam>=60){
            val duracionHoras=duracionExam/60
            view?.findViewById<TextView>(R.id.duracionExamen)?.text ?:  "$duracionHoras horas"
        }else{
            view?.findViewById<TextView>(R.id.duracionExamen)?.text ?:  "$duracionExam minutos"
        }
    }
    fun parsearFecha(fechas: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedDate = dateFormat.parse(fechas)
        val formattedDate = parsedDate?.let {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US).format(
                it
            )
        }

        return formattedDate.toString()
    }
}