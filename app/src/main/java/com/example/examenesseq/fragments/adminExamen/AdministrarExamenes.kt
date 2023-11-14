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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.ExamenViewModel
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentAdministrarExamenesBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.RespuestaEliminarExamen
import com.example.examenesseq.datos.respuesta.RespuestaEliminarUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdministrarExamenes : Fragment(),AdminExamenAdapter.OnItemClickListener {
    private var selectedPosition: Int? = null

    private var _binding: FragmentAdministrarExamenesBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    private lateinit var adminExamenAdapter: AdminExamenAdapter
    private lateinit var examenViewModel: ExamenViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdministrarExamenesBinding.inflate(inflater, container, false)
        examenViewModel = ViewModelProvider(requireActivity())[ExamenViewModel::class.java]
        binding.listaExamenesAdmin.layoutManager = LinearLayoutManager(requireContext())
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_administrarExamenes_to_dashboard)
        }
        obtenerExamenes()
        agregarExamen()
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
                eliminarExamen(position)
            }
        })

        // Vincular el ItemTouchHelper con el RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.listaExamenesAdmin)
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
                    adminExamenAdapter.setOnItemClickListener(this@AdministrarExamenes)
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

    private fun eliminarExamen(idExamen: Int){
        val examenAeliminar = adminExamenAdapter.list[idExamen]
        apiServicio.deleteExamen(examenAeliminar.IdExamen).enqueue(object : Callback<RespuestaEliminarExamen>{
            override fun onResponse(
                call: Call<RespuestaEliminarExamen>,
                response: Response<RespuestaEliminarExamen>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    if (respuesta != null) {
                        val nuevaLista = adminExamenAdapter.list.toMutableList()
                        nuevaLista.removeAt(idExamen)

                        // Actualizar la lista en el adaptador
                        adminExamenAdapter.list = nuevaLista

                        // Notificar al adaptador sobre el cambio
                        adminExamenAdapter.notifyItemRemoved(idExamen)

                        Toast.makeText(requireContext(),"Se elimino el examen ${examenAeliminar.TituloExamen}",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaEliminarExamen>, t: Throwable) {
                Toast.makeText(requireContext(),"No se logró conectar al servidor para eliminar el usuario",Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onItemClick(position: Int, examenData: AdminExamenesData) {
        examenViewModel.selectedExamenData = examenData
        if (selectedPosition == position) {
            // Ya está seleccionado, deseleccionar
            selectedPosition = null
            binding.btnEditar.visibility = View.GONE
        } else {
            // Seleccionar el nuevo elemento
            selectedPosition = position
            binding.btnEditar.visibility = View.VISIBLE
            binding.btnEditar.setOnClickListener{
                findNavController().navigate(R.id.action_administrarExamenes_to_editarExamen)
            }
        }

        // Notificar al adaptador sobre el cambio en la selección
        adminExamenAdapter.setSelectedPosition(selectedPosition)
    }


}