package com.example.examenesseq.fragments.inicio.examenadapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.example.examenesseq.R
import com.example.examenesseq.model.examen.ExamenUsuario


class ExamenUsuarioAdapter{
    companion object{
        @BindingAdapter("android:cambiarImageView")
        @JvmStatic
        fun imgViewExamen(imageView: ImageView, examenUsuario: ExamenUsuario){
            if(examenUsuario.Estado==2  || examenUsuario.Estado==3){
                imageView.setImageResource(R.drawable.book_check_outline)
            }else{
                imageView.setImageResource(R.drawable.book_alert_outline)
            }

        }
    }
}