package com.example.examenesseq


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.examenesseq.databinding.FragmentDashboardBinding
import com.example.examenesseq.databinding.FragmentInicioBinding
import com.example.examenesseq.datos.ApiServicio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class dashboard : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    /*
    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create()
    }
    */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //examenes()
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }
/*
    fun examenes(){
        // Realizar la solicitud para obtener los exámenes disponibles
        apiServicio.getExamenesDisponibles().enqueue(object : Callback<List<Examen>> {
            override fun onResponse(call: Call<List<Examen>>, response: Response<List<Examen>>) {
                if (response.isSuccessful) {
                    val examenes = response.body()
                    if (examenes != null) {
                        // Mostrar los datos de los exámenes en la vista
                        // Por ejemplo, puedes asignarlos a un RecyclerView o ListView
                        // o mostrarlos en un TextView
                        binding.prueba.text = examenes.joinToString("\n") { it.TituloExamen }
                    } else {
                        // No hay exámenes disponibles
                        binding.prueba.text = "No hay exámenes disponibles"
                    }
                } else {
                    // La solicitud no fue exitosa
                    binding.prueba.text = "Error al obtener los exámenes"
                }
            }

            override fun onFailure(call: Call<List<Examen>>, t: Throwable) {
                // Error en la comunicación con la API
                binding.prueba.text = "Error de conexión"
            }
        })
    }
    */
}