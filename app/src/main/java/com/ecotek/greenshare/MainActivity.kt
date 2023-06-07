package com.ecotek.greenshare

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.ecotek.greenshare.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_home -> {
                    moveToFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_New_content -> {
                    moveToFragment(NewContentFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {
                    moveToFragment(SearchFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }



        /*val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        binding.fab.setOnClickListener { view ->


            myRef.setValue("Hello, World!")


        }

        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue()
                Log.d("MainActivity", "Value is: " + value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "Failed to read value.", error.toException())
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }*/

 /*   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }*/

        }

    private fun moveToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container_view, fragment).commit()
    }

}