package com.example.examenesseq.secciones.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.examenesseq.secciones.data.EstadoSeccionData
import com.example.examenesseq.R

class EstadoSeccionAdapter (context: Context, val options: List<EstadoSeccionData>) :
    ArrayAdapter<EstadoSeccionData>(context, R.layout.item_estadoexamen, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_estadoexamen, parent, false)
        val item = getItem(position)
        val labelEstado = view.findViewById<TextView>(R.id.labelEstado)
        labelEstado.text = item?.label ?: ""
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}