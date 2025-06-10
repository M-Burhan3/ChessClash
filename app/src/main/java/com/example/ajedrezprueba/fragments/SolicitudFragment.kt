package com.example.ajedrezprueba.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ajedrezprueba.R

class SolicitudFragment(): Fragment() {

    private lateinit var usuarioEmisorF: String
    private var usuarioReceptorF: String? = null
    private var tipoSolicitudF: String? = null
    private var botonCancelarE: ((String) -> Unit)? = null
    private var botonAceptar: ((String) -> Unit)? = null

    companion object {
        fun newInstance(
            usuarioEmisor: String,
            usuarioReceptor: String,
            tipoSolicitud: String,
            botonCancelar: (String) -> Unit,
            botonAceptar: (String) -> Unit
        ): SolicitudFragment {
            val fragment = SolicitudFragment()
            val args = Bundle().apply {
                putString("usuarioEmisor", usuarioEmisor)
                putString("usuarioReceptor", usuarioReceptor)
                putString("tipoSolicitud", tipoSolicitud)
            }
            fragment.arguments = args
            fragment.botonCancelarE = botonCancelar
            fragment.botonAceptar = botonAceptar
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuarioEmisorF = it.getString("usuarioEmisor").toString()
            usuarioReceptorF = it.getString("usuarioReceptor")
            tipoSolicitudF = it.getString("tipoSolicitud")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (tipoSolicitudF == "emisor") {
            inflater.inflate(R.layout.fragment_solicitud_emisor, container, false)
        } else {
            inflater.inflate(R.layout.fragment_solicitud_receptor, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (tipoSolicitudF == "emisor") {
            view.findViewById<TextView>(R.id.fg_e_usuario).text = usuarioReceptorF
            view.findViewById<Button>(R.id.fg_e_cancelar).setOnClickListener {
                botonCancelarE?.invoke(usuarioEmisorF)
            }
        } else {
            view.findViewById<TextView>(R.id.fg_r_usuario).text = usuarioEmisorF
            view.findViewById<Button>(R.id.fg_r_cancelar).setOnClickListener {
                botonCancelarE?.invoke(usuarioEmisorF)
            }
            view.findViewById<Button>(R.id.fg_r_aceptar).setOnClickListener {
                botonAceptar?.invoke(usuarioEmisorF)
            }
        }
    }

}