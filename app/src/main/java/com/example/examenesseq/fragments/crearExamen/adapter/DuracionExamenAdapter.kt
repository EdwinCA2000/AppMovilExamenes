package com.example.examenesseq.fragments.crearExamen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.examenesseq.R
import com.example.examenesseq.fragments.crearExamen.data.DuracionExamenData

class DuracionExamenAdapter(context: Context, val options: List<DuracionExamenData>) :
    ArrayAdapter<DuracionExamenData>(context, R.layout.item_duracionexamen, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_duracionexamen, parent, false)
        val item = getItem(position)
        val labelDuracion = view.findViewById<TextView>(R.id.labelDuracion)
        labelDuracion.text = item?.label ?: ""
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
