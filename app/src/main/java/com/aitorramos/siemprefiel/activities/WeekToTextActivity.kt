package com.aitorramos.siemprefiel.activities

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.widget.Toolbar
import com.aitorramos.mylibrary.ToolbarActivity
import com.aitorramos.siemprefiel.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_week_to_text.*
import android.content.ClipData
import android.content.ClipboardManager
import android.util.Log
import com.aitorramos.siemprefiel.toast


class WeekToTextActivity : ToolbarActivity() {

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var turnDB: CollectionReference
    private lateinit var weekDB: CollectionReference
    val daysOfWeek = arrayOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
    private var textListM: ArrayList<String> = ArrayList(7)
    private var textListA: ArrayList<String> = ArrayList(7)
    private val myClipboard: ClipboardManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_to_text)
        window.statusBarColor = resources.getColor(R.color.colorAccent)
        setTitle("Compartir estadillo")
        toolbarToLoad(toolbar as Toolbar)
        enableHomeDisplay(true)

        tvWeek.movementMethod = ScrollingMovementMethod()

        val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        for(i in 0..6)
        {
            textListM.add("")
            textListA.add("")
        }
        setUpTurnDb()
        setUpWeekDb()
        turnToText("M")
        turnToText("A")

        btCopy.setOnClickListener{
            val myClip = ClipData.newPlainText("text", tvWeek.text.toString())
            myClipboard!!.setPrimaryClip(myClip)
            toast("Estadillo copiado al portapapeles")
        }

        btGenerate.setOnClickListener{
            weekToText()
        }
    }

    private fun weekToText(){
        tvWeek.text = ""
        tvWeek.append("\n" + intent.getStringExtra("week") + "\n\n")
        for(i in 0..6){
            tvWeek.append("-- " + daysOfWeek[i].toUpperCase() + ":\n")
            tvWeek.append("\t" + "- Mañana: " + textListM[i] + "\n")
            tvWeek.append("\t" + "- Tarde: " + textListA[i] + "\n\n")
        }
    }

    private fun turnToText(time: String){
        var day = "1"
        var flag = true;
        var text: String = ""
        turnDB
            .orderBy("day")
            .orderBy("range")
            .whereEqualTo("time", time)
            .get()
            .addOnSuccessListener {documents ->
                for(document in documents){
                    if(day != document.getString("day").toString()) {
                        text = ""
                        day = document.getString("day").toString()
                    }
                        when(document.getString("range").toString()){
                            "1" -> text += (document.getString("name")!!.toUpperCase() + " " + document.getString("suffix")!!.toUpperCase() + " ")
                            "2" -> text += ("*" + document.getString("name")!!.toUpperCase() + " " + document.getString("suffix")!!.toUpperCase() + " ")
                            "3" -> text += ("*" + document.getString("name") + " " + document.getString("suffix")!!.toUpperCase() + " ")
                        }

                        if(time == "M")
                            textListM.set(day.toInt() - 1, text)
                        else
                            textListA.set(day.toInt() - 1, text)
                }
            }.addOnFailureListener{
                Log.e("Prueba", it.toString())
            }

    }

    private fun setUpTurnDb(){
        turnDB = store.collection("turns")
    }

    private fun setUpWeekDb(){
        weekDB = store.collection("weeks")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
