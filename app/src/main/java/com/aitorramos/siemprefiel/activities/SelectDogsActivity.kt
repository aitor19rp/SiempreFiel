package com.aitorramos.siemprefiel.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitorramos.mylibrary.ToolbarActivity
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.adapters.DogAdapter
import com.aitorramos.siemprefiel.listeners.RecyclerDogListener
import com.aitorramos.siemprefiel.models.Dog
import com.aitorramos.siemprefiel.toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_select_dogs.*
import kotlinx.android.synthetic.main.fragment_morning.view.*
import kotlin.system.measureTimeMillis

class SelectDogsActivity : ToolbarActivity() {

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dogDb: CollectionReference
    private lateinit var dogWalkDb: CollectionReference
    private lateinit var adapter: DogAdapter
    private var dogList: ArrayList<Dog> = ArrayList()
    private var dogListAux: ArrayList<Dog> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_dogs)
        setTitle("Seleccionar perros paseados")
        window.statusBarColor = resources.getColor(R.color.colorAccent)
        toolbarToLoad(toolbar as Toolbar)
        enableHomeDisplay(true)

        setDogDb()
        setDogWalkDb()
        setUpRecyclerView()
        getDogs()
        clearWalkDogs()

        btAddWalkDogs.setOnClickListener{
            for(dog in dogListAux){
                addWalkDogs(dog)
            }
            onBackPressed()
        }

    }

    private fun setDogDb() {
        dogDb = store.collection("dogs")
    }

    private fun setDogWalkDb(){
        dogWalkDb = store.collection("walkdog")
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        adapter = DogAdapter(dogList, object: RecyclerDogListener {
            override fun onClick(dogs: Dog, position: Int) {

            }

            override fun onDelete(dogs: Dog, position: Int) {

            }

            override fun onSelect(dogs: Dog, position: Int, status: Boolean) {
                if(status){
                    dogListAux.add(dogs)

                }else{
                    dogListAux.remove(dogs)
                }
            }

        }, "1")

        recyclerView.adapter = adapter
    }

    private fun getDogs(){
        dogList.clear()
        dogDb
            .orderBy("name")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    var dog = Dog()

                    dog.url = document.getString("url").toString()
                    dog.name = document.getString("name").toString()
                    dogList.add(dog)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener{
                toast("Error al obtener los datos")
            }
    }

    private fun addWalkDogs(dog: Dog){
        val dog = hashMapOf(
            "name" to dog.name,
            "url" to dog.url,
            "day" to intent.getStringExtra("day"),
            "time" to intent.getStringExtra("time")
        )

        dogWalkDb.add(dog)
            .addOnFailureListener{
                toast("Error al asignar")
            }
    }

    private fun clearWalkDogs(){
        dogWalkDb
            .whereEqualTo("day", intent.getStringExtra("day"))
            .whereEqualTo("time", intent.getStringExtra("time"))
            .get()
            .addOnSuccessListener {documents ->
                for(document in documents){
                    dogWalkDb.document(document.id).delete()
                }

            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}