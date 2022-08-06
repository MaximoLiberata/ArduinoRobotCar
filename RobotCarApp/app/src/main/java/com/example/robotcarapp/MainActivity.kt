package com.example.robotcarapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.robotcarapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var toogle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    enum class LayoutActivity {
        CONTROLLER,
        SELECT_DEVICE_LIST
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var lastLayoutActivity: LayoutActivity = LayoutActivity.CONTROLLER

        val mainLayout: DrawerLayout = findViewById(R.id.mainLayout)
        val navView: NavigationView = findViewById(R.id.navView)

        toogle = ActionBarDrawerToggle(this, mainLayout, R.string.open, R.string.close)
        mainLayout.addDrawerListener(toogle)
        toogle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.navController -> {
                    if (lastLayoutActivity != LayoutActivity.CONTROLLER) {
                        lastLayoutActivity = LayoutActivity.CONTROLLER
                        replaceFragment(Controller())
                    }
                }
                R.id.navBluetooth -> {
                    if (lastLayoutActivity != LayoutActivity.SELECT_DEVICE_LIST) {
                        lastLayoutActivity = LayoutActivity.SELECT_DEVICE_LIST
                        replaceFragment(SelectBluetoothDevice())
                    }
                }
            }

            mainLayout.closeDrawer(navView)

            true

        }

        replaceFragment(Controller())

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toogle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFragment, fragment)
        fragmentTransaction.commit()

    }

}