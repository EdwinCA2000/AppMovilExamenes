package com.example.examenesseq.fragments.secciones

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.examenesseq.R
import com.example.examenesseq.model.examen.Secciones
import org.jsoup.Jsoup

class ModalDescripcionSeccion (private val secciones: Secciones): DialogFragment(){

    private lateinit var txtDescripcionModalSeccion: TextView
    private lateinit var etDescripcionSeccion: EditText
    private lateinit var btnCerrarModal: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_descripcion_seccion, null)

        txtDescripcionModalSeccion= view.findViewById(R.id.descripcionModalSeccion)
        etDescripcionSeccion=view.findViewById(R.id.etDescripcionSeccionModal)
        btnCerrarModal=view.findViewById(R.id.btnCerrarModalSeccion)

        cargarDescripcion(secciones)

        btnCerrarModal.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

    private fun cargarDescripcion(seccion: Secciones){
        txtDescripcionModalSeccion.text="Descripción de ${seccion.TituloSeccion}"
        etDescripcionSeccion.setText(eliminarEtiquetasHTML(seccion.DescripcionSeccion))
    }

    private fun eliminarEtiquetasHTML(textoHTML: String): String {
        val documento = Jsoup.parse(textoHTML)
        return documento.text()
    }

}