package com.aitorramos.siemprefiel.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aitorramos.siemprefiel.MainActivity
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.goToActivity

class MainEmpty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(preferences!!.range == -1)
        {
            goToActivity<LoginActivity>(){
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }else{
            goToActivity<MainActivity>(){
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
