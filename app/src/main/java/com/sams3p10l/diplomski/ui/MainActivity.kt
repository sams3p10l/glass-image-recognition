package com.sams3p10l.diplomski.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sams3p10l.diplomski.R

class MainActivity : BaseActivity() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: IT'S ALIVE")
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStateAdapter(fm, lifecycle) {

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

        override fun createFragment(position: Int): Fragment {
            TODO("Not yet implemented")
        }

    }
}