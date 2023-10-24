package com.example.examenesseq.fragments.inicio.examenadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.R
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario

class ExamenAdapter(var con: Context, var list: List<Examen>, var list2: List<ExamenUsuario>): RecyclerView.Adapter<ExamenAdapter.ViewHolder>(){
    inner class ViewHolder(v:View): RecyclerView.ViewHolder(v){
        var txtTitulo=v.findViewById<TextView>(R.id.tituloExamenTxt)
        var txtCalificacion=v.findViewById<TextView>(R.id.txtCalificacion)
        var txtCalifNumero=v.findViewById<TextView>(R.id.txtCalifNum)
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

        val examen = list[position]
        val examenUsuarioDetails= list2.find { it.IdExamen == examen.IdExamen }

        if (examenUsuarioDetails != null) {
            if(examenUsuarioDetails.Estado==2 || examenUsuarioDetails.Estado==3){
                if (examenUsuarioDetails.TotalCalificacion>60){
                    holder.txtCalifNumero.setTextColor(ContextCompat.getColor(con, R.color.green))
                    holder.txtCalifNumero.text=examenUsuarioDetails.TotalCalificacion.toString()
                }else{
                    holder.txtCalifNumero.text=examenUsuarioDetails.TotalCalificacion.toString()
                    holder.txtCalifNumero.setTextColor(ContextCompat.getColor(con, R.color.red))
                }
            }else{
                val avisoEstadoExamen="En proceso"
                holder.txtCalificacion.text=avisoEstadoExamen
                holder.txtCalifNumero.visibility=View.INVISIBLE
            }
        }else{
            val avisoEstadoExamen="No iniciado"
            holder.txtCalificacion.text=avisoEstadoExamen
            holder.txtCalifNumero.visibility=View.INVISIBLE
        }
        val estadoExamen=examen.Activo

        if (estadoExamen == 1) {
            holder.Estado.setCardBackgroundColor(ContextCompat.getColor(con, R.color.green))
        } else {
            holder.Estado.setCardBackgroundColor(ContextCompat.getColor(con, R.color.red))
        }

        val estado = examenUsuarioDetails?.Estado

        holder.itemView.setOnClickListener {
            if (estado == 1) {
                val detalleExamenModal = ExamenModal(examen)
                detalleExamenModal.show((con as AppCompatActivity).supportFragmentManager, "DetalleExamenModal")
            } else {
                if (examenUsuarioDetails != null) {
                    val detalleExamenModalCompletado = ExamenModalCompletado(examen, examenUsuarioDetails)
                    detalleExamenModalCompletado.show((con as AppCompatActivity).supportFragmentManager, "DetalleExamenModalCompletado")
                } else {
                    val detalleExamenModal = ExamenModal(examen)
                    detalleExamenModal.show((con as AppCompatActivity).supportFragmentManager, "DetalleExamenModal")
                }
            }
        }
        when(estado){
            1 -> holder.imgExamen.setImageResource(R.drawable.book_alert_outline)
            2 -> holder.imgExamen.setImageResource(R.drawable.book_check_outline)
            3 -> holder.imgExamen.setImageResource(R.drawable.book_check_outline)
            else -> holder.imgExamen.setImageResource(R.drawable.book_alert_outline)
        }
    }
}