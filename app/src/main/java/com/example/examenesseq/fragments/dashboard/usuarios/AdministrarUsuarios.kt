package com.example.examenesseq.fragments.dashboard.usuarios

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.databinding.FragmentAdministrarUsuariosBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.ModuloUsuarioRespuesta
import com.example.examenesseq.datos.respuesta.RespuestaEliminarUser
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
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())

        binding.recargarUsuarios.setOnRefreshListener {
            obtenerModuloUsuarios()
        }
        obtenerModuloUsuarios()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,  ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No permitir el movimiento de elementos
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Eliminar el elemento cuando se realiza un deslizamiento
                val position = viewHolder.absoluteAdapterPosition
                eliminarUsuario(position)
            }
        })

        // Vincular el ItemTouchHelper con el RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.recyclerUsers)
    }

    private fun eliminarUsuario(position: Int) {
        val usuarioAEliminar = usuariosAdapter.list.Objeto[position]
        apiServicio.eliminarUsuario(usuarioAEliminar.IdUsuario).enqueue(object : Callback<RespuestaEliminarUser>{
            override fun onResponse(
                call: Call<RespuestaEliminarUser>,
                response: Response<RespuestaEliminarUser>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    if (respuesta != null) {
                        val nuevaLista = usuariosAdapter.list.Objeto.toMutableList()
                        nuevaLista.removeAt(position)

                        // Actualizar la lista en el adaptador
                        usuariosAdapter.list.Objeto = nuevaLista

                        // Notificar al adaptador sobre el cambio
                        usuariosAdapter.notifyItemRemoved(position)

                        Toast.makeText(requireContext(),"Se elimino el usuario ${usuarioAEliminar.Nombres + " "+ usuarioAEliminar.Apellido1+" "+usuarioAEliminar.Apellido2}",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaEliminarUser>, t: Throwable) {
                Toast.makeText(requireContext(),"No se logró conectar al servidor para eliminar el usuario",Toast.LENGTH_SHORT).show()
            }

        })
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