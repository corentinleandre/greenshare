package com.ecotek.greenshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.ecotek.greenshare.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById(R.id.bottom_nav)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.container_view, HomeFragment())
        }
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_home -> {
                    moveToFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_New_content -> {
                    moveToFragment(NewContentFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_search -> {
                    moveToFragment(SearchFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    fun moveToFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.container_view, fragment)
        }
    }

}
