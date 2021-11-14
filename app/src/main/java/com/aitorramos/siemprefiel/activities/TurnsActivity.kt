package com.aitorramos.siemprefiel.activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.aitorramos.mylibrary.ToolbarActivity
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.adapters.PageAdapterTurns
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_turns.*

class TurnsActivity : ToolbarActivity() {

    val fragmentAdapter = PageAdapterTurns(supportFragmentManager)
    val daysOfWeek = arrayOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turns)
        window.statusBarColor = resources.getColor(R.color.colorAccent)
        setTitle("Turno ${daysOfWeek[intent.getStringExtra("idDay").toInt() -1]}")
        toolbarToLoad(toolbar as Toolbar)
        enableHomeDisplay(true)

        view_pager.adapter = fragmentAdapter

        tab.setupWithViewPager(view_pager)
        tab.getTabAt(0)!!.setIcon(R.drawable.ic_sun)
        tab.getTabAt(1)!!.setIcon(R.drawable.ic_moon)
        tab.getTabAt(0)!!.icon!!.setColorFilter(Color.parseColor("#016856"), PorterDuff.Mode.SRC_IN)
        tab.getTabAt(1)!!.icon!!.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN)
        //tab.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"))

        tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                tab.getTabAt(p0!!.position)!!.icon!!.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                tab.getTabAt(p0!!.position)!!.icon!!.setColorFilter(Color.parseColor("#016856"), PorterDuff.Mode.SRC_IN)
            }

        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
