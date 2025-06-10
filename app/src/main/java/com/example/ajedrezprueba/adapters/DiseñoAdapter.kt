package com.example.ajedrezprueba.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ajedrezprueba.R
import com.example.ajedrezprueba.models.DiseñoModel

class DiseñoAdapter(val listaDiseños: MutableList<DiseñoModel>, private val aplicarDiseño: (Int) -> Unit): RecyclerView.Adapter<DiseñoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseñoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.diseno_layout, parent, false)
        return DiseñoViewHolder(v)
    }

    override fun getItemCount() = listaDiseños.size

    override fun onBindViewHolder(holder: DiseñoViewHolder, position: Int) {
        holder.render(listaDiseños[position], aplicarDiseño)
    }

}