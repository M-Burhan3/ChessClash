package com.example.ajedrezprueba

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ajedrezprueba.databinding.ActivityLocalBinding
import com.example.ajedrezprueba.preferences.Preferences


class LocalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocalBinding

    private lateinit var tablero: Array<Array<ImageView>>
    //private var tamañoCasilla = 128
    private var tamañoCasilla = 112
    //private var tamañoCasilla = 80
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
    private var contadorBotonFEN = 0

    private var fen = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLocalBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferences = Preferences(this)

        crearTablero()
        crearFichas()
        setListeners()
        generarFEN()

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    private fun crearTablero() {
        val gridLayout = findViewById<GridLayout>(R.id.tableroAjedrez)

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
        findViewById<GridLayout>(R.id.tableroAjedrez).addView(ficha)

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
        findViewById<GridLayout>(R.id.tableroAjedrez).addView(ficha)

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
                ganarPartida(tagNuevo)
            }
            generarFEN()
        }


    }


    private fun registrarUltimaFichaSeleccionada(ficha: String, row: Int, col: Int) {
        ultimaFichaSeleccionada = ficha
        ultimaRowSeleccionada = row
        ultimaColSeleccionada = col
    }


    private fun setListeners() {
        binding.btnVolver.setOnClickListener {
            finish()
        }
        binding.btnFen.setOnClickListener {
            mostrarFEN()
        }
        binding.ivGuardarFEN.setOnClickListener {
            copiarFEN()
        }
    }

    private fun cambiarTurno(turno: String) {
        this@LocalActivity.turno = if (turno == "blancas") "negras" else "blancas"
        binding.tvTurno.text = "Turno: ${this@LocalActivity.turno}"
        alternarContadores()
        marcarTurno(turno)
    }




    ////////// CONTADORES //////////

    private fun iniciarContador1(tiempo: Long = tiempoRestante1) {
        contador1 = object : CountDownTimer(tiempo, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante1 = millisUntilFinished
                binding.tvContadorBlancas.text = formatearTiempo(millisUntilFinished)
            }

            override fun onFinish() {
                contador1Activo = false
                binding.tvContadorBlancas.text = "¡Fin!"
            }
        }
        contador1.start()
        contador1Activo = true
    }

    private fun iniciarContador2(tiempo: Long = tiempoRestante2) {
        contador2 = object : CountDownTimer(tiempo, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante2 = millisUntilFinished
                binding.tvContadorNegras.text = formatearTiempo(millisUntilFinished)
            }

            override fun onFinish() {
                contador2Activo = false
                binding.tvContadorNegras.text = "¡Fin!"
            }
        }
        contador2.start()
        contador2Activo = true
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


    private fun marcarTurno(turno: String) {
        if (turno == "negras") {
            binding.tvContadorBlancas.setTextColor(Color.YELLOW)
            binding.tvContadorBlancas.setShadowLayer(4f, 2f, 2f, Color.YELLOW)
            binding.ivBlancas.setColorFilter("#A1F0DE3C".toColorInt(), PorterDuff.Mode.SRC_ATOP)

            binding.tvContadorNegras.setTextColor(Color.WHITE)
            binding.tvContadorNegras.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
            binding.ivNegras.setColorFilter("#00FFFFFF".toColorInt(), PorterDuff.Mode.SRC_ATOP)
        } else {
            binding.tvContadorNegras.setTextColor(Color.YELLOW)
            binding.tvContadorNegras.setShadowLayer(4f, 2f, 2f, Color.YELLOW)
            binding.ivNegras.setColorFilter("#A1F0DE3C".toColorInt(), PorterDuff.Mode.SRC_ATOP)

            binding.tvContadorBlancas.setTextColor(Color.WHITE)
            binding.tvContadorBlancas.setShadowLayer(0f, 0f, 0f, Color.YELLOW)
            binding.ivBlancas.setColorFilter("#00FFFFFF".toColorInt(), PorterDuff.Mode.SRC_ATOP)
        }
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
        if (tagAntiguo == "blanca_reina" && turno == "blancas") {

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

        if (tagAntiguo == "blanca_torre" && turno == "blancas") {

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

        if (tagAntiguo == "blanca_alfil" && turno == "blancas") {

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

        if (tagAntiguo == "blanca_caballo" && turno == "blancas") {

            if ( (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo - 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo - 1) || /**/ (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo - 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo - 2) ) {
                return true
            }
        }

        if (tagAntiguo == "blanca_rey" && turno == "blancas") {

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
        if (tagAntiguo == "negra_reina" && turno == "negras") {

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

        if (tagAntiguo == "negra_torre" && turno == "negras") {

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

        if (tagAntiguo == "negra_alfil" && turno == "negras") {

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

        if (tagAntiguo == "negra_caballo" && turno == "negras") {

            if ( (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo + 1) || (rowNuevo == rowAntiguo + 2 && colNuevo == colAntiguo - 1) || (rowNuevo == rowAntiguo - 2 && colNuevo == colAntiguo - 1) || /**/ (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo + 2) || (rowNuevo == rowAntiguo + 1 && colNuevo == colAntiguo - 2) || (rowNuevo == rowAntiguo - 1 && colNuevo == colAntiguo - 2) ) {
                return true
            }
        }

        if (tagAntiguo == "negra_rey" && turno == "negras") {

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
        if (tagAntiguo.startsWith("blanca_peon") && turno == "blancas") {

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
        if (tagAntiguo.startsWith("negra_peon") && turno == "negras") {

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




    ////////// METODOS PARA COMPROBAR FIN DE PARTIDA //////////

    private fun ganarPartida(tag: String) {

        for (fila in tablero) {
            for (casilla in fila) {
                casilla.isClickable = false
            }
        }

        binding.bloqueoTablero.visibility = View.VISIBLE

        pausarContador1()
        pausarContador2()

        AlertDialog.Builder(this)
            .setTitle("¡Fin de la partida!")
            .setMessage(if (tag == "blanca_rey") "Ganan las Negras" else "Ganan las Blancas")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Salir") { dialog, _ ->
                finish()
            }
            .create()
            .show()
    }



    ///// GENERAR FEN /////

    private fun generarFEN() {
        var fenBuilder = StringBuilder()
        var contadorCasillasVacias = 0

        for (fila in 0 until 8) {
            for (col in 0 until 8) {
                val index = fila * 8 + col
                val tag = binding.tableroAjedrez.getChildAt(index).tag.toString()

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
    }

    private fun mostrarFEN() {
        contadorBotonFEN++
        if (contadorBotonFEN % 2 != 0) {
            binding.cvFen.visibility = View.VISIBLE
            binding.tvFen.text = fen
        } else {
            binding.cvFen.visibility = View.GONE
        }
    }

    private fun copiarFEN() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("fenCopiado", fen)

        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Texto Copiado", Toast.LENGTH_SHORT).show()
    }

}