package com.example.examenesseq.fragments.adminExamen

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.examenesseq.R
import org.jsoup.Jsoup

class ModalDescripcionExamen (private val adminExamenesData: AdminExamenesData): DialogFragment(){

    private lateinit var txtDescripcionModal: TextView
    private lateinit var etDescripcionExamen: EditText
    private lateinit var etBienvenidaExamen: EditText
    private lateinit var btnCerrarModal: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_descripcion_examen, null)

        txtDescripcionModal= view.findViewById(R.id.descripcionModal)
        etDescripcionExamen=view.findViewById(R.id.etDescripcionExamenModal)
        etBienvenidaExamen=view.findViewById(R.id.etBievenidaExamenModal)
        btnCerrarModal=view.findViewById(R.id.btnCerrarModalExamen)

        cargarDescYBienvenida(adminExamenesData)

        btnCerrarModal.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

    private fun cargarDescYBienvenida(examen: AdminExamenesData){
        txtDescripcionModal.text="Descripción y Bienvenida del ${examen.TituloExamen}"
        etDescripcionExamen.setText(eliminarEtiquetasHTML(examen.DescripcionExamen))
        etBienvenidaExamen.setText(eliminarEtiquetasHTML(examen.TextoBienvenida))
    }

    private fun eliminarEtiquetasHTML(textoHTML: String): String {
        val documento = Jsoup.parse(textoHTML)
        return documento.text()
    }

}