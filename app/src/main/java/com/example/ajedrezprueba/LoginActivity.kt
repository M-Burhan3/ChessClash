package com.example.ajedrezprueba

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ajedrezprueba.databinding.ActivityLoginBinding
import com.example.ajedrezprueba.models.UsuarioModel
import com.example.ajedrezprueba.preferences.Preferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val datos = GoogleSignIn.getSignedInAccountFromIntent(it.data)

            try {
                val cuenta = datos.getResult(ApiException::class.java)

                if (cuenta != null) {
                    val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credenciales)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                irActivityMain()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
            } catch(e: ApiException) {
                Log.e("GoogleSignInError", "Error al iniciar sesión: ${e.statusCode} - ${e.message}")
            }
        }

        if (it.resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "El usuario canceló.", Toast.LENGTH_SHORT).show()
        }
    }


    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        databaseRef = FirebaseDatabase.getInstance().getReference("usuarios")
        setListeners()

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    //----------------------------------------------------------------------------------------------
    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun login() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConf)

        googleClient.signOut()

        responseLauncher.launch(googleClient.signInIntent)
    }

    //----------------------------------------------------------------------------------------------
    private fun irActivityMain() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        guardarUsuario(email.toString())
        startActivity(Intent(this, MenuActivity::class.java))
    }

    //----------------------------------------------------------------------------------------------
    override fun onStart() {
        super.onStart()
        val usuario = auth.currentUser
        if(usuario != null) {
            irActivityMain()
        }
    }


    private fun guardarUsuario(email: String) {
        val key = email.replace(".", "_")

        databaseRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    databaseRef.child(key).setValue(UsuarioModel(email))
                        .addOnSuccessListener {
                            Log.d("FIREBASE", "Usuario guardado correctamente")
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@LoginActivity, "No se pudo guardar el usuario", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.d("FIREBASE", "El usuario ya existe, no se guarda duplicado")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Error al verificar el usuario", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //----------------------------------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.hide()
        return false
    }

}