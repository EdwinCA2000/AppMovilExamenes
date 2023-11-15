package com.example.examenesseq.fragments.preguntas.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.fragments.preguntas.data.ContenidoPregunta
import com.example.examenesseq.fragments.preguntas.data.Pregunta
import com.example.examenesseq.R
import com.example.examenesseq.datos.ApiServicio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminPreguntasAdapter (var con: Context, var list: List<Pregunta>): RecyclerView.Adapter<AdminPreguntasAdapter.ViewHolder>(){

    private var listener: OnItemClickListener? = null

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(con)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private var selectedPosition: Int? = null

    fun setSelectedPosition(position: Int?) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var llPreguntaExamen = v.findViewById<LinearLayout>(R.id.llPreguntasAdmin)
        var numeroPregunta=v.findViewById<TextView>(R.id.numero_Pregunta)
        var contenidoPregunta= v.findViewById<TextView>(R.id.contenidoPreguntatxt)

        init {
            llPreguntaExamen.setOnClickListener {
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
        val view= LayoutInflater.from(con).inflate(R.layout.elementos_preguntas,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.numeroPregunta.text=list[position].Numero.toString()
        val idPregunta=list[position].IdPregunta
        TieneContenido(idPregunta,holder.contenidoPregunta)

        val estadoPregunta = list[position].Activo

        // Cambiar el fondo según el estado

        if (selectedPosition == position) {
            holder.llPreguntaExamen.setBackgroundResource(R.drawable.border_stroke_selected)
        }else if (estadoPregunta == 1){
            holder.llPreguntaExamen.setBackgroundResource(R.drawable.border_stroke_activo)
        }else{
            holder.llPreguntaExamen.setBackgroundResource(R.drawable.border_stroke_inactivo)
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position, list[position])
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, preguntasData: Pregunta)
    }

    private fun TieneContenido(idPregunta: Int, contenidoTextView: TextView){
        apiServicio.getContenidoDePreguntas(idPregunta).enqueue(object :
            Callback<List<ContenidoPregunta>> {
            override fun onResponse(
                call: Call<List<ContenidoPregunta>>,
                response: Response<List<ContenidoPregunta>>
            ) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    if (respuesta != null && respuesta.isNotEmpty()) {
                        contenidoTextView.text = "Contiene contenido"
                        contenidoTextView.setTextColor(ContextCompat.getColor(con,
                            R.color.greendark
                        ))
                    }else{
                        contenidoTextView.text = "¡No existe contenido!"
                        contenidoTextView.setTextColor(ContextCompat.getColor(con, R.color.red))
                    }
                }

            }

            override fun onFailure(call: Call<List<ContenidoPregunta>>, t: Throwable) {
                Toast.makeText(con, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }
}