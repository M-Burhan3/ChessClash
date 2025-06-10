package com.example.ajedrezprueba

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ajedrezprueba.databinding.ActivityMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRefUsuario: DatabaseReference
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        databaseRefUsuario = FirebaseDatabase.getInstance().getReference("usuarios")

        ponerImagenDePerfilNombreYCopas()

        setListeners()


        cambiarColorBarraNotificaciones()

    }

    private fun setListeners() {
        binding.btnJugar.setOnClickListener {
            startActivity(Intent(this, LocalActivity::class.java))
        }
        binding.btnPersonalizar.setOnClickListener {
            startActivity(Intent(this, PersonalizarActivity::class.java))
        }
        binding.ivFotoPerfilMenu.setOnClickListener {
            startActivity(Intent(this, PersonalizarActivity::class.java))
        }
        binding.btnJugadores.setOnClickListener {
            startActivity(Intent(this, JugadoresActivity::class.java))
        }
        binding.btnVolverLogin.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnUsuario.setOnClickListener {
            startActivity(Intent(this, DatosUsuarioActivity::class.java))
        }
        binding.btnRevisarJugada.setOnClickListener{
            startActivity(Intent(this, RevisarJugadaActivity::class.java))
        }
        binding.btnAyuda.setOnClickListener{
            mostrarAyuda()
        }
    }


    private fun ponerImagenDePerfilNombreYCopas() {
        databaseRefUsuario.child(user?.email.toString().replace(".", "_")).child("nombre").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.btnUsuario.text = snapshot.getValue(String::class.java)
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(user?.email.toString().replace(".", "_")).child("fotoBase64").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String::class.java).let {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.ivFotoPerfilMenu.setImageBitmap(bmp)
                }
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(user?.email.toString().replace(".", "_")).child("copas").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvCopas.text = snapshot.getValue(Int::class.java).toString()
            }
            override fun onCancelled(error: DatabaseError) {  }
        })
    }



    private fun mostrarAyuda() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_ayuda, null)
        bottomSheet.setContentView(view)
        bottomSheet.show()

        val btnManual = view.findViewById<Button>(R.id.btn_manual)
        btnManual.setOnClickListener {
            descargarPdfEnDownloads(this, "Manual de Usuario ChessClash.pdf")
        }

        val btnAprender = view.findViewById<Button>(R.id.btn_aprender)
        btnAprender.setOnClickListener {
            descargarPdfEnDownloads(this, "Aprende a Jugar ChessClash.pdf")
        }
    }



    //----------------------------------------------------------------------------------------------
    private var shouldRefreshOnResume = false

    override fun onPause() {
        super.onPause()
        shouldRefreshOnResume = true
    }

    override fun onResume() {
        super.onResume()
        ponerImagenDePerfilNombreYCopas()
        cambiarColorBarraNotificaciones()
    }

    private fun cambiarColorBarraNotificaciones() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }







    fun descargarPdfEnDownloads(context: Context, nombreArchivo: String) {
        try {
            val inputStream = context.assets.open(nombreArchivo)

            // Carpeta de Descargas
            val descargaDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            if (!descargaDir.exists()) {
                descargaDir.mkdirs()
            }

            val archivoDestino = File(descargaDir, nombreArchivo)

            inputStream.use { input ->
                FileOutputStream(archivoDestino).use { output ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(context, "PDF descargado en: ${archivoDestino.absolutePath}", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {

        }
    }



}