package com.example.ajedrezprueba

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ajedrezprueba.databinding.ActivityDatosUsuarioBinding
import com.example.ajedrezprueba.models.UsuarioModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class DatosUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding
    private lateinit var launcherGaleria: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRefUsuarios: DatabaseReference
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        databaseRefUsuarios = FirebaseDatabase.getInstance().getReference("usuarios")

        ejecutarGaleria()

        setListeners()
        ponerImagenDePerfilYNombre()

    }

    private fun ejecutarGaleria() {
        launcherGaleria = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uriImagen = result.data!!.data
                binding.ivFotoPerfil.setImageURI(uriImagen)

            }
        }
    }

    private fun ponerImagenDePerfilYNombre() {
        databaseRefUsuarios.child(user?.email.toString().replace(".", "_")).child("fotoBase64").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String::class.java).let {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.ivFotoPerfil.setImageBitmap(bmp)
                }
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuarios.child(user?.email.toString().replace(".", "_")).child("nombre").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.edNombreUsuario.setText(snapshot.getValue(String::class.java))
            }
            override fun onCancelled(error: DatabaseError) {  }
        })
    }

    private fun setListeners() {
        binding.btnElegirFoto.setOnClickListener {
            abrirGaleria()
        }
        binding.btnGuardarDatos.setOnClickListener {
            comprobarDatos()
        }
        binding.btnCancelarDatos.setOnClickListener {
            finish()
        }
    }

    private fun comprobarDatos() {
        if (binding.edNombreUsuario.text.length < 3 || binding.edNombreUsuario.text.length > 15) {
            Toast.makeText(this, "El nombre de usuario debe tener entre 3-15 caracteres", Toast.LENGTH_SHORT).show()
        } else {
            guardarDatosFirebase()
            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun guardarDatosFirebase() {
        val email = user?.email.toString()
        val key = email.replace(".", "_")

        val bitmap = (binding.ivFotoPerfil.drawable as BitmapDrawable).bitmap
        val fotoBase64 = bitmapToBase64(bitmap)

        databaseRefUsuarios.child(key).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseRefUsuarios.child(key).setValue(UsuarioModel(email, binding.edNombreUsuario.text.toString(), fotoBase64))
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }



    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcherGaleria.launch(intent)
    }

    private fun bitmapToBase64(bmp: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return  Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

}