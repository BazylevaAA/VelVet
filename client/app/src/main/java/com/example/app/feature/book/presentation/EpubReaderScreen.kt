package com.example.app.feature.book.presentation

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import java.io.File

private const val TAG = "EpubReaderScreen"

@Composable
fun EpubReaderScreen(fileName: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        AndroidView(
            factory = { ctx ->
                val filesDir = ctx.filesDir
                val assetLoader = WebViewAssetLoader.Builder()
                    .setDomain("appassets.androidplatform.net")
                    .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(ctx))
                    .addPathHandler("/books/", object : WebViewAssetLoader.PathHandler {
                        override fun handle(path: String): WebResourceResponse? {
                            // path is like "/books/book_1.epub", extract the filename
                            val filename = path.removePrefix("/books/")
                            val file = File(filesDir, filename)
                            if (file.exists()) {
                                Log.d(TAG, "Book file found: ${file.absolutePath} (${file.length()} bytes)")
                                return WebResourceResponse(
                                    "application/epub+zip", null,
                                    200, "OK",
                                    mapOf("Access-Control-Allow-Origin" to "*"),
                                    file.inputStream()
                                )
                            } else {
                                Log.w(TAG, "Book file not found: ${file.absolutePath}")
                                Log.w(TAG, "Files in ${filesDir.absolutePath}: ${filesDir.listFiles()?.map { it.name }}")
                                return null
                            }
                        }
                    })
                    .build()

                WebView(ctx).apply {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        WebView.setWebContentsDebuggingEnabled(true)
                    }
                    settings.apply {
                        javaScriptEnabled  = true
                        domStorageEnabled  = true
                        allowFileAccess    = false
                        allowContentAccess = false
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldInterceptRequest(
                            view: WebView,
                            request: WebResourceRequest
                        ): WebResourceResponse? {
                            val url = request.url
                            val scheme = url.scheme
                            if (scheme == "data" || scheme == "blob" || url.host == null) {
                                return null
                            }
                            Log.d(TAG, "Intercepting request: $url")
                            return assetLoader.shouldInterceptRequest(url)
                        }

                        override fun onPageFinished(view: WebView, url: String) {
                            Log.d(TAG, "Page finished loading: $url")
                            if (!url.contains("epub_reader.html")) return
                            val bookUrl = "https://appassets.androidplatform.net/books/$fileName"
                            Log.d(TAG, "Attempting to open book from: $bookUrl")
                            view.evaluateJavascript("openBook('$bookUrl')", null)
                        }
                    }

                    loadUrl("https://appassets.androidplatform.net/assets/epub_reader.html")
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick  = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
        }
    }
}
