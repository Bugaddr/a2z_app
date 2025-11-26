package com.example.dsa

import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.dsa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    // Fragments
    private lateinit var codeFragment: Fragment
    private lateinit var blogFragment: Fragment
    private lateinit var youtubeFragment: Fragment
    
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            windowInsets
        }

        // --- High Refresh Rate --- //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.let {
                it.preferredRefreshRate = 120.0f
                window.attributes = it
            }
        }

        // Setup Fragments
        if (savedInstanceState == null) {
            codeFragment = WebViewFragment.newInstance("https://jrchintu.github.io/a2z_old_sheet")
            blogFragment = WebViewFragment.newInstance("https://jrchintu.github.io/a2z_old_sheet")
            youtubeFragment = WebViewFragment.newInstance("https://jrchintu.github.io/a2z_old_sheet")
            activeFragment = codeFragment
            
            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, codeFragment, "code")
                add(R.id.fragment_container, blogFragment, "blog").hide(blogFragment)
                add(R.id.fragment_container, youtubeFragment, "youtube").hide(youtubeFragment)
            }.commit()
        } else {
            codeFragment = supportFragmentManager.findFragmentByTag("code")!!
            blogFragment = supportFragmentManager.findFragmentByTag("blog")!!
            youtubeFragment = supportFragmentManager.findFragmentByTag("youtube")!!
            
            val activeTag = savedInstanceState.getString("active_fragment_tag", "code")
            activeFragment = when (activeTag) {
                "blog" -> blogFragment
                "youtube" -> youtubeFragment
                else -> codeFragment
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_code -> {
                    switchFragment(codeFragment)
                    true
                }
                R.id.nav_blog -> {
                    switchFragment(blogFragment)
                    true
                }
                R.id.nav_youtube -> {
                    switchFragment(youtubeFragment)
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (activeFragment is WebViewFragment && (activeFragment as WebViewFragment).canGoBack()) {
                    (activeFragment as WebViewFragment).goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val tag = when (activeFragment) {
            blogFragment -> "blog"
            youtubeFragment -> "youtube"
            else -> "code"
        }
        outState.putString("active_fragment_tag", tag)
    }
    
    private fun switchFragment(fragment: Fragment) {
        if (fragment != activeFragment) {
            supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
            activeFragment = fragment
        }
    }
}
