package com.example.ajedrezprueba.adapters

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.ajedrezprueba.databinding.JugadorLayoutBinding
import com.example.ajedrezprueba.models.UsuarioModel
import com.google.firebase.auth.FirebaseAuth

class JugadoresViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = JugadorLayoutBinding.bind(v)

    fun render(c: UsuarioModel, mandarMensaje: (String) -> Unit) {
        binding.tvNombreJugador.text = c.nombre

        //--------------------------------------- Imagen de Jugador
        val base64 = c.fotoBase64
        base64.let {
            val bytes = Base64.decode(it, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.ivJugador.setImageBitmap(bmp)
        }
        //---------------------------------------

        binding.btnMandarSolicitud.setOnClickListener {
            mandarMensaje(c.usuario)
        }


        val user = FirebaseAuth.getInstance().currentUser
        if (c.usuario == user?.email) {
            binding.btnMandarSolicitud.isEnabled = false
            binding.btnMandarSolicitud.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        }
    }
}