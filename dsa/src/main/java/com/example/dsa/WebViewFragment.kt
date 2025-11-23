package com.example.dsa

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

class WebViewFragment : Fragment() {

    private lateinit var webView: WebView
    private var url: String? = null

    companion object {
        private const val ARG_URL = "url"

        fun newInstance(url: String): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putString(ARG_URL, url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments?.getString(ARG_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Programmatically create WebView or inflate a layout. 
        // Since it's just a WebView, we can return it directly or wrap it.
        // For simplicity and to match previous layout params, let's create it programmatically 
        // or inflate a simple layout. Let's create programmatically to avoid another XML file.
        webView = WebView(requireContext())
        return webView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        url?.let { webView.loadUrl(it) }
    }

    private fun setupWebView() {
        webView.webViewClient = WebViewClient()

        // Enable Cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        // Performance settings
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true

        // Theme settings
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
            } else {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_OFF)
            }
        }
    }

    fun canGoBack(): Boolean {
        return ::webView.isInitialized && webView.canGoBack()
    }

    fun goBack() {
        if (::webView.isInitialized) {
            webView.goBack()
        }
    }
}
