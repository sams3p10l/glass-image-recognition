package com.sams3p10l.diplomski.ui

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sams3p10l.diplomski.R
import com.sams3p10l.diplomski.databinding.ActivityMainBinding
import com.sams3p10l.diplomski.gesture.GlassGestureDetector
import com.sams3p10l.diplomski.ui.fragment.*
import com.sams3p10l.diplomski.util.Constants
import com.sams3p10l.diplomski.util.currentNavigationFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), GlassGestureDetector.OnGestureListener {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var decorView: View
    private lateinit var glassGestureDetector: GlassGestureDetector

    private lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph)

        supportActionBar?.hide()

        decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

        glassGestureDetector = GlassGestureDetector(this, this)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (glassGestureDetector.onTouchEvent(ev)) {
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture?): Boolean {
        return when (gesture) {
            GlassGestureDetector.Gesture.TAP -> {
                val currentFragment = supportFragmentManager.currentNavigationFragment as BaseFragment
                currentFragment.onSingleTap()
                true
            }
            GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    private fun hideSystemUI() {
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

}