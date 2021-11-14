package com.aitorramos.siemprefiel.app

import android.app.Application
import com.aitorramos.siemprefiel.utils.MyShared

val preferences: MyShared? by lazy { MyApp.prefs }

class MyApp : Application(){

    companion object{
        var prefs: MyShared? = null
    }

    override fun onCreate() {
        super.onCreate()
        prefs = MyShared(applicationContext)
    }
}