package com.example.examenesseq.fragments.dashboard.usuarios

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examenesseq.databinding.FragmentAdministrarUsuariosBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.ModuloUsuarioRespuesta
import com.example.examenesseq.util.PreferenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdministrarUsuarios : Fragment() {

    private var _binding: FragmentAdministrarUsuariosBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private lateinit var usuariosAdapter: UsuariosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdministrarUsuariosBinding.inflate(inflater, container, false)
        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())

        binding.recargarUsuarios.setOnRefreshListener {
            obtenerModuloUsuarios()
        }
        obtenerModuloUsuarios()
        return binding.root
    }


    fun obtenerModuloUsuarios(){
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        apiServicio.obtenerModuloUsuarios().enqueue(object: Callback<ModuloUsuarioRespuesta> {
            override fun onResponse(call: Call<ModuloUsuarioRespuesta>, response: Response<ModuloUsuarioRespuesta>) {
                binding.recargarUsuarios.isRefreshing = false
                if (response.isSuccessful){
                    val usuarios=response.body()
                    if (usuarios != null) {
                        usuariosAdapter=UsuariosAdapter(requireContext(),usuarios)
                        binding.recyclerUsers.adapter=usuariosAdapter


                    }else{
                        val usuariosAdapter = usuarios?.let {
                            UsuariosAdapter(requireContext(),
                                it
                            )
                        }
                        binding.recyclerUsers.adapter = usuariosAdapter
                    }
                }
            }

            override fun onFailure(call: Call<ModuloUsuarioRespuesta>, t: Throwable) {
                Toast.makeText(requireContext(), "No se logro conectar al servidor para obtener los usuarios", Toast.LENGTH_SHORT).show()
            }

        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}