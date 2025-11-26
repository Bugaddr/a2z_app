package com.example.dsa

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
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
        webView = WebView(requireContext()).apply {
            // Enable hardware acceleration for better performance
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        return webView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            url?.let { webView.loadUrl(it) }
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }
    
    override fun onPause() {
        super.onPause()
        webView.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    private fun setupWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                return handleExternalIntents(view, url)
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return handleExternalIntents(view, url)
            }
        }

        // Enable Cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        // Performance settings
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        
        // Caching configuration
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        
        // Rendering optimizations
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        
        // Additional performance optimizations
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        
        // Layout and text optimizations
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        webSettings.textZoom = 100
        webSettings.minimumFontSize = 8
        
        // Enable safe browsing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.safeBrowsingEnabled = true
        }

        // Dark mode settings using the newer algorithmic darkening API
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        
        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(webSettings, isDarkMode)
        } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            // Fallback for older devices that don't support ALGORITHMIC_DARKENING
            // FORCE_DARK is deprecated but still needed for backward compatibility
            @Suppress("DEPRECATION")
            if (isDarkMode) {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
            } else {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_OFF)
            }
        }
    }

    private fun handleExternalIntents(view: WebView, url: String): Boolean {
        // Handle intent:// links and YouTube deep links so they open in the YouTube app
        return try {
            when {
                url.startsWith("intent://") -> {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    val pm = requireContext().packageManager

                    if (intent.`package` != null && isPackageInstalled(pm, intent.`package`!!)) {
                        startActivity(intent)
                        true
                    } else {
                        val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                        if (!fallbackUrl.isNullOrEmpty()) {
                            view.loadUrl(fallbackUrl)
                            true
                        } else if (intent.`package` != null) {
                            // Try to open Play Store for the missing app
                            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=${intent.`package`}")
                            }
                            startActivity(marketIntent)
                            true
                        } else {
                            false
                        }
                    }
                }

                url.startsWith("vnd.youtube:") -> {
                    // vnd.youtube:VIDEO_ID
                    openExternal(Uri.parse(url))
                    true
                }

                url.contains("youtube.com/") || url.contains("youtu.be/") -> {
                    // Prefer opening YouTube links in the YouTube app when available
                    if (openExternal(Uri.parse(url))) true else false
                }

                url.startsWith("market://") -> {
                    openExternal(Uri.parse(url))
                    true
                }

                else -> false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun openExternal(uri: Uri): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= 33) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
            true
        } catch (_: Exception) {
            false
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
