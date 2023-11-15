package com.example.examenesseq.fragments.secciones.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.R
import com.example.examenesseq.model.examen.Secciones

class AdminSeccionesAdapter (var con: Context, var list: List<Secciones>): RecyclerView.Adapter<AdminSeccionesAdapter.ViewHolder>(){

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
        var txtTitulo = v.findViewById<TextView>(R.id.tituloSecciontxt)
        var llSeccionExamen = v.findViewById<LinearLayout>(R.id.llSeccionesAdmin)
        var llordenSeccion=v.findViewById<TextView>(R.id.orden_Seccion)

        init {
            llSeccionExamen.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position, list[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view= LayoutInflater.from(con).inflate(R.layout.elementos_secciones,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitulo.text = list[position].TituloSeccion
        holder.llordenSeccion.text=list[position].Orden.toString()

        val estadoExamen = list[position].Activo

        // Cambiar el fondo según el estado

        if (selectedPosition == position) {
            holder.llSeccionExamen.setBackgroundResource(R.drawable.border_stroke_selected)
        }else if (estadoExamen == 1){
            holder.llSeccionExamen.setBackgroundResource(R.drawable.border_stroke_activo)
        }else{
            holder.llSeccionExamen.setBackgroundResource(R.drawable.border_stroke_inactivo)
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position, list[position])
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, seccionesData: Secciones)
    }
}