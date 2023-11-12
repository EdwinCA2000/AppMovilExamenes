package com.example.examenesseq.fragments.dashboard.historialUsers

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examenesseq.databinding.FragmentHistorialExamenUsersBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.model.examen.ExamenUsuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HistorialExamenUsers : Fragment() {

    private var _binding: FragmentHistorialExamenUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var examenHistorialAdapterUsuarios: ExamenHistorialAdapterUsuarios

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHistorialExamenUsersBinding.inflate(inflater, container, false)
        binding.recyclerHistorial.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    fun obtenerHistorialExamenes(idUsuario: Int) {
        if (idUsuario != null) {
            apiServicio.obtenerExamenUsuario(idUsuario).enqueue(object :
                Callback<List<ExamenUsuario>> {
                override fun onResponse(call: Call<List<ExamenUsuario>>, response: Response<List<ExamenUsuario>>) {
                    if (response.isSuccessful) {
                        val exameneusuario = response.body()

                        if (!exameneusuario.isNullOrEmpty()) {
                            examenHistorialAdapterUsuarios= ExamenHistorialAdapterUsuarios(requireContext(),exameneusuario)
                            binding.recyclerHistorial.adapter = examenHistorialAdapterUsuarios
                        }else{
                            examenHistorialAdapterUsuarios= ExamenHistorialAdapterUsuarios(requireContext(),
                                emptyList()
                            )
                            binding.iconAviso.visibility=View.VISIBLE
                            binding.textAviso.visibility=View.VISIBLE
                        }
                    } else {
                        Toast.makeText(requireContext(), "Hubo error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ExamenUsuario>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("API Failure", "Error: ${t.message}", t)
                }

            })
        }
    }
}