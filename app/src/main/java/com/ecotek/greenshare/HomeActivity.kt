package com.ecotek.greenshare


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.ecotek.greenshare.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

/**
 * The class HomeActivity represents the home screen of the application.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById(R.id.bottom_nav)
        // Set the initial fragment as HomeFragment

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.container_view, HomeFragment())
        }
        // Handle the item selection events in the bottom navigation view

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_home -> {
                    moveToFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_New_content -> {
                    moveToFragment(AddPostFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment(FirebaseAuth.getInstance().currentUser?.email.toString()))
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
    /**
     * This moveToFragment Moves to the specified fragment and updates the bottom navigation view accordingly.
     *
     * @param fragment The fragment to navigate to.
     */
    fun moveToFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.container_view, fragment)
        }
        // Update the selected item in the bottom navigation view
        when(fragment){
            is HomeFragment -> bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
            is AddPostFragment -> bottomNavigationView.menu.findItem(R.id.nav_New_content).isChecked = false
        }
    }
    companion object{

    }
}