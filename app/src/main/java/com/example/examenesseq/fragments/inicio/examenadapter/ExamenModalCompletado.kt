package com.example.examenesseq.fragments.inicio.examenadapter

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.examenesseq.R
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import java.text.SimpleDateFormat
import java.util.Locale

class ExamenModalCompletado(private val examen: Examen, private val examenUsuario: ExamenUsuario) : DialogFragment() {
    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_examen_completado, null)

        view.findViewById<TextView>(R.id.tituloModal).text = examen.TituloExamen

        val descripcionSinEtiquetas=examen.DescripcionExamen.replace("<p>".toRegex(), "")?.replace("</p>".toRegex(), "")

        if (examen.DescripcionExamen.contains("<p>") && examen.DescripcionExamen.contains("</p>")){
            view.findViewById<TextView>(R.id.descripcionModal).text = descripcionSinEtiquetas
        }else{
            view.findViewById<TextView>(R.id.descripcionModal).text = examen.DescripcionExamen
        }

        val examenUsuarios=examenUsuario
        val calificacion=examenUsuario.TotalCalificacion

        if(calificacion>=60){
            view.findViewById<TextView>(R.id.tituloCalif).text="Has aprobado el examen con "
            view.findViewById<TextView>(R.id.calificacion).text="$calificacion"
            view.findViewById<TextView>(R.id.calificacion).setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }else{
            view.findViewById<TextView>(R.id.tituloCalif).text="Has reprobado el examen con "
            view.findViewById<TextView>(R.id.calificacion).text="$calificacion"
            view.findViewById<TextView>(R.id.calificacion).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        val fechaFin=examenUsuario.FechaFinal.toString()
        view.findViewById<TextView>(R.id.fechaFinalizacion).text=parsearFecha(fechaFin)

        val tiempoTranscurrido=examenUsuario.TiempoTranscurrido
        val tiempoConvertido=conversorHoras(tiempoTranscurrido)

        view.findViewById<TextView>(R.id.tiempoTranscurrido).text=tiempoConvertido


        builder.setView(view)
            .setTitle("Detalles del examen")
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }

    fun conversorHoras(duracionExam: Int): String {
        if(duracionExam>=60) {
            val duracionHoras = duracionExam / 60
            return "$duracionHoras horas"
        }else {
            return "$duracionExam minutos"
        }
    }

    fun parsearFecha(fechas: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedDate = dateFormat.parse(fechas)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US).format(parsedDate)

        return formattedDate.toString()
    }
}



