package com.aitorramos.siemprefiel.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.activities.InfoTurnActivity
import com.aitorramos.siemprefiel.activities.SelectDogsActivity
import com.aitorramos.siemprefiel.adapters.DogAdapter
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.goToActivity
import com.aitorramos.siemprefiel.listeners.RecyclerDogListener
import com.aitorramos.siemprefiel.models.Dog
import com.aitorramos.siemprefiel.toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_walk_dog.*
import kotlinx.android.synthetic.main.fragment_walk_dog.view.*

class WalkDogFragment : Fragment() {

    private lateinit var _view: View
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dogDb: CollectionReference
    private lateinit var adapter: DogAdapter
    private var dogList: ArrayList<Dog> = ArrayList()
    private var day = "0"
    private var time = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_walk_dog, container, false)
        setHasOptionsMenu(true)

        day = activity!!.intent.getStringExtra("day")
        time = activity!!.intent.getStringExtra("time")

        setDogDb()
        setUpRecyclerView()

        return _view
    }

    private fun setDogDb() {
        dogDb = store.collection("walkdog")
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()

        adapter = DogAdapter(dogList, object: RecyclerDogListener {
            override fun onClick(dogs: Dog, position: Int) {

            }

            override fun onDelete(dogs: Dog, position: Int) {

            }

            override fun onSelect(dogs: Dog, position: Int, status: Boolean) {

            }

        }, "2")

        _view.recyclerView.adapter = adapter
    }

    private fun getDogs(){
        dogList.clear()
        dogDb
            .orderBy("name")
            .whereEqualTo("day", day)
            .whereEqualTo("time", time)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    var dog = Dog()

                    dog.url = document.getString("url").toString()
                    dog.name = document.getString("name").toString()
                    dog.day = document.getString("day").toString()
                    dog.time = document.getString("time").toString()

                    dogList.add(dog)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener{
                activity!!.toast("Error al obtener los datos")
                Log.e("Prueba", it.toString())
            }
    }

    private fun noYesDialog()
    {
        val mAlertDialog = AlertDialog.Builder(context)
        mAlertDialog.setMessage("Se borrarán los perros ya asignados ¿Continuar?")
        mAlertDialog.setPositiveButton("Si") { dialog, id ->
            activity!!.goToActivity<SelectDogsActivity>{
                putExtra("day", day)
                putExtra("time", time)
            }
            dialog.dismiss()
        }
        mAlertDialog.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }
        mAlertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_select_dogs, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            R.id.selectDogs ->{
                if(dogList.size > 0)
                noYesDialog()
                else activity!!.goToActivity<SelectDogsActivity>{
                    putExtra("day", day)
                    putExtra("time", time)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        getDogs()
    }

}