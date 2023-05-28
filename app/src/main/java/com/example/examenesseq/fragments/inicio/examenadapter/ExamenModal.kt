package com.example.examenesseq.fragments.inicio.examenadapter

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.examenesseq.R
import com.example.examenesseq.model.examen.Examen
import java.text.SimpleDateFormat
import java.util.Locale

class ExamenModal(private val examen: Examen) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_examen, null)


        view.findViewById<TextView>(R.id.tituloModal).text = examen.TituloExamen
        val descripcionSinEtiquetas=examen.DescripcionExamen.replace("<p>".toRegex(), "")?.replace("</p>".toRegex(), "")

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






        builder.setView(view)
            .setTitle("Detalles del examen")
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
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
        val formattedDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US).format(parsedDate)

        return formattedDate.toString()
    }
}