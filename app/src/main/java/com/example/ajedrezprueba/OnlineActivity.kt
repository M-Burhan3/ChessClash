package com.example.ajedrezprueba

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ajedrezprueba.databinding.ActivityOnlineBinding
import com.example.ajedrezprueba.models.MensajeModel
import com.example.ajedrezprueba.models.MovimientoModel
import com.example.ajedrezprueba.models.PartidaCanceladaModel
import com.example.ajedrezprueba.models.UsuarioModel
import com.example.ajedrezprueba.preferences.Preferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OnlineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnlineBinding

    //---------------------------------------------------
    private var usuarioEmisor = ""
    private var usuarioReceptor = ""
    private lateinit var auth: FirebaseAuth
    val user = FirebaseAuth.getInstance().currentUser
    private lateinit var databaseRefMovimiento: DatabaseReference
    private lateinit var databaseRefUsuario: DatabaseReference
    private lateinit var databaseRefMensajeEmoji: DatabaseReference
    private lateinit var databaseRefPartidaCancelada: DatabaseReference
    var jugadores = ""
    var permitirMoverFichasOponente = false
    //---------------------------------------------------

    private lateinit var tablero: Array<Array<ImageView>>
    //private var tamañoCasilla = 128
    private var tamañoCasilla = 112
    //private var tamañoCasilla = 80

    private var casillaSeleccionadaTieneFicha = false
    private var ultimaFichaSeleccionada = ""
    private var ultimaRowSeleccionada = 0
    private var ultimaColSeleccionada = 0

    //---------------------------------------------------
    private var peonBlancoPrimerMovimiento1 = true
    private var peonBlancoPrimerMovimiento2 = true
    private var peonBlancoPrimerMovimiento3 = true
    private var peonBlancoPrimerMovimiento4 = true
    private var peonBlancoPrimerMovimiento5 = true
    private var peonBlancoPrimerMovimiento6 = true
    private var peonBlancoPrimerMovimiento7 = true
    private var peonBlancoPrimerMovimiento8 = true

    private var peonNegroPrimerMovimiento1 = true
    private var peonNegroPrimerMovimiento2 = true
    private var peonNegroPrimerMovimiento3 = true
    private var peonNegroPrimerMovimiento4 = true
    private var peonNegroPrimerMovimiento5 = true
    private var peonNegroPrimerMovimiento6 = true
    private var peonNegroPrimerMovimiento7 = true
    private var peonNegroPrimerMovimiento8 = true
    //---------------------------------------------------

    private var casillaSeleccionadaParaComer = false
    private var turno = "blancas"
    //---------------------------------------------------

    private lateinit var contador1: CountDownTimer
    private lateinit var contador2: CountDownTimer

    private var tiempoRestante1 = 900_000L
    private var tiempoRestante2 = 900_000L

    private var contador1Activo = false
    private var contador2Activo = false
    //---------------------------------------------------
    private lateinit var preferences: Preferences
    //---------------------------------------------------
    private var contadorBotonEmojis = 0
    private var contadorBotonFEN = 0
    //---------------------------------------------------
    private var fen = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnlineBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        databaseRefMovimiento = FirebaseDatabase.getInstance().getReference("movimiento")
        databaseRefUsuario = FirebaseDatabase.getInstance().getReference("usuarios")
        databaseRefMensajeEmoji = FirebaseDatabase.getInstance().getReference("mensajeEmoji")
        databaseRefPartidaCancelada = FirebaseDatabase.getInstance().getReference("partidaCancelada")

        recogerDatos()
        jugadores = "$usuarioEmisor - $usuarioReceptor"

        preferences = Preferences(this)

        crearTablero()
        crearFichas()
        setListeners()
        ejecutarMovimientoDelOponente()
        mostrarMensajeEmojiOponente()
        cambiarPrimerTurno(usuarioReceptor, 1)
        if (usuarioEmisor == user?.email) { obtenerImagenYNombre(usuarioReceptor, usuarioEmisor) } else { obtenerImagenYNombre(usuarioEmisor, usuarioReceptor) }
        rotarTablero()
        mostrarPartidaCanceladaAOponente()
        generarFEN()

    }

    //----------------------------------------------------------------------------------------------
    private fun recogerDatos() {
        val datos = intent.extras
        usuarioEmisor = datos?.getString("USUARIO_EMISOR").toString()
        usuarioReceptor = datos?.getString("USUARIO_RECEPTOR").toString()
    }

    //----------------------------------------------------------------------------------------------
    private fun crearTablero() {
        val gridLayout = findViewById<GridLayout>(R.id.tableroAjedrez_online)

        tablero = Array(8) { row ->
            Array(8) { col ->
                val casilla = ImageView(this).apply {
                    setTag("CasillaVacia")
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = tamañoCasilla
                        height = tamañoCasilla
                        rowSpec = GridLayout.spec(row)
                        columnSpec = GridLayout.spec(col)
                    }
                    setBackgroundColor(if ((row + col) % 2 == 0) preferences.getTableroColor1() else preferences.getTableroColor2())
                    setOnClickListener { casillaPulsada(row, col) }
                }
                gridLayout.addView(casilla)
                casilla
            }
        }
    }


    private fun crearFichas() {
        crearFicha(preferences.getImagenPeonBlanco(), 6, 0, "blanca_peon1")
        crearFicha(preferences.getImagenPeonBlanco(), 6, 1, "blanca_peon2")
        crearFicha(preferences.getImagenPeonBlanco(), 6, 2, "blanca_peon3")
        crearFicha(preferences.getImagenPeonBlanco(), 6, 3, "blanca_peon4")
        crearFicha(preferences.getImagenPeonBlanco(), 6, 4, "blanca_peon5")
        crearFicha(preferences.getImagenPeonBlanco(), 6, 5, "blanca_peon6")
        crearFicha(preferences.getImagenPeonBlanco(), 6, 6, "blanca_peon7")
        crearFicha(preferences.getImagenPeonBlanco(), 6, 7, "blanca_peon8")
        crearFicha(preferences.getImagenReinaBlanco(), 7, 3, "blanca_reina")
        crearFicha(preferences.getImagenTorreBlanco(), 7, 7, "blanca_torre")
        crearFicha(preferences.getImagenAlfilBlanco(), 7, 5, "blanca_alfil")
        crearFicha(preferences.getImagenCaballoBlanco(), 7, 6, "blanca_caballo")
        crearFicha(preferences.getImagenReyBlanco(), 7, 4, "blanca_rey")
        crearFicha(preferences.getImagenTorreBlanco(), 7, 0, "blanca_torre")
        crearFicha(preferences.getImagenCaballoBlanco(), 7, 1, "blanca_caballo")
        crearFicha(preferences.getImagenAlfilBlanco(), 7, 2, "blanca_alfil")

        crearFicha(preferences.getImagenPeonNegro(), 1, 0, "negra_peon1")
        crearFicha(preferences.getImagenPeonNegro(), 1, 1, "negra_peon2")
        crearFicha(preferences.getImagenPeonNegro(), 1, 2, "negra_peon3")
        crearFicha(preferences.getImagenPeonNegro(), 1, 3, "negra_peon4")
        crearFicha(preferences.getImagenPeonNegro(), 1, 4, "negra_peon5")
        crearFicha(preferences.getImagenPeonNegro(), 1, 5, "negra_peon6")
        crearFicha(preferences.getImagenPeonNegro(), 1, 6, "negra_peon7")
        crearFicha(preferences.getImagenPeonNegro(), 1, 7, "negra_peon8")
        crearFicha(preferences.getImagenTorreNegro(), 0, 0, "negra_torre")
        crearFicha(preferences.getImagenCaballoNegro(), 0, 1, "negra_caballo")
        crearFicha(preferences.getImagenAlfilNegro(), 0, 2, "negra_alfil")
        crearFicha(preferences.getImagenReinaNegro(), 0, 3, "negra_reina")
        crearFicha(preferences.getImagenReyNegro(), 0, 4, "negra_rey")
        crearFicha(preferences.getImagenAlfilNegro(), 0, 5, "negra_alfil")
        crearFicha(preferences.getImagenCaballoNegro(), 0, 6, "negra_caballo")
        crearFicha(preferences.getImagenTorreNegro(), 0, 7, "negra_torre")


    }


    private fun crearFicha(rutaImagen: Int, fila: Int, columna: Int, tag: String) {
        val ficha = ImageView(this).apply {
            setBackgroundColor(if ((fila + columna) % 2 == 0) preferences.getTableroColor1() else preferences.getTableroColor2())
            setImageResource(rutaImagen)
            setTag(tag)
            layoutParams = GridLayout.LayoutParams().apply {
                width = tamañoCasilla
                height = tamañoCasilla
                rowSpec = GridLayout.spec(fila)
                columnSpec = GridLayout.spec(columna)
            }
        }
        findViewById<GridLayout>(R.id.tableroAjedrez_online).addView(ficha)

        tablero[fila][columna].setTag(tag)
    }


    private fun eleminarFicha(row: Int, col: Int) {
        val ficha = ImageView(this).apply {
            setTag("CasillaVacia")
            layoutParams = GridLayout.LayoutParams().apply {
                width = tamañoCasilla
                height = tamañoCasilla
                rowSpec = GridLayout.spec(row)
                columnSpec = GridLayout.spec(col)
            }
            setBackgroundColor(if ((row + col) % 2 == 0) preferences.getTableroColor1() else preferences.getTableroColor2())
        }
        findViewById<GridLayout>(R.id.tableroAjedrez_online).addView(ficha)

        tablero[row][col].setTag("CasillaVacia")
    }


    private fun casillaPulsada(row: Int, col: Int) {

        val casillaSeleccionada = tablero[row][col]

        if (casillaSeleccionada.tag == "CasillaVacia" || (casillaSeleccionada.tag.toString().substring(0, 5) == "negra" && casillaSeleccionadaParaComer && turno == "blancas") || (casillaSeleccionada.tag.toString().substring(0, 6) == "blanca" && casillaSeleccionadaParaComer && turno == "negras")) {
            if (casillaSeleccionadaTieneFicha == true || casillaSeleccionadaParaComer) {
                moverFicha(ultimaFichaSeleccionada, ultimaRowSeleccionada, ultimaColSeleccionada, casillaSeleccionada.tag.toString(), row, col)
            }
            casillaSeleccionadaParaComer = false
            casillaSeleccionadaTieneFicha = false
        } else {
            casillaSeleccionadaParaComer = true
            casillaSeleccionadaTieneFicha = true
            registrarUltimaFichaSeleccionada(casillaSeleccionada.tag.toString(), row, col)
        }

    }



    private fun moverFicha(tagAntiguo: String, rowAntiguo: Int, colAntiguo: Int, tagNuevo: String, rowNuevo: Int, colNuevo: Int) {
        val rutaImagen = when (tagAntiguo) {
            "blanca_reina" -> preferences.getImagenReinaBlanco()
            "blanca_torre" -> preferences.getImagenTorreBlanco()
            "blanca_alfil" -> preferences.getImagenAlfilBlanco()
            "blanca_caballo" -> preferences.getImagenCaballoBlanco()
            "blanca_rey" -> preferences.getImagenReyBlanco()
            "blanca_peon1" -> preferences.getImagenPeonBlanco()
            "blanca_peon2" -> preferences.getImagenPeonBlanco()
            "blanca_peon3" -> preferences.getImagenPeonBlanco()
            "blanca_peon4" -> preferences.getImagenPeonBlanco()
            "blanca_peon5" -> preferences.getImagenPeonBlanco()
            "blanca_peon6" -> preferences.getImagenPeonBlanco()
            "blanca_peon7" -> preferences.getImagenPeonBlanco()
            "blanca_peon8" -> preferences.getImagenPeonBlanco()

            "negra_reina" -> preferences.getImagenReinaNegro()
            "negra_torre" -> preferences.getImagenTorreNegro()
            "negra_alfil" -> preferences.getImagenAlfilNegro()
            "negra_caballo" -> preferences.getImagenCaballoNegro()
            "negra_rey" -> preferences.getImagenReyNegro()
            "negra_peon1" -> preferences.getImagenPeonNegro()
            "negra_peon2" -> preferences.getImagenPeonNegro()
            "negra_peon3" -> preferences.getImagenPeonNegro()
            "negra_peon4" -> preferences.getImagenPeonNegro()
            "negra_peon5" -> preferences.getImagenPeonNegro()
            "negra_peon6" -> preferences.getImagenPeonNegro()
            "negra_peon7" -> preferences.getImagenPeonNegro()
            "negra_peon8" -> preferences.getImagenPeonNegro()
            else -> R.drawable.error
        }
        if (comprobarMovimientoBlancas(tagAntiguo, rowAntiguo, colAntiguo, tagNuevo, rowNuevo, colNuevo) || comprobarMovimientoNegras(tagAntiguo, rowAntiguo, colAntiguo, tagNuevo, rowNuevo, colNuevo) || comprobarMovimientoPeonesBlancos(tagAntiguo, rowAntiguo, colAntiguo, tagNuevo, rowNuevo, colNuevo) || comprobarMovimientoPeonesNegros(tagAntiguo, rowAntiguo, colAntiguo, tagNuevo, rowNuevo, colNuevo)) {
            eleminarFicha(ultimaRowSeleccionada, ultimaColSeleccionada)
            crearFicha(rutaImagen, rowNuevo, colNuevo, tagAntiguo)
            cambiarTurno(turno)
            if (tagNuevo == "blanca_rey" || tagNuevo == "negra_rey") {
                ganarPartida(tagNuevo, false)
            }
            guardarMoivimiento(tagAntiguo, rowAntiguo, colAntiguo, tagNuevo, rowNuevo, colNuevo)
            rotarFicha()
        }


    }


    private fun registrarUltimaFichaSeleccionada(ficha: String, row: Int, col: Int) {
        ultimaFichaSeleccionada = ficha
        ultimaRowSeleccionada = row
        ultimaColSeleccionada = col
    }


    private fun setListeners() {
        binding.btnVolverOnline.setOnClickListener { cancelarPartida() }
        binding.btnEmojis.setOnClickListener { mostrarListaEmoticonos() }
        binding.emoji1.setOnClickListener { mostrarEmoticonoJugador(R.drawable.emoji_feliz_, binding.bocadilloJugador2, binding.emojiJugador2); guardarMensajeEmoji(R.drawable.emoji_feliz_) }
        binding.emoji2.setOnClickListener { mostrarEmoticonoJugador(R.drawable.emoji_preocupado_, binding.bocadilloJugador2, binding.emojiJugador2); guardarMensajeEmoji(R.drawable.emoji_preocupado_) }
        binding.emoji3.setOnClickListener { mostrarEmoticonoJugador(R.drawable.emoji_enfadado_, binding.bocadilloJugador2, binding.emojiJugador2); guardarMensajeEmoji(R.drawable.emoji_enfadado_) }
        binding.emoji4.setOnClickListener { mostrarEmoticonoJugador(R.drawable.emoji_4_, binding.bocadilloJugador2, binding.emojiJugador2); guardarMensajeEmoji(R.drawable.emoji_4_) }
        binding.btnFenOnline.setOnClickListener { mostrarFEN() }
        binding.ivGuardarFENOnline.setOnClickListener { copiarFEN() }
    }


    ////////// CONTADORES //////////

    private fun iniciarContador1(tiempo: Long = tiempoRestante1) {
        contador1 = object : CountDownTimer(tiempo, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante1 = millisUntilFinished
                binding.tvContadorBlancasOnline.text = formatearTiempo(millisUntilFinished)

                marcarColorTurno(user?.email.toString(), 1)
            }

            override fun onFinish() {
                contador1Activo = false
                binding.tvContadorBlancasOnline.text = "¡Fin!"
                if (user?.email == usuarioEmisor) {
                    ganarPartida("blanca_rey", true)
                } else {
                    ganarPartida("negra_rey", true)
                }
            }
        }
        contador1.start()
        contador1Activo = true
    }

    private fun iniciarContador2(tiempo: Long = tiempoRestante2) {
        contador2 = object : CountDownTimer(tiempo, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante2 = millisUntilFinished
                binding.tvContadorNegrasOnline.text = formatearTiempo(millisUntilFinished)

                marcarColorTurno(user?.email.toString(), 2)
            }

            override fun onFinish() {
                contador2Activo = false
                binding.tvContadorNegrasOnline.text = "¡Fin!"
                if (user?.email == usuarioReceptor) {
                    ganarPartida("negra_rey", true)
                } else {
                    ganarPartida("blanca_rey", true)
                }
            }
        }
        contador2.start()
        contador2Activo = true
    }

    private fun marcarColorTurno(usuario: String, numContador: Int) {
        if (usuario == usuarioEmisor) {
            if (numContador == 1) {
                binding.tvContadorBlancasOnline.setTextColor(Color.YELLOW)
                binding.tvContadorBlancasOnline.setShadowLayer(4f, 2f, 2f, Color.YELLOW)
                binding.tvNombreJugador2Online.setTextColor(Color.YELLOW)
                binding.tvNombreJugador2Online.setShadowLayer(4f, 2f, 2f, Color.YELLOW)

                binding.tvContadorNegrasOnline.setTextColor(Color.WHITE)
                binding.tvContadorNegrasOnline.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
                binding.tvNombreJugador1Online.setTextColor(Color.WHITE)
                binding.tvNombreJugador1Online.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
            } else {
                binding.tvContadorNegrasOnline.setTextColor(Color.YELLOW)
                binding.tvContadorNegrasOnline.setShadowLayer(4f, 2f, 2f, Color.YELLOW)
                binding.tvNombreJugador1Online.setTextColor(Color.YELLOW)
                binding.tvNombreJugador1Online.setShadowLayer(4f, 2f, 2f, Color.YELLOW)

                binding.tvContadorBlancasOnline.setTextColor(Color.WHITE)
                binding.tvContadorBlancasOnline.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
                binding.tvNombreJugador2Online.setTextColor(Color.WHITE)
                binding.tvNombreJugador2Online.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
            }
        } else {
            if (numContador == 2) {
                binding.tvContadorBlancasOnline.setTextColor(Color.YELLOW)
                binding.tvContadorBlancasOnline.setShadowLayer(4f, 2f, 2f, Color.YELLOW)
                binding.tvNombreJugador2Online.setTextColor(Color.YELLOW)
                binding.tvNombreJugador2Online.setShadowLayer(4f, 2f, 2f, Color.YELLOW)

                binding.tvContadorNegrasOnline.setTextColor(Color.WHITE)
                binding.tvContadorNegrasOnline.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
                binding.tvNombreJugador1Online.setTextColor(Color.WHITE)
                binding.tvNombreJugador1Online.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
            } else {
                binding.tvContadorNegrasOnline.setTextColor(Color.YELLOW)
                binding.tvContadorNegrasOnline.setShadowLayer(4f, 2f, 2f, Color.YELLOW)
                binding.tvNombreJugador1Online.setTextColor(Color.YELLOW)
                binding.tvNombreJugador1Online.setShadowLayer(4f, 2f, 2f, Color.YELLOW)

                binding.tvContadorBlancasOnline.setTextColor(Color.WHITE)
                binding.tvContadorBlancasOnline.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
                binding.tvNombreJugador2Online.setTextColor(Color.WHITE)
                binding.tvNombreJugador2Online.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
            }
        }
    }

    private fun cambiarPrimerTurno(usuario: String, numContador: Int) {
        if (user?.email == usuarioReceptor) {
            marcarColorTurno(usuario, numContador)
        }
    }

    private fun pausarContador1() {
        if (contador1Activo) {
            contador1.cancel()
            contador1Activo = false
        }
    }

    private fun pausarContador2() {
        if (contador2Activo) {
            contador2.cancel()
            contador2Activo = false
        }
    }

    private fun alternarContadores() {
        if (contador1Activo) {
            pausarContador1()
            iniciarContador2()
        } else if (contador2Activo) {
            pausarContador2()
            iniciarContador1()
        } else {
            iniciarContador2()
        }
    }

    private fun formatearTiempo(millis: Long): String {
        val totalSegundos = millis / 1000
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        return String.format("%02d:%02d", minutos, segundos)
    }

    private fun cambiarTurno(turno: String) {
        this@OnlineActivity.turno = if (turno == "blancas") "negras" else "blancas"
        alternarContadores()
    }


    ////////// COMPROBAR MOVIMIENTOS //////////

    private fun comprobarMovimientoBlancas(
        tagAntiguo: String,
        rowAntiguo: Int,
        colAntiguo: Int,
        tagNuevo: String,
        rowNuevo: Int,
        colNuevo: Int
    ): Boolean {
        if (tagAntiguo == "blanca_reina" && turno == "blancas" && (user?.email == usuarioEmisor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (rowAntiguo == rowNuevo || colAntiguo == colNuevo || kotlin.math.abs(deltaRow) == kotlin.math.abs(deltaCol)) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }

                return true
            }
        }

        if (tagAntiguo == "blanca_torre" && turno == "blancas" && (user?.email == usuarioEmisor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (rowAntiguo == rowNuevo || colAntiguo == colNuevo) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                return true
            }
        }

        if (tagAntiguo == "blanca_alfil" && turno == "blancas" && (user?.email == usuarioEmisor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (kotlin.math.abs(deltaRow) == kotlin.math.abs(deltaCol)) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                return true
            }
        }

        if (tagAntiguo == "blanca_caballo" && turno == "blancas" && (user?.email == usuarioEmisor || permitirMoverFichasOponente)) {

            if ( (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo - 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo - 1) || /**/ (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo - 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo - 2) ) {
                return true
            }
        }

        if (tagAntiguo == "blanca_rey" && turno == "blancas" && (user?.email == usuarioEmisor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (rowNuevo == rowAntiguo + 1 || colNuevo == colAntiguo + 1 || rowNuevo == rowAntiguo - 1 || colNuevo == colAntiguo - 1) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                return true
            }
        }


        return false
    }






    private fun comprobarMovimientoNegras(
        tagAntiguo: String,
        rowAntiguo: Int,
        colAntiguo: Int,
        tagNuevo: String,
        rowNuevo: Int,
        colNuevo: Int
    ): Boolean {
        if (tagAntiguo == "negra_reina" && turno == "negras" && (user?.email == usuarioReceptor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (rowAntiguo == rowNuevo || colAntiguo == colNuevo || kotlin.math.abs(deltaRow) == kotlin.math.abs(deltaCol)) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }

                return true
            }
        }

        if (tagAntiguo == "negra_torre" && turno == "negras" && (user?.email == usuarioReceptor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (rowAntiguo == rowNuevo || colAntiguo == colNuevo) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                return true
            }
        }

        if (tagAntiguo == "negra_alfil" && turno == "negras" && (user?.email == usuarioReceptor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (kotlin.math.abs(deltaRow) == kotlin.math.abs(deltaCol)) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                return true
            }
        }

        if (tagAntiguo == "negra_caballo" && turno == "negras" && (user?.email == usuarioReceptor || permitirMoverFichasOponente)) {

            if ( (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo - 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo - 1) || /**/ (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo - 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo - 2) ) {
                return true
            }
        }

        if (tagAntiguo == "negra_rey" && turno == "negras" && (user?.email == usuarioReceptor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            if (rowNuevo == rowAntiguo + 1 || colNuevo == colAntiguo + 1 || rowNuevo == rowAntiguo - 1 || colNuevo == colAntiguo - 1) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                return true
            }
        }


        return false
    }










    private fun comprobarMovimientoPeonesBlancos(
        tagAntiguo: String,
        rowAntiguo: Int,
        colAntiguo: Int,
        tagNuevo: String,
        rowNuevo: Int,
        colNuevo: Int
    ): Boolean {
        if (tagAntiguo.startsWith("blanca_peon") && turno == "blancas" && (user?.email == usuarioEmisor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            val peonBlancoPrimerMovimiento = when (tagAntiguo.substring(11, 12)) {
                "1" -> peonBlancoPrimerMovimiento1
                "2" -> peonBlancoPrimerMovimiento2
                "3" -> peonBlancoPrimerMovimiento3
                "4" -> peonBlancoPrimerMovimiento4
                "5" -> peonBlancoPrimerMovimiento5
                "6" -> peonBlancoPrimerMovimiento6
                "7" -> peonBlancoPrimerMovimiento7
                "8" -> peonBlancoPrimerMovimiento8
                else -> return false
            }

            if ( (rowNuevo == rowAntiguo - 1 && colAntiguo == colNuevo) || (rowNuevo == rowAntiguo - 2 && colAntiguo == colNuevo && peonBlancoPrimerMovimiento) ) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[rowNuevo][colNuevo].tag != "CasillaVacia") {
                        return true
                    }
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                when (tagAntiguo.substring(11, 12)) {
                    "1" -> peonBlancoPrimerMovimiento1 = false
                    "2" -> peonBlancoPrimerMovimiento2 = false
                    "3" -> peonBlancoPrimerMovimiento3 = false
                    "4" -> peonBlancoPrimerMovimiento4 = false
                    "5" -> peonBlancoPrimerMovimiento5 = false
                    "6" -> peonBlancoPrimerMovimiento6 = false
                    "7" -> peonBlancoPrimerMovimiento7 = false
                    "8" -> peonBlancoPrimerMovimiento8 = false
                }

                return true
            }

            if (((rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo + 1) && tablero[rowNuevo][colNuevo].tag != "CasillaVacia" ) || ((rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo - 1) && tablero[rowNuevo][colNuevo].tag != "CasillaVacia"  )) {
                when (tagAntiguo.substring(11, 12)) {
                    "1" -> peonBlancoPrimerMovimiento1 = false
                    "2" -> peonBlancoPrimerMovimiento2 = false
                    "3" -> peonBlancoPrimerMovimiento3 = false
                    "4" -> peonBlancoPrimerMovimiento4 = false
                    "5" -> peonBlancoPrimerMovimiento5 = false
                    "6" -> peonBlancoPrimerMovimiento6 = false
                    "7" -> peonBlancoPrimerMovimiento7 = false
                    "8" -> peonBlancoPrimerMovimiento8 = false
                }
                return true
            }
        }
        return false
    }






    private fun comprobarMovimientoPeonesNegros(
        tagAntiguo: String,
        rowAntiguo: Int,
        colAntiguo: Int,
        tagNuevo: String,
        rowNuevo: Int,
        colNuevo: Int
    ): Boolean {
        if (tagAntiguo.startsWith("negra_peon") && turno == "negras" && (user?.email == usuarioReceptor || permitirMoverFichasOponente)) {

            val deltaRow = rowNuevo - rowAntiguo
            val deltaCol = colNuevo - colAntiguo

            val peonNegroPrimerMovimiento = when (tagAntiguo.substring(10, 11)) {
                "1" -> peonNegroPrimerMovimiento1
                "2" -> peonNegroPrimerMovimiento2
                "3" -> peonNegroPrimerMovimiento3
                "4" -> peonNegroPrimerMovimiento4
                "5" -> peonNegroPrimerMovimiento5
                "6" -> peonNegroPrimerMovimiento6
                "7" -> peonNegroPrimerMovimiento7
                "8" -> peonNegroPrimerMovimiento8
                else -> return false
            }

            if ( (rowNuevo == rowAntiguo + 1 && colAntiguo == colNuevo) || (rowNuevo == rowAntiguo + 2 && colAntiguo == colNuevo && peonNegroPrimerMovimiento) ) {

                val stepRow = if (deltaRow == 0) 0 else deltaRow / kotlin.math.abs(deltaRow)
                val stepCol = if (deltaCol == 0) 0 else deltaCol / kotlin.math.abs(deltaCol)

                var currentRow = rowAntiguo + stepRow
                var currentCol = colAntiguo + stepCol
                while (currentRow != rowNuevo || currentCol != colNuevo) {
                    if (tablero[rowNuevo][colNuevo].tag != "CasillaVacia") {
                        return true
                    }
                    if (tablero[currentRow][currentCol].tag != "CasillaVacia") {
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                when (tagAntiguo.substring(10, 11)) {
                    "1" -> peonNegroPrimerMovimiento1 = false
                    "2" -> peonNegroPrimerMovimiento2 = false
                    "3" -> peonNegroPrimerMovimiento3 = false
                    "4" -> peonNegroPrimerMovimiento4 = false
                    "5" -> peonNegroPrimerMovimiento5 = false
                    "6" -> peonNegroPrimerMovimiento6 = false
                    "7" -> peonNegroPrimerMovimiento7 = false
                    "8" -> peonNegroPrimerMovimiento8 = false
                }

                return true
            }

            if (((rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo + 1) && tablero[rowNuevo][colNuevo].tag != "CasillaVacia" ) || ((rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo - 1) && tablero[rowNuevo][colNuevo].tag != "CasillaVacia"  )) {
                when (tagAntiguo.substring(10, 11)) {
                    "1" -> peonNegroPrimerMovimiento1 = false
                    "2" -> peonNegroPrimerMovimiento2 = false
                    "3" -> peonNegroPrimerMovimiento3 = false
                    "4" -> peonNegroPrimerMovimiento4 = false
                    "5" -> peonNegroPrimerMovimiento5 = false
                    "6" -> peonNegroPrimerMovimiento6 = false
                    "7" -> peonNegroPrimerMovimiento7 = false
                    "8" -> peonNegroPrimerMovimiento8 = false
                }
                return true
            }
        }
        return false
    }





    ////////// METODOS PARA EL JUEGO ONLINE //////////
    //----------------------------------------------------------------------------------------------

    private fun guardarMoivimiento(tagAntiguo: String, rowAntiguo: Int, colAntiguo: Int, tagNuevo: String, rowNuevo: Int, colNuevo: Int) {

        val key = System.currentTimeMillis().toString()
        val autor = user?.email.toString()

        databaseRefMovimiento.child(key).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseRefMovimiento.child(key).setValue(MovimientoModel(tagAntiguo, rowAntiguo, colAntiguo, tagNuevo, rowNuevo, colNuevo, ultimaRowSeleccionada, ultimaColSeleccionada, jugadores, autor))
                    .addOnSuccessListener {
                        //Toast.makeText(this@JugadoresActivity, "Solicitud mandada", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //----------------------------------------------------------------------------------------------
    private fun ejecutarMovimientoDelOponente() {
        databaseRefMovimiento.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val jugadoresRegistrados = snapshot.child("jugadores").getValue(String::class.java)
                val autorRegistrado = snapshot.child("autor").getValue(String::class.java)

                if (jugadoresRegistrados == jugadores && autorRegistrado != user?.email) {
                    ultimaRowSeleccionada = snapshot.child("ultimaRowSeleccionada").getValue(Int::class.java)!!
                    ultimaColSeleccionada = snapshot.child("ultimaColSeleccionada").getValue(Int::class.java)!!

                    permitirMoverFichasOponente = true
                    moverFicha(
                        snapshot.child("tagAntiguo").getValue(String::class.java).toString(),
                        snapshot.child("rowAntiguo").getValue(Int::class.java)!!,
                        snapshot.child("colAntiguo").getValue(Int::class.java)!!,
                        snapshot.child("tagNuevo").getValue(String::class.java).toString(),
                        snapshot.child("rowNuevo").getValue(Int::class.java)!!,
                        snapshot.child("colNuevo").getValue(Int::class.java)!!
                    )
                    permitirMoverFichasOponente = false

                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
    }



    ////////// METODOS PARA COMPROBAR FIN DE PARTIDA //////////

    private fun ganarPartida(tag: String, ganarPorTiempo: Boolean) {

        for (fila in tablero) {
            for (casilla in fila) {
                casilla.isClickable = false
            }
        }

        binding.bloqueoTableroOnline.visibility = View.VISIBLE

        pausarContador1()
        pausarContador2()

        if (tag == "blanca_rey") {
            if (user?.email == usuarioEmisor) {
                guardarCopas(-50)
                Toast.makeText(this, "- 50 copas", Toast.LENGTH_SHORT).show()
            } else {
                guardarCopas(+50)
                Toast.makeText(this, "+ 50 copas", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (user?.email == usuarioEmisor) {
                guardarCopas(+50)
                Toast.makeText(this, "- 50 copas", Toast.LENGTH_SHORT).show()
            } else {
                guardarCopas(-50)
                Toast.makeText(this, "+ 50 copas", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog.Builder(this)
            .setTitle( if (ganarPorTiempo) "¡Se acabó el tiempo!" else "¡Fin de la partida!")
            .setMessage(if (tag == "blanca_rey") (if (user?.email == usuarioEmisor) "Ganador: ${binding.tvNombreJugador1Online.text}" else "Ganador: ${binding.tvNombreJugador2Online.text}") + "\n" + if (user?.email == usuarioEmisor) "Perdiste: - 50 copas" else "Ganaste: + 50 copas" else (if (user?.email == usuarioEmisor) "Ganador: ${binding.tvNombreJugador2Online.text}" else "Ganador: ${binding.tvNombreJugador1Online.text}") + "\n" + if (user?.email == usuarioEmisor) "Ganaste: + 50 copas" else "Perdiste: - 50 copas")
            .setNegativeButton("Salir") { dialog, _ ->
                finish()
            }
            .create()
            .show()

    }


    private fun guardarCopas(numCopas: Int) {
        val key = user?.email.toString()
        var fotoBase64 = ""
        var nombre = ""
        var copas = 0

        databaseRefUsuario.child(key.replace(".", "_")).child("fotoBase64").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fotoBase64 = snapshot.getValue(String::class.java).toString()
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(key.replace(".", "_")).child("nombre").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nombre = snapshot.getValue(String::class.java).toString()
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(key.replace(".", "_")).child("copas").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (copas < 0) copas = 0 else copas = snapshot.getValue(Int::class.java)!!
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(key.replace(".", "_")).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseRefUsuario.child(key.replace(".", "_")).setValue(UsuarioModel(key, nombre, fotoBase64, (copas + numCopas)))
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }




    private fun obtenerImagenYNombre(correo1: String, correo2: String) {
        databaseRefUsuario.child(correo1.replace(".", "_")).child("nombre").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvNombreJugador1Online.text = snapshot.getValue(String::class.java)
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(correo1.replace(".", "_")).child("fotoBase64").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String::class.java).let {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.ivJugador1Online.setImageBitmap(bmp)
                }
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(correo2.replace(".", "_")).child("nombre").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvNombreJugador2Online.text = snapshot.getValue(String::class.java)
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        databaseRefUsuario.child(correo2.replace(".", "_")).child("fotoBase64").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String::class.java).let {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.ivJugador2Online.setImageBitmap(bmp)
                }
            }
            override fun onCancelled(error: DatabaseError) {  }
        })


    }

    private fun rotarTablero() {
        if (usuarioReceptor == user?.email) {
            binding.tableroAjedrezOnline.rotation = 180f

            for (i in 0 until binding.tableroAjedrezOnline.childCount) {
                binding.tableroAjedrezOnline.getChildAt(i).rotation = 180f
            }
        }
    }

    private fun rotarFicha() {
        if (usuarioReceptor == user?.email) {
            for (i in 0 until binding.tableroAjedrezOnline.childCount) {
                binding.tableroAjedrezOnline.getChildAt(i).rotation = 180f
            }
        }
    }

    ///// EMOTICONOS /////
    private fun mostrarListaEmoticonos() {
        contadorBotonEmojis++
        if (contadorBotonEmojis % 2 != 0) {
            binding.cvEmoticonos.visibility = View.VISIBLE
        } else {
            binding.cvEmoticonos.visibility = View.GONE
        }
    }

    private fun mostrarEmoticonoJugador(rutaImagen: Int, constraint: ConstraintLayout, imagen: ImageView) {
        contadorBotonEmojis++
        constraint.visibility = View.VISIBLE
        binding.cvEmoticonos.visibility = View.GONE
        imagen.setImageResource(rutaImagen)
        Handler().postDelayed({
            constraint.visibility = View.GONE
        }, 3500)
    }

    private fun guardarMensajeEmoji(rutaImagen: Int) {
        val key = System.currentTimeMillis().toString()

        databaseRefMensajeEmoji.child(key).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseRefMensajeEmoji.child(key).setValue(MensajeModel(usuarioEmisor+usuarioReceptor, user?.email.toString(), rutaImagen))
                    .addOnSuccessListener {

                    }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun mostrarMensajeEmojiOponente() {
        databaseRefMensajeEmoji.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val jugadores = snapshot.child("jugadores").getValue(String::class.java)
                val autor = snapshot.child("autor").getValue(String::class.java)
                val rutaImagen = snapshot.child("rutaImagen").getValue(Int::class.java)!!

                if (jugadores == usuarioEmisor+usuarioReceptor && autor != user?.email) {
                    mostrarEmoticonoJugador(rutaImagen, binding.bocadilloJugador1, binding.emojiJugador1)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
    }



    ////////// GENERAR FEN //////////

    private fun generarFEN() {
        var fenBuilder = StringBuilder()
        var contadorCasillasVacias = 0

        for (fila in 0 until 8) {
            for (col in 0 until 8) {
                val index = fila * 8 + col
                val tag = binding.tableroAjedrezOnline.getChildAt(index).tag.toString()

                when {
                    tag == "CasillaVacia" -> {
                        contadorCasillasVacias++
                    }
                    else -> {
                        if (contadorCasillasVacias > 0) {
                            fenBuilder.append(contadorCasillasVacias)
                            contadorCasillasVacias = 0
                        }

                        fenBuilder.append(
                            when {
                                tag == "negra_torre" -> "r"
                                tag == "negra_caballo" -> "n"
                                tag == "negra_alfil" -> "b"
                                tag == "negra_rey" -> "k"
                                tag == "negra_reina" -> "q"
                                tag.startsWith("negra_peon") -> "p"
                                tag == "blanca_torre" -> "R"
                                tag == "blanca_caballo" -> "N"
                                tag == "blanca_alfil" -> "B"
                                tag == "blanca_rey" -> "K"
                                tag == "blanca_reina" -> "Q"
                                tag.startsWith("blanca_peon") -> "P"
                                else -> ""
                            }
                        )
                    }
                }
            }

            if (contadorCasillasVacias > 0) {
                fenBuilder.append(contadorCasillasVacias)
                contadorCasillasVacias = 0
            }

            if (fila < 7) {
                fenBuilder.append("/")
            }
        }

        fen = fenBuilder.toString()
        if (turno == "blancas") fen += " w" else fen += " b"
        fen += " - - 0 1"
        Log.e("FEN", fen)

    }


    private fun mostrarFEN() {
        contadorBotonFEN++
        if (contadorBotonFEN % 2 != 0) {
            binding.cvFenOnline.visibility = View.VISIBLE
            binding.tvFenOnline.text = fen
        } else {
            binding.cvFenOnline.visibility = View.GONE
        }
    }

    private fun copiarFEN() {

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("fenCopiado", fen)

        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Texto Copiado", Toast.LENGTH_SHORT).show()
    }


    private fun cancelarPartida() {

        AlertDialog.Builder(this)
            .setTitle( "¿Abandonar partida?")
            .setMessage("Si sales ahora perderás 50 copas por abandoar la paartida")
            .setPositiveButton("Aceptar") { dialog, _ ->
                guardarCopas(-50)
                registrarPartidaCancelada()
                finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun registrarPartidaCancelada() {
        val key = System.currentTimeMillis().toString()

        databaseRefPartidaCancelada.child(key).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseRefPartidaCancelada.child(key).setValue(PartidaCanceladaModel(usuarioEmisor+usuarioReceptor, user?.email.toString()))
                    .addOnSuccessListener {

                    }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun mostrarPartidaCanceladaAOponente() {

        databaseRefPartidaCancelada.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val partida = snapshot.child("partida").getValue(String::class.java)
                val emisor = snapshot.child("emisor").getValue(String::class.java)

                if (partida == usuarioEmisor+usuarioReceptor && emisor != user?.email) {
                    mostrarDialogoPartidaCancelada()
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun mostrarDialogoPartidaCancelada() {
        AlertDialog.Builder(this)
            .setTitle( "El oponente abandonó la partida")
            .setPositiveButton("Aceptar") { dialog, _ ->
                finish()
            }
            .create()
            .show()
    }


}