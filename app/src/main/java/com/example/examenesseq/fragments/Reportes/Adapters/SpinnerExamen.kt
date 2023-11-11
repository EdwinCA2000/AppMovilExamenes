package com.example.examenesseq.fragments.Reportes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.examenesseq.fragments.Reportes.Data.DatosExamen
import com.example.examenesseq.R

class SpinnerExamen (context: Context, datosExamenes:List<DatosExamen>):

    ArrayAdapter<DatosExamen>(context, R.layout.item_duracionexamen, datosExamenes) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_duracionexamen, parent, false)
        val item = getItem(position)
        val labelDuracion = view.findViewById<TextView>(R.id.labelDuracion)
        labelDuracion.text = item?.tituloExamen ?: ""
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }




}