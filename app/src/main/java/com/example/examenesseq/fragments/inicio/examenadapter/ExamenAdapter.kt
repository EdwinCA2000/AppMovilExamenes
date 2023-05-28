package com.example.examenesseq.fragments.inicio.examenadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.R
import com.example.examenesseq.fragments.inicio.perfil.ExamenModal
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario

class examenAdapter(var con: Context, var list: List<Examen>, var list2: List<ExamenUsuario>): RecyclerView.Adapter<examenAdapter.ViewHolder>(){
    inner class ViewHolder(v:View): RecyclerView.ViewHolder(v){
        var txtTitulo=v.findViewById<TextView>(R.id.tituloExamenTxt)
        var txtDescription=v.findViewById<TextView>(R.id.descripcionExamentxt)
        var Estado=v.findViewById<CardView>(R.id.estadoExamen)
        var imgExamen=v.findViewById<ImageView>(R.id.ic_examen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(con).inflate(R.layout.elementos_examen,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitulo.text = list[position].TituloExamen
        holder.txtDescription.text = list[position].DescripcionExamen

        val estadoExamen = list[position].EstadoExamen

        if (estadoExamen == 1) {
            holder.Estado.setCardBackgroundColor(ContextCompat.getColor(con, R.color.green))
        } else {
            holder.Estado.setCardBackgroundColor(ContextCompat.getColor(con, R.color.red))
        }

        val examen = list[position]
        holder.itemView.setOnClickListener {
            val detalleExamenModal = ExamenModal(examen)
            detalleExamenModal.show((con as AppCompatActivity).supportFragmentManager, "DetalleExamenModal")
            Toast.makeText(con, "Información del examen: ${examen.TituloExamen}", Toast.LENGTH_SHORT).show()
            // Puedes abrir una actividad o fragmento con más detalles del examen si lo deseas
        }

        val examenUsuario = list2.find { it.IdExamen == examen.IdExamen }

        // Obtener el estado del ExamenUsuario
        val estado = examenUsuario?.Estado

        when(estado){
            1 -> holder.imgExamen.setImageResource(R.drawable.book_alert_outline)
            2 -> holder.imgExamen.setImageResource(R.drawable.book_check_outline)
            3 -> holder.imgExamen.setImageResource(R.drawable.book_check_outline)
            else -> holder.imgExamen.setImageResource(R.drawable.book_alert_outline)
        }


    }


}