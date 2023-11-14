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

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private var selectedPosition: Int? = null

    fun setSelectedPosition(position: Int?) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var txtTitulo = v.findViewById<TextView>(R.id.tituloExamenTxt)
        var llAdminExamen = v.findViewById<LinearLayout>(R.id.llExamenAdmin)

        init {
            llAdminExamen.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position, list[position])
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        val view= LayoutInflater.from(con).inflate(R.layout.elementos_adminexamen,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitulo.text = list[position].TituloExamen

        val estadoExamen = list[position].Activo

        // Cambiar el fondo según el estado

        if (selectedPosition == position) {
            holder.llAdminExamen.setBackgroundResource(R.drawable.border_stroke_selected)
        }else if (estadoExamen == 1){
            holder.llAdminExamen.setBackgroundResource(R.drawable.border_stroke_activo)
        }else{
            holder.llAdminExamen.setBackgroundResource(R.drawable.border_stroke_inactivo)
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position, list[position])
        }
    }



    override fun getItemCount(): Int {
        return list.count()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, examenData: AdminExamenesData)
    }



}