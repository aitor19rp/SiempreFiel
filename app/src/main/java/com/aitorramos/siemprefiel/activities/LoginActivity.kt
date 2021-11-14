package com.aitorramos.siemprefiel.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aitorramos.siemprefiel.*
import com.aitorramos.siemprefiel.app.preferences
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor = resources.getColor(R.color.colorAccent)

        btLogIn.setOnClickListener{
            collection
                .whereEqualTo("password",edtPassword.text.toString())
                .get()
                .addOnSuccessListener { documentReference ->
                    if(!documentReference.isEmpty)
                    {
                        for(document in documentReference) {
                            preferences!!.range = "${document.get("range")}".toInt()
                            goToActivity<MainActivity>() {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        }
                    }else{
                        toast("Contraseña incorrecta")
                        edtPassword.setText("")
                    }
                }
                .addOnFailureListener { e ->
                    toast("Error al conectar")
                    Log.e("Prueba", e.toString())
                }
        }

    }
}
