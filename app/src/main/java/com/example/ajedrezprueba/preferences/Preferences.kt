package com.example.ajedrezprueba.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import com.example.ajedrezprueba.R

class Preferences(c: Context) {

    val storage = c.getSharedPreferences("PERSONALIZAR_DATOS", MODE_PRIVATE)

    fun setImagenPeonBlanco(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_PEON_BLANCO", rutaImagen).apply()
    }

    fun setImagenTorreBlanco(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_TORRE_BLANCO", rutaImagen).apply()
    }

    fun setImagenCaballoBlanco(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_CABALLO_BLANCO", rutaImagen).apply()
    }

    fun setImagenAlfilBlanco(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_ALFIL_BLANCO", rutaImagen).apply()
    }

    fun setImagenReinaBlanco(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_REINA_BLANCO", rutaImagen).apply()
    }

    fun setImagenReyBlanco(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_REY_BLANCO", rutaImagen).apply()
    }

    fun setImagenPeonNegro(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_PEON_NEGRO", rutaImagen).apply()
    }

    fun setImagenTorreNegro(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_TORRE_NEGRO", rutaImagen).apply()
    }

    fun setImagenCaballoNegro(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_CABALLO_NEGRO", rutaImagen).apply()
    }

    fun setImagenAlfilNegro(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_ALFIL_NEGRO", rutaImagen).apply()
    }

    fun setImagenReinaNegro(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_REINA_NEGRO", rutaImagen).apply()
    }

    fun setImagenReyNegro(rutaImagen: Int) {
        storage.edit().putInt("RUTA_IMAGEN_REY_NEGRO", rutaImagen).apply()
    }

    fun setTableroColor1(color: Int) {
        storage.edit().putInt("COLOR1", color).apply()
    }

    fun setTableroColor2(color: Int) {
        storage.edit().putInt("COLOR2", color).apply()
    }






    fun getImagenPeonBlanco(): Int {
        return storage.getInt("RUTA_IMAGEN_PEON_BLANCO", R.drawable.blanca_peon)
    }

    fun getImagenTorreBlanco(): Int {
        return storage.getInt("RUTA_IMAGEN_TORRE_BLANCO", R.drawable.blanca_torre)
    }

    fun getImagenCaballoBlanco(): Int {
        return storage.getInt("RUTA_IMAGEN_CABALLO_BLANCO", R.drawable.blanca_caballo)
    }

    fun getImagenAlfilBlanco(): Int {
        return storage.getInt("RUTA_IMAGEN_ALFIL_BLANCO", R.drawable.blanca_alfil)
    }

    fun getImagenReinaBlanco(): Int {
        return storage.getInt("RUTA_IMAGEN_REINA_BLANCO", R.drawable.blanca_reina)
    }

    fun getImagenReyBlanco(): Int {
        return storage.getInt("RUTA_IMAGEN_REY_BLANCO", R.drawable.blanca_rey)
    }

    fun getImagenPeonNegro(): Int {
        return storage.getInt("RUTA_IMAGEN_PEON_NEGRO", R.drawable.negra_peon)
    }

    fun getImagenTorreNegro(): Int {
        return storage.getInt("RUTA_IMAGEN_TORRE_NEGRO", R.drawable.negra_torre)
    }

    fun getImagenCaballoNegro(): Int {
        return storage.getInt("RUTA_IMAGEN_CABALLO_NEGRO", R.drawable.negra_caballo)
    }

    fun getImagenAlfilNegro(): Int {
        return storage.getInt("RUTA_IMAGEN_ALFIL_NEGRO", R.drawable.negra_alfil)
    }

    fun getImagenReinaNegro(): Int {
        return storage.getInt("RUTA_IMAGEN_REINA_NEGRO", R.drawable.negra_reina)
    }

    fun getImagenReyNegro(): Int {
        return storage.getInt("RUTA_IMAGEN_REY_NEGRO", R.drawable.negra_rey)
    }

    fun getTableroColor1(): Int {
        return storage.getInt("COLOR1", Color.WHITE)
    }

    fun getTableroColor2(): Int {
        return storage.getInt("COLOR2", Color.DKGRAY)
    }




    //----- Guardar Diseños Elegidos -----

    fun setDiseñoFichaElegido(diseñoSeleccionado: Int) {
        storage.edit().putInt("DISEÑO_FICHA_ELEGIDA", diseñoSeleccionado).apply()
    }

    fun getDiseñoFichaElegido(): Int {
        return storage.getInt("DISEÑO_FICHA_ELEGIDA", 0)
    }

    fun setDiseñoTableroElegido(diseñoSeleccionado: Int) {
        storage.edit().putInt("DISEÑO_TABLERO_ELEGIDA", diseñoSeleccionado).apply()
    }

    fun getDiseñoTableroElegido(): Int {
        return storage.getInt("DISEÑO_TABLERO_ELEGIDA", 0)
    }


}