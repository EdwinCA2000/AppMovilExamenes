package com.example.examenesseq.fragments.dashboard.historialUsers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.R
import com.example.examenesseq.model.examen.ExamenUsuario
import java.text.SimpleDateFormat
import java.util.Locale

class ExamenHistorialAdapterUsuarios(var con: Context, var list: List<ExamenUsuario>): RecyclerView.Adapter<ExamenHistorialAdapterUsuarios.ViewHolder>(){
    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var txtTitulo=v.findViewById<TextView>(R.id.tituloExamen)
        var txtFechaFin=v.findViewById<TextView>(R.id.fechaFinExamen)
        var txtDuracionExamen=v.findViewById<TextView>(R.id.duracionExamen)
        var txtCalificacion=v.findViewById<TextView>(R.id.calificacionExamen)
        var calificacionHistorial=v.findViewById<TextView>(R.id.calificacionHistorial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(con).inflate(R.layout.elementos_historial_users,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val examenHistorial = list[position]

        val tituloExamen=examenHistorial.TituloExamen
        val fechaFin=examenHistorial.FechaFinal.toString()
        val duracionExamen=examenHistorial.TiempoTranscurrido
        val calificacion= examenHistorial.TotalCalificacion

        holder.txtTitulo.text=tituloExamen
        holder.txtFechaFin.text=parsearFecha(fechaFin)
        holder.txtDuracionExamen.text=conversorHoras(duracionExamen)

        if(calificacion>=60){
            holder.calificacionHistorial.text="Aprobó el examen con "
            holder.txtCalificacion.text="$calificacion"
            holder.txtCalificacion.setTextColor(ContextCompat.getColor(con, R.color.green))
        }else{
            holder.calificacionHistorial.text="Reprobó el examen con "
            holder.txtCalificacion.text="$calificacion"
            holder.txtCalificacion.setTextColor(ContextCompat.getColor(con,R.color.red))
        }
    }
    fun conversorHoras(duracionExam: Int): String {
        if(duracionExam>=60) {
            val duracionHoras = duracionExam / 60
            return "$duracionHoras horas"
        }else {
            return "$duracionExam minutos"
        }
    }

    fun parsearFecha(fechas: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedDate = dateFormat.parse(fechas)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US).format(parsedDate)

        return formattedDate.toString()
    }

}