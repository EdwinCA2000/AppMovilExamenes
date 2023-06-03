package com.example.examenesseq

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.examenesseq.fragments.dashboard.usuarios.AdministrarUsuarios
import com.example.examenesseq.fragments.dashboard.usuarios.ModalDatosUsuarios


class MainActivity : AppCompatActivity() , ModalDatosUsuarios.ModalListener{
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostNavegacion) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onModalClosed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostNavegacion) as NavHostFragment
        val administrarUsuariosFragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is AdministrarUsuarios } as? AdministrarUsuarios
        administrarUsuariosFragment?.obtenerModuloUsuarios()
    }
}

