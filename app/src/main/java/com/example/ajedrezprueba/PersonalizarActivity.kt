package com.example.ajedrezprueba

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajedrezprueba.adapters.DiseñoAdapter
import com.example.ajedrezprueba.databinding.ActivityPersonalizarBinding
import com.example.ajedrezprueba.models.DiseñoModel
import com.example.ajedrezprueba.models.UsuarioModel
import com.example.ajedrezprueba.preferences.Preferences
import androidx.core.graphics.toColorInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PersonalizarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalizarBinding

    private lateinit var preferences: Preferences
    private var listaDiseños = mutableListOf<DiseñoModel>()
    private lateinit var adapter: DiseñoAdapter

    val user = FirebaseAuth.getInstance().currentUser
    private lateinit var databaseRefUsuario: DatabaseReference
    var copas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPersonalizarBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferences = Preferences(this)
        databaseRefUsuario = FirebaseDatabase.getInstance().getReference("usuarios")

        obtenerCopas {
            setRecycler()
        }
        setListeners()
    }

    private fun setListeners() {
        binding.btnVolverPersonalizar.setOnClickListener {
            finish()
        }
    }


    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvDiseOs.layoutManager = layoutManager

        cargarDiseños()

        adapter = DiseñoAdapter(listaDiseños, {id -> aplicarDiseño(id)})
        binding.rvDiseOs.adapter = adapter
    }

    private fun cargarDiseños() {
        listaDiseños.add(DiseñoModel(1, "Tablero Clásico", R.drawable.diseno_tablero_2, 0, false))
        listaDiseños.add(DiseñoModel(2, "Fichas clásicas", R.drawable.negra_rey, 0, false))
        listaDiseños.add(DiseñoModel(3, "Tablero Verde", R.drawable.diseno_tablero_1, 0, false))
        listaDiseños.add(DiseñoModel(4, "F. Azules y Blancas", R.drawable.rey_negro2, 0, false))
        listaDiseños.add(DiseñoModel(5, "Tablero Rojo", R.drawable.diseno_tablero_3, 150, copas <= 150))
        listaDiseños.add(DiseñoModel(6, "F. Diseño Sencillo", R.drawable.negra_rey3, 150, copas <= 150))
        listaDiseños.add(DiseñoModel(7, "Tablero Antiguo", R.drawable.diseno_tablero_4, 200, copas <= 200))
        listaDiseños.add(DiseñoModel(8, "F. Pintura Grafiti", R.drawable.negra_rey4, 200, copas <= 200))
        listaDiseños.add(DiseñoModel(9, "Tablero Esmeralda", R.drawable.diseno_tablero_5, 250, copas <= 250))
        listaDiseños.add(DiseñoModel(10, "F. Metalizadas", R.drawable.negra_rey5, 250, copas <= 250))
        listaDiseños.add(DiseñoModel(11, "Tablero Zafiro", R.drawable.diseno_tablero_6, 300, copas <= 300))
        listaDiseños.add(DiseñoModel(12, "F. Diseño Luca", R.drawable.negra_rey6, 300, copas <= 300))
        listaDiseños.add(DiseñoModel(13, "Tablero Oxidado", R.drawable.diseno_tablero_7, 350, copas <= 350))
        listaDiseños.add(DiseñoModel(14, "Fichas Estilo Maya", R.drawable.negra_rey7, 350, copas <= 350))
        listaDiseños.add(DiseñoModel(15, "Tablero Amatista", R.drawable.diseno_tablero_8, 400, copas <= 400))
        listaDiseños.add(DiseñoModel(16, "Fichas Modernas", R.drawable.negra_rey8, 400, copas <= 400))
        listaDiseños.add(DiseñoModel(17, "Tablero Cian", R.drawable.diseno_tablero_9, 450, copas <= 450))
        listaDiseños.add(DiseñoModel(18, "Fichas Neón", R.drawable.negra_rey9, 450, copas <= 450))
        listaDiseños.add(DiseñoModel(19, "Tablero Madera", R.drawable.diseno_tablero_10, 500, copas <= 500))
        listaDiseños.add(DiseñoModel(20, "Fichas Cielo", R.drawable.negra_rey10, 500, copas <= 500))
        listaDiseños.add(DiseñoModel(21, "Tablero Radioactivo", R.drawable.diseno_tablero_11, 550, copas <= 550))
        listaDiseños.add(DiseñoModel(22, "Fichas Vintage", R.drawable.negra_rey11, 550, copas <= 550))
        listaDiseños.add(DiseñoModel(23, "Tablero Piruleta", R.drawable.diseno_tablero_12, 600, copas <= 600))
        listaDiseños.add(DiseñoModel(24, "Fichas Guerreras", R.drawable.negra_rey12, 600, copas <= 600))
        listaDiseños.add(DiseñoModel(25, "Tablero Solar", R.drawable.diseno_tablero_13, 650, copas <= 650))
        Log.e("copas en el metodo", "" + copas)
    }

    private fun aplicarDiseño(id: Int) {
        when (id) {
            1 -> guardarDatosTablero1()
            2 -> guardarDatosFicha1()
            3 -> guardarDatosTablero2()
            4 -> guardarDatosFicha2()
            5 -> guardarDatosTablero3()
            6 -> guardarDatosFicha3()
            7 -> guardarDatosTablero4()
            8 -> guardarDatosFicha4()
            9 -> guardarDatosTablero5()
            10 -> guardarDatosFicha5()
            11 -> guardarDatosTablero6()
            12 -> guardarDatosFicha6()
            13 -> guardarDatosTablero7()
            14 -> guardarDatosFicha7()
            15 -> guardarDatosTablero8()
            16 -> guardarDatosFicha8()
            17 -> guardarDatosTablero9()
            18 -> guardarDatosFicha9()
            19 -> guardarDatosTablero10()
            20 -> guardarDatosFicha10()
            21 -> guardarDatosTablero11()
            22 -> guardarDatosFicha11()
            23 -> guardarDatosTablero12()
            24 -> guardarDatosFicha12()
            25 -> guardarDatosTablero13()
        }
        adapter.notifyDataSetChanged()
    }

    private fun guardarDatosFicha1() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey)

        preferences.setImagenPeonNegro(R.drawable.negra_peon)
        preferences.setImagenTorreNegro(R.drawable.negra_torre)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil)
        preferences.setImagenReinaNegro(R.drawable.negra_reina)
        preferences.setImagenReyNegro(R.drawable.negra_rey)
    }

    private fun guardarDatosFicha2() {
        preferences.setImagenPeonBlanco(R.drawable.peon_blanco2)
        preferences.setImagenTorreBlanco(R.drawable.torre_blanco2)
        preferences.setImagenCaballoBlanco(R.drawable.caballo_blanco2)
        preferences.setImagenAlfilBlanco(R.drawable.alfil_blanco2)
        preferences.setImagenReinaBlanco(R.drawable.reina_blanco2)
        preferences.setImagenReyBlanco(R.drawable.rey_blanco2)

        preferences.setImagenPeonNegro(R.drawable.peon_negro2)
        preferences.setImagenTorreNegro(R.drawable.torre_negro2)
        preferences.setImagenCaballoNegro(R.drawable.caballo_negro2)
        preferences.setImagenAlfilNegro(R.drawable.alfil_negro2)
        preferences.setImagenReinaNegro(R.drawable.reina_negro2)
        preferences.setImagenReyNegro(R.drawable.rey_negro2)
    }

    private fun guardarDatosFicha3() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon3)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre3)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo3)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil3)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina3)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey3)

        preferences.setImagenPeonNegro(R.drawable.negra_peon3)
        preferences.setImagenTorreNegro(R.drawable.negra_torre3)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo3)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil3)
        preferences.setImagenReinaNegro(R.drawable.negra_reina3)
        preferences.setImagenReyNegro(R.drawable.negra_rey3)
    }

    private fun guardarDatosFicha4() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon4)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre4)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo4)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil4)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina4)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey4)

        preferences.setImagenPeonNegro(R.drawable.negra_peon4)
        preferences.setImagenTorreNegro(R.drawable.negra_torre4)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo4)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil4)
        preferences.setImagenReinaNegro(R.drawable.negra_reina4)
        preferences.setImagenReyNegro(R.drawable.negra_rey4)
    }

    private fun guardarDatosFicha5() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon5)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre5)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo5)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil5)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina5)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey5)

        preferences.setImagenPeonNegro(R.drawable.negra_peon5)
        preferences.setImagenTorreNegro(R.drawable.negra_torre5)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo5)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil5)
        preferences.setImagenReinaNegro(R.drawable.negra_reina5)
        preferences.setImagenReyNegro(R.drawable.negra_rey5)
    }

    private fun guardarDatosFicha6() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon6)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre6)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo6)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil6)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina6)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey6)

        preferences.setImagenPeonNegro(R.drawable.negra_peon7)
        preferences.setImagenTorreNegro(R.drawable.negra_torre7)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo7)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil7)
        preferences.setImagenReinaNegro(R.drawable.negra_reina7)
        preferences.setImagenReyNegro(R.drawable.negra_rey7)
    }

    private fun guardarDatosFicha7() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon7)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre7)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo7)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil7)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina7)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey7)

        preferences.setImagenPeonNegro(R.drawable.negra_peon7)
        preferences.setImagenTorreNegro(R.drawable.negra_torre7)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo7)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil7)
        preferences.setImagenReinaNegro(R.drawable.negra_reina7)
        preferences.setImagenReyNegro(R.drawable.negra_rey7)
    }

    private fun guardarDatosFicha8() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon8)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre8)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo8)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil8)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina8)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey8)

        preferences.setImagenPeonNegro(R.drawable.negra_peon8)
        preferences.setImagenTorreNegro(R.drawable.negra_torre8)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo8)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil8)
        preferences.setImagenReinaNegro(R.drawable.negra_reina8)
        preferences.setImagenReyNegro(R.drawable.negra_rey8)
    }

    private fun guardarDatosFicha9() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon9)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre9)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo9)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil9)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina9)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey9)

        preferences.setImagenPeonNegro(R.drawable.negra_peon9)
        preferences.setImagenTorreNegro(R.drawable.negra_torre9)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo9)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil9)
        preferences.setImagenReinaNegro(R.drawable.negra_reina9)
        preferences.setImagenReyNegro(R.drawable.negra_rey9)
    }

    private fun guardarDatosFicha10() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon10)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre10)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo10)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil10)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina10)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey10)

        preferences.setImagenPeonNegro(R.drawable.negra_peon10)
        preferences.setImagenTorreNegro(R.drawable.negra_torre10)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo10)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil10)
        preferences.setImagenReinaNegro(R.drawable.negra_reina10)
        preferences.setImagenReyNegro(R.drawable.negra_rey10)
    }

    private fun guardarDatosFicha11() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon11)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre11)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo11)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil11)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina11)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey11)

        preferences.setImagenPeonNegro(R.drawable.negra_peon11)
        preferences.setImagenTorreNegro(R.drawable.negra_torre11)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo11)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil11)
        preferences.setImagenReinaNegro(R.drawable.negra_reina11)
        preferences.setImagenReyNegro(R.drawable.negra_rey11)
    }

    private fun guardarDatosFicha12() {
        preferences.setImagenPeonBlanco(R.drawable.blanca_peon12)
        preferences.setImagenTorreBlanco(R.drawable.blanca_torre12)
        preferences.setImagenCaballoBlanco(R.drawable.blanca_caballo12)
        preferences.setImagenAlfilBlanco(R.drawable.blanca_alfil12)
        preferences.setImagenReinaBlanco(R.drawable.blanca_reina12)
        preferences.setImagenReyBlanco(R.drawable.blanca_rey12)

        preferences.setImagenPeonNegro(R.drawable.negra_peon12)
        preferences.setImagenTorreNegro(R.drawable.negra_torre12)
        preferences.setImagenCaballoNegro(R.drawable.negra_caballo12)
        preferences.setImagenAlfilNegro(R.drawable.negra_alfil12)
        preferences.setImagenReinaNegro(R.drawable.negra_reina12)
        preferences.setImagenReyNegro(R.drawable.negra_rey12)
    }

    private fun guardarDatosTablero1() {
        preferences.setTableroColor1(Color.WHITE)
        preferences.setTableroColor2(Color.DKGRAY)
    }

    private fun guardarDatosTablero2() {
        preferences.setTableroColor1(Color.WHITE)
        preferences.setTableroColor2("#74d433".toColorInt())
    }

    private fun guardarDatosTablero3() {
        preferences.setTableroColor1(Color.WHITE)
        preferences.setTableroColor2("#ec2525".toColorInt())
    }

    private fun guardarDatosTablero4() {
        preferences.setTableroColor1("#fbeaa8".toColorInt())
        preferences.setTableroColor2("#c32a2a".toColorInt())
    }

    private fun guardarDatosTablero5() {
        preferences.setTableroColor1("#d5ffb8".toColorInt())
        preferences.setTableroColor2("#50892a".toColorInt())
    }

    private fun guardarDatosTablero6() {
        preferences.setTableroColor1("#d6e2ff".toColorInt())
        preferences.setTableroColor2("#0a3fbf".toColorInt())
    }

    private fun guardarDatosTablero7() {
        preferences.setTableroColor1("#cbe4ff".toColorInt())
        preferences.setTableroColor2("#9f0000".toColorInt())
    }

    private fun guardarDatosTablero8() {
        preferences.setTableroColor1("#f2c7ff".toColorInt())
        preferences.setTableroColor2("#7d00a5".toColorInt())
    }

    private fun guardarDatosTablero9() {
        preferences.setTableroColor1("#a5ff91".toColorInt())
        preferences.setTableroColor2("#2944a9".toColorInt())
    }

    private fun guardarDatosTablero10() {
        preferences.setTableroColor1("#ffeab6".toColorInt())
        preferences.setTableroColor2("#6b2803".toColorInt())
    }

    private fun guardarDatosTablero11() {
        preferences.setTableroColor1("#c7ffb6".toColorInt())
        preferences.setTableroColor2("#4f4f4f".toColorInt())
    }

    private fun guardarDatosTablero12() {
        preferences.setTableroColor1("#ffceed".toColorInt())
        preferences.setTableroColor2("#ca0080".toColorInt())
    }

    private fun guardarDatosTablero13() {
        preferences.setTableroColor1("#fffec9".toColorInt())
        preferences.setTableroColor2("#ffdb00".toColorInt())
    }


    private fun obtenerCopas(onResult: () -> Unit) {
        val key = user?.email.toString()

        databaseRefUsuario.child(key.replace(".", "_")).child("copas").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                copas = snapshot.getValue(Int::class.java)!!
                onResult()
            }
            override fun onCancelled(error: DatabaseError) {  }
        })
    }
}