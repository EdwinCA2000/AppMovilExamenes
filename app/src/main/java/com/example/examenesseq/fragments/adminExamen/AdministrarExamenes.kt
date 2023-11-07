package com.example.examenesseq.fragments.adminExamen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentAdministrarExamenesBinding
import com.example.examenesseq.datos.ApiServicio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdministrarExamenes : Fragment() {

    private var _binding: FragmentAdministrarExamenesBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    private lateinit var adminExamenAdapter: AdminExamenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdministrarExamenesBinding.inflate(inflater, container, false)
        binding.listaExamenesAdmin.layoutManager = LinearLayoutManager(requireContext())
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_administrarExamenes_to_dashboard)
        }
        obtenerExamenes()
        agregarExamen()
        return binding.root
    }

    private fun obtenerExamenes(){
        apiServicio.getExamenesAdmin().enqueue(object : Callback<List<AdminExamenesData>>{
            override fun onResponse(
                call: Call<List<AdminExamenesData>>,
                response: Response<List<AdminExamenesData>>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    adminExamenAdapter = AdminExamenAdapter(requireContext(), respuesta!!)
                    binding.listaExamenesAdmin.adapter = adminExamenAdapter
                }
            }

            override fun onFailure(call: Call<List<AdminExamenesData>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }

    private fun agregarExamen(){
        binding.fabAgregarExamen.setOnClickListener{
            findNavController().navigate(R.id.action_administrarExamenes_to_crearExamen1)
        }
    }
}