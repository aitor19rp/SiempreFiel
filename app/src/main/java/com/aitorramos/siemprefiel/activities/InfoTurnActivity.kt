package com.aitorramos.siemprefiel.activities

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.aitorramos.mylibrary.ToolbarActivity
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.adapters.PageAdapterInfoTurns
import kotlinx.android.synthetic.main.activity_info_turn.*

class InfoTurnActivity : ToolbarActivity() {

    val fragmentAdapter = PageAdapterInfoTurns(supportFragmentManager)
    val daysOfWeek = arrayOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
    var time = "mañana"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_turn)
        window.statusBarColor = resources.getColor(R.color.colorAccent)
        if(intent.getStringExtra("time") == "A") time = "tarde"
        setTitle("Información ${daysOfWeek[intent.getStringExtra("day").toInt() -1].toLowerCase()} $time")
        toolbarToLoad(toolbar as Toolbar)
        enableHomeDisplay(true)

        view_pager.adapter = fragmentAdapter

        tab.setupWithViewPager(view_pager)
        tab.getTabAt(0)!!.setIcon(R.drawable.waldog)
        tab.getTabAt(0)!!.icon!!.setColorFilter(Color.parseColor("#016856"), PorterDuff.Mode.SRC_IN)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}