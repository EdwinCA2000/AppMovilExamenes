package com.example.examenesseq.fragments.adminExamen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.R


class AdminExamenAdapter (var con: Context, var list: List<AdminExamenesData>): RecyclerView.Adapter<AdminExamenAdapter.ViewHolder>(){

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var txtTitulo=v.findViewById<TextView>(R.id.tituloExamenTxt)
        var llAdminExamen= v.findViewById<LinearLayout>(R.id.llExamenAdmin)

    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        val view= LayoutInflater.from(con).inflate(R.layout.elementos_adminexamen,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitulo.text = list[position].TituloExamen

        val estadoExamen = list[position].Activo

        // Cambiar el fondo según el estado
        if (estadoExamen == 1) {
            holder.llAdminExamen.setBackgroundResource(R.drawable.border_stroke_activo)
        } else if (estadoExamen == 0) {
            holder.llAdminExamen.setBackgroundResource(R.drawable.border_stroke_inactivo)
        }
    }


    override fun getItemCount(): Int {
        return list.count()
    }

}