package com.example.ajedrezprueba

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ajedrezprueba.databinding.ActivityRevisarJugadaBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

class RevisarJugadaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRevisarJugadaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRevisarJugadaBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarWebView()
        setListeners()

    }

    //----------------------------------------------------------------------------------------------
    private fun setListeners() {
        binding.swipe.setOnRefreshListener {
            binding.webView.reload()
        }
        binding.btnInfoFen.setOnClickListener {
            mostrarInfo()
        }
        binding.btnVolverWeb.setOnClickListener {
            finish()
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun inicializarWebView() {
        binding.webView.webViewClient = object : WebViewClient() {

            @Override
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.swipe.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.swipe.isRefreshing = false
            }

        }

        binding.webView.webChromeClient = object : WebChromeClient() {}
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.loadUrl("https://www.ajedrezeureka.com/tablero-de-ajedrez-de-analisis-con-editor-fen-y-pgn/")

    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }


    private fun mostrarInfo() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_info, null)
        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

}