package com.aitorramos.siemprefiel


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.aitorramos.mylibrary.ToolbarActivity
import com.aitorramos.siemprefiel.activities.LoginActivity
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.fragments.DogsFragment
import com.aitorramos.siemprefiel.fragments.RequestFragment
import com.aitorramos.siemprefiel.fragments.WeekFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ToolbarActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("Estadillo")
        window.statusBarColor = resources.getColor(R.color.colorAccent)
        toolbarToLoad(toolbar as Toolbar)

        setNavDrawer()

        fragmentTransaction(WeekFragment())
        navView.menu.getItem(0).isChecked = true
    }

    private fun setNavDrawer(){
        val toggle = ActionBarDrawerToggle(this, drawerLayout, _toolbar, R.string.open_drawer, R.string.close_drawer)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(preferences!!.range != 1) navView.menu.getItem(2).setVisible(false)
        navView.setNavigationItemSelectedListener(this)
    }

    private fun fragmentTransaction(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.schedule -> {
                setTitle("Estadillo")
                fragmentTransaction(WeekFragment())
            }
            R.id.dogs -> {
                setTitle("Perros")
                fragmentTransaction(DogsFragment())
            }
            R.id.requestTurn -> {
                setTitle("Solicitudes de turno")
                fragmentTransaction(RequestFragment())
            }
            R.id.logOut -> {
                preferences!!.range = -1
                goToActivity<LoginActivity>() {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
       return true
    }
}
