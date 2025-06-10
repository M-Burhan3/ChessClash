package com.example.ajedrezprueba.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ajedrezprueba.R
import com.example.ajedrezprueba.models.UsuarioModel

class JugadoresAdapter(
    var lista: MutableList<UsuarioModel>,
    private val mandarSolicitud: (String) -> Unit
): RecyclerView.Adapter<JugadoresViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadoresViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.jugador_layout, parent, false)
        return JugadoresViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: JugadoresViewHolder, position: Int) {
        holder.render(lista[position], mandarSolicitud)
    }

}