package com.example.examenesseq

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.model.examen.Examen

class examenAdapter(var con: Context, var list: List<Examen>): RecyclerView.Adapter<examenAdapter.ViewHolder>(){
    inner class ViewHolder(v:View): RecyclerView.ViewHolder(v){
        var txtTitulo=v.findViewById<TextView>(R.id.tituloExamenTxt)
        var txtDescription=v.findViewById<TextView>(R.id.descripcionExamentxt)
        var txtEstado=v.findViewById<TextView>(R.id.estadoTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(con).inflate(R.layout.elementos_examen,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitulo.text=list[position].TituloExamen
        holder.txtDescription.text=list[position].DescripcionExamen
        holder.txtEstado.text=list[position].EstadoExamen.toString()
    }

}