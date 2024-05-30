package com.example.walkrewardapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.walkrewardapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Lateinit variable for view binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation host fragment and controller
        // CITE: https://issuetracker.google.com/issues/142847973?pli=1 TOOK ME 2 DAY TO SOLVE THAT DANM
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Find the BottomNavigationView
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // Set up BottomNavigationView with NavController
        navView.setupWithNavController(navController)
    }
}
