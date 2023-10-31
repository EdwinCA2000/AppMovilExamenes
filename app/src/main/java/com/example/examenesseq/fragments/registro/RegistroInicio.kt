package com.example.examenesseq.fragments.registro
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentRegistroInicioBinding
import com.google.android.material.tabs.TabLayoutMediator


class RegistroInicio : Fragment() {
    private lateinit var binding: FragmentRegistroInicioBinding
    private val tabTitles = arrayListOf("Información Personal", "Información Cuenta")

    private lateinit var currentFragment: Fragment
    var isSecondTabEnabled=false
    private val sharedViewModel: RegistroViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistroInicioBinding.inflate(inflater)
        setUpTabLayoutWithViewPager()
        return binding.root
    }

    private fun setUpTabLayoutWithViewPager() {
        val registroInicioAdapter = RegistroInicioAdapter(this)
        binding.viewPager.adapter = registroInicioAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        for (i in 0 until tabTitles.size) {
            val textView = LayoutInflater.from(requireContext()).inflate(R.layout.tab_title, null) as View
            binding.tabLayout.getTabAt(i)?.customView = textView
        }

        // Establece el fragment inicial (InfoPersonal)
        currentFragment = InfoPersonal()
        updateTabState()
        binding.viewPager.isUserInputEnabled = false
    }

    fun changeToSecondTab() {
        binding.viewPager.currentItem = 1
        binding.viewPager.isUserInputEnabled = true
    }

    fun updateTabState() {
        val tabStrip = binding.tabLayout.getChildAt(0) as ViewGroup
        val secondTab = tabStrip.getChildAt(1)
        if (isSecondTabEnabled) {
            secondTab.setOnTouchListener(null) // Habilita la interacción
        } else {
            secondTab.setOnTouchListener { v, event -> true } // Deshabilita la interacción
        }
    }


}