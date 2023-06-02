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
import com.example.examenesseq.model.usuario.Usuario
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneUsuarios
import com.example.examenesseq.util.PreferenceHelper.getModuloUsuarios
import com.example.examenesseq.util.PreferenceHelper.saveModuloUsuarios
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
        obtenerModuloUsuarios()
        return binding.root
    }


    private fun obtenerModuloUsuarios(){
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        apiServicio.obtenerModuloUsuarios().enqueue(object: Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful){
                    val usuarios=response.body()
                    if (usuarios != null) {
                        usuariosAdapter=UsuariosAdapter(requireContext(),usuarios)
                        binding.recyclerUsers.adapter=usuariosAdapter
                        preferences.saveModuloUsuarios(usuarios)
                    }else{
                        usuariosAdapter=UsuariosAdapter(requireContext(), emptyList())
                        binding.recyclerUsers.adapter=usuariosAdapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(requireContext(), "No se logro conectar al servidor para obtener los usuarios", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}