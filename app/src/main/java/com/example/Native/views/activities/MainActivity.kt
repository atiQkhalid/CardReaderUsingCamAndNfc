package com.example.Native.views.activities

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.Native.R
import com.example.Native.base.BaseActivity
import com.example.Native.databinding.ActivityMainBinding
import com.example.Native.extenssions.replaceFragmentSafely
import com.example.Native.views.fragments.home.HomeFragment

open class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        replaceFragmentSafely(HomeFragment())
    }

    fun switchFragmentFromAnotherActivity(fragment: Fragment, bundle: Bundle) {
        replaceFragmentSafely(fragment, bundle = bundle)
    }
}