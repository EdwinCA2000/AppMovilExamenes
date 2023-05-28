package com.example.examenesseq.fragments.inicio.perfil

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.examenesseq.R
import com.example.examenesseq.model.examen.Examen

class ExamenModal(private val examen: Examen) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_examen, null)

        // Configurar los elementos del modal con los datos del examen
        // Ejemplo:
        view.findViewById<TextView>(R.id.tituloModal).text = examen.TituloExamen
        view.findViewById<TextView>(R.id.descripcionModal).text = examen.DescripcionExamen

        builder.setView(view)
            .setTitle("Detalles del examen")
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}
