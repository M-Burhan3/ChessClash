package com.example.ajedrezprueba.adapters

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ajedrezprueba.R
import com.example.ajedrezprueba.databinding.DisenoLayoutBinding
import com.example.ajedrezprueba.models.DiseñoModel
import com.example.ajedrezprueba.preferences.Preferences

class DiseñoViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = DisenoLayoutBinding.bind(v)

    fun render(diseño: DiseñoModel, aplicarDiseño: (Int) -> Unit) {

        binding.tvDiseO.text = diseño.titulo
        binding.ivDiseO.setImageResource(diseño.rutaImagen)

        binding.cvDiseO.setOnClickListener {
            aplicarDiseño(diseño.id)
            guardarDiseñoElegido(diseño.id)
            marcarDiseñoElegido(diseño.id)
        }


        // Poner el cardview centrado
        val params = binding.cvDiseO.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER_HORIZONTAL


        if (diseño.copasNecesarias != 0) {
            binding.tvCopasNecesarias.setText(diseño.copasNecesarias.toString())
            binding.tvCopasNecesarias.setTextColor(Color.RED)
        }

        if (!diseño.isBloqued) {
            binding.ivCandado.setImageResource(R.drawable.tick)
            binding.cvDiseO.isClickable = true
        } else {
            binding.ivCandado.setImageResource(R.drawable.candado2)
            binding.cvDiseO.isClickable = false
        }


        marcarDiseñoElegido(diseño.id)




    }

    private fun guardarDiseñoElegido(diseñoId: Int) {
        if (diseñoId % 2 == 0) {
            Preferences(itemView.context).setDiseñoFichaElegido(diseñoId)
        } else {
            Preferences(itemView.context).setDiseñoTableroElegido(diseñoId)
        }
    }

    private fun marcarDiseñoElegido(diseñoId: Int) {
        if (Preferences(itemView.context).getDiseñoFichaElegido() == diseñoId) {
            binding.clDiseO.setBackgroundResource(R.drawable.fondo_amarillo_brillante)
        }
        if (Preferences(itemView.context).getDiseñoTableroElegido() == diseñoId) {
            binding.clDiseO.setBackgroundResource(R.drawable.fondo_amarillo_brillante)
        }
        if (diseñoId != Preferences(itemView.context).getDiseñoTableroElegido() && diseñoId != Preferences(itemView.context).getDiseñoFichaElegido()) {
            binding.clDiseO.setBackgroundResource(R.drawable.fondo_cardview2)
        }
    }

}