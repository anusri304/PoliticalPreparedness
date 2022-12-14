package com.example.android.politicalpreparedness.election;

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.util.Constants.WEB_VIEW_URL

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val intent = getIntent()
        val textUrl = intent.getStringExtra(WEB_VIEW_URL)

        // Find the WebView by its unique ID
        val webView: WebView = findViewById(R.id.voterInfoWebview)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)

        webView.loadUrl(textUrl!!)
    }
}
