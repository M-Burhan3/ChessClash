package com.example.ajedrezprueba

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajedrezprueba.adapters.JugadoresAdapter
import com.example.ajedrezprueba.databinding.ActivityJugadoresBinding
import com.example.ajedrezprueba.fragments.SolicitudFragment
import com.example.ajedrezprueba.fragments.SolicitudFragmentVacio
import com.example.ajedrezprueba.models.SolicitudModel
import com.example.ajedrezprueba.models.UsuarioModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JugadoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJugadoresBinding

    private var listaUsuarios = mutableListOf<UsuarioModel>()
    private lateinit var adapter: JugadoresAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRefUsuarios: DatabaseReference
    private lateinit var databaseRefSolicitud: DatabaseReference
    private lateinit var databaseRefMovimientos: DatabaseReference
    private lateinit var databaseRefMensajeEmoji: DatabaseReference
    private lateinit var databaseRefPartidaCancelada: DatabaseReference

    private lateinit var solicitudRecibidaListener: ChildEventListener
    private lateinit var solicitudAceptadaListener: ChildEventListener

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityJugadoresBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        databaseRefUsuarios = FirebaseDatabase.getInstance().getReference("usuarios")
        databaseRefSolicitud = FirebaseDatabase.getInstance().getReference("solicitud")
        databaseRefMovimientos = FirebaseDatabase.getInstance().getReference("movimiento")
        databaseRefMensajeEmoji = FirebaseDatabase.getInstance().getReference("mensajeEmoji")
        databaseRefPartidaCancelada = FirebaseDatabase.getInstance().getReference("partidaCancelada")

        eliminarMovimientosAnteriores()
        eliminarMensajesAnteriores()
        eliminarPartidasCnaceladasAnteriores()
        setRecycler()
        comprobarSolicitudRecibida()
        comprobarSolicitudAceptadaOCancelada()
        eliminarSolicitud(user?.email.toString())

    }

    //----------------------------------------------------------------------------------------------
    private fun eliminarMovimientosAnteriores() {
        databaseRefMovimientos.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (movimientoSnapshot in snapshot.children) {
                    val autor = movimientoSnapshot.child("autor").getValue(String::class.java)
                    if (autor == user?.email) {
                        movimientoSnapshot.ref.removeValue()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    //----------------------------------------------------------------------------------------------
    private fun eliminarMensajesAnteriores() {
        databaseRefMensajeEmoji.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (mensajeSnapshot in snapshot.children) {
                    val autor = mensajeSnapshot.child("autor").getValue(String::class.java)
                    if (autor == user?.email) {
                        mensajeSnapshot.ref.removeValue()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    //----------------------------------------------------------------------------------------------
    private fun eliminarPartidasCnaceladasAnteriores() {
        databaseRefPartidaCancelada.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (partidaCanceladaSnapshot in snapshot.children) {
                    val autor = partidaCanceladaSnapshot.child("emisor").getValue(String::class.java)
                    if (autor == user?.email) {
                        partidaCanceladaSnapshot.ref.removeValue()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    //----------------------------------------------------------------------------------------------
    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.layoutManager = layoutManager

        cargarJugadores()

        adapter = JugadoresAdapter(listaUsuarios, {usuarioReceptor -> mandarSolicitud(usuarioReceptor)})
        binding.rvUsuarios.adapter = adapter
    }

    //----------------------------------------------------------------------------------------------
    private fun cargarJugadores() {
        databaseRefUsuarios.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaUsuarios.clear()
                for (nodo in snapshot.children) {
                    val usuario = nodo.getValue(UsuarioModel::class.java)
                    if (usuario != null) {
                        listaUsuarios.add(usuario)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@JugadoresActivity, "Error al cargar los jugadores", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //----------------------------------------------------------------------------------------------
    private fun mandarSolicitud(usuarioReceptor: String) {
        val usuarioEmisor = user?.email.toString()
        val key = usuarioEmisor.replace(".", "_")

        databaseRefSolicitud.child(key).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseRefSolicitud.child(key).setValue(SolicitudModel(usuarioEmisor, usuarioReceptor))
            }

            override fun onCancelled(error: DatabaseError) { }
        })

        crearFragment(usuarioEmisor, usuarioReceptor, "emisor")
    }

    //----------------------------------------------------------------------------------------------
    private fun crearFragment(usuarioEmisor: String, usuarioReceptor: String, tipoSolicitud: String) {
        val fg = SolicitudFragment.newInstance(usuarioEmisor, usuarioReceptor, tipoSolicitud, { usuarioEmisor -> cambiarEstadoSolicitud(usuarioEmisor, "cancelada") }, { usuarioEmisor -> cambiarEstadoSolicitud(usuarioEmisor, "aceptada") })

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fcv_solicitud, fg)
        }
    }

    private fun eliminarSolicitud(usuarioEmisor: String) {
        databaseRefSolicitud.child(usuarioEmisor.replace(".", "_")).removeValue()
        ponerFragmentVacio()
    }

    private fun ponerFragmentVacio() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fcv_solicitud, SolicitudFragmentVacio())
        }
    }

    private fun cambiarEstadoSolicitud(usuarioEmisor: String, nuevoEstado: String) {
        val key = usuarioEmisor.replace(".", "_")
        val solicitudRef = FirebaseDatabase.getInstance().getReference("solicitud").child(key)

        solicitudRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val emisor = snapshot.child("usuarioEmisor").getValue(String::class.java)
                val receptor = snapshot.child("usuarioReceptor").getValue(String::class.java)
                if (receptor == user?.email && nuevoEstado == "aceptada") {
                    solicitudRef.child("estado").setValue(nuevoEstado)
                    empezarPartida(emisor.toString(), receptor.toString())
                }
                if (nuevoEstado == "cancelada") {
                    solicitudRef.child("estado").setValue(nuevoEstado)
                    eliminarSolicitud(usuarioEmisor)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    //----------------------------------------------------------------------------------------------
    private fun comprobarSolicitudRecibida() {
        solicitudRecibidaListener = object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val usuarioEmisor = snapshot.child("usuarioEmisor").getValue(String::class.java)
                val usuarioReceptor = snapshot.child("usuarioReceptor").getValue(String::class.java)
                if (usuarioReceptor == user?.email && usuarioEmisor != user?.email) {
                    crearFragment(usuarioEmisor.toString(), usuarioReceptor.toString(), "receptor")
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        }
        databaseRefSolicitud.addChildEventListener(solicitudRecibidaListener)
    }

    //----------------------------------------------------------------------------------------------
    private fun comprobarSolicitudAceptadaOCancelada() {
        solicitudAceptadaListener = object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val usuarioEmisor = snapshot.child("usuarioEmisor").getValue(String::class.java)
                val usuarioReceptor = snapshot.child("usuarioReceptor").getValue(String::class.java)
                val estado = snapshot.child("estado").getValue(String::class.java)
                if (usuarioReceptor != user?.email && usuarioEmisor == user?.email && estado == "aceptada") {
                    empezarPartida(usuarioEmisor.toString(), usuarioReceptor.toString())
                    eliminarSolicitud(usuarioEmisor.toString())
                }
                if ((usuarioReceptor == user?.email || usuarioEmisor == user?.email) && estado == "cancelada") {
                    eliminarSolicitud(usuarioEmisor.toString())
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        }
        databaseRefSolicitud.addChildEventListener(solicitudAceptadaListener)
    }

    //----------------------------------------------------------------------------------------------
    private fun empezarPartida(usuarioEmisor: String, usuarioReceptor: String) {
        databaseRefSolicitud.removeEventListener(solicitudRecibidaListener)
        databaseRefSolicitud.removeEventListener(solicitudAceptadaListener)

        val bundle = Bundle().apply {
            putString("USUARIO_EMISOR", usuarioEmisor)
            putString("USUARIO_RECEPTOR", usuarioReceptor)
        }
        startActivity(Intent(this, OnlineActivity::class.java).apply {
            putExtras(bundle)
        })
        eliminarSolicitud(usuarioEmisor)
    }

    //----------------------------------------------------------------------------------------------
    private var shouldRefreshOnResume = false

    override fun onPause() {
        super.onPause()
        shouldRefreshOnResume = true
    }

    override fun onResume() {
        super.onResume()
        if (shouldRefreshOnResume) {
            shouldRefreshOnResume = false
            recreate()
        }
    }

}