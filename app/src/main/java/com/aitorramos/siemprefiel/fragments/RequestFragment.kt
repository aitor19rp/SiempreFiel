package com.aitorramos.siemprefiel.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.adapters.RequestAdapter
import com.aitorramos.siemprefiel.listeners.RecyclerTurnListener
import com.aitorramos.siemprefiel.models.Turn
import com.aitorramos.siemprefiel.toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_turn.view.*
import kotlinx.android.synthetic.main.fragment_morning.view.*
import kotlinx.android.synthetic.main.fragment_morning.view.recyclerView
import kotlinx.android.synthetic.main.fragment_morning.view.swipeToRefresh
import kotlinx.android.synthetic.main.fragment_week.view.*

class RequestFragment : Fragment() {

    private lateinit var _view: View
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var requestDb: CollectionReference
    private lateinit var turnDb: CollectionReference
    private lateinit var weekDb: CollectionReference
    private lateinit var adapter: RequestAdapter
    private var requestList: ArrayList<Turn> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_request, container, false)

        setRequestDb()
        setTurnDb()
        setWeekDb()
        setSwipeToRefresh()
        setUpRecyclerView()

        return _view
    }

    private fun setRequestDb(){
        requestDb = store.collection("request")
    }

    private fun setTurnDb(){
        turnDb = store.collection("turns")
    }

    private fun setWeekDb(){
        weekDb = store.collection("weeks")
    }

    private fun setSwipeToRefresh(){
        _view.swipeToRefresh.setOnRefreshListener{
            getRequest()
            _view.swipeToRefresh.isRefreshing = false
        }
    }

    private fun setUpRecyclerView(){
        val layoutManager = LinearLayoutManager(context)
        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()

        adapter = RequestAdapter(requestList, object: RecyclerTurnListener {
            override fun onClick(turns: Turn, position: Int) {
                setAddTurnDialog(turns)
            }

            override fun onDelete(turns: Turn, position: Int) {
                deleteRequest(turns)
            }

        })

        _view.recyclerView.adapter = adapter
    }

    private fun setAddTurnDialog(turn: Turn){
        var range = ""
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_turn, null)
        val builder = AlertDialog.Builder(context).setView(dialogView).setTitle("Añadir al turno como...")
        builder
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar"){_, _ ->

            }
        dialogView.etName.visibility = View.INVISIBLE
        dialogView.etSuffix.visibility = View.INVISIBLE
        val dialog = builder.create()
        dialog.setOnShowListener{
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener{
                when(dialogView.rbGroup.checkedRadioButtonId){
                    R.id.rbVeteran -> range = "1"
                    R.id.rbSemiVeteran -> range = "2"
                    R.id.rbVolunteer -> range = "3"
                }

                addTurn(turn, range)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun addTurn(turns: Turn, range: String){
        val turn = hashMapOf(
            "name" to turns.name,
            "suffix" to turns.suffix,
            "range" to range,
            "day" to turns.day,
            "time" to turns.time
        )
        turnDb.add(turn)
            .addOnSuccessListener {
                deleteRequest(turns)
                getRequest()
                updateWeekAdd(turns, range)
            }
            .addOnFailureListener{activity!!.toast("Error al añadir al turno")}
    }

    private fun deleteRequest(turn: Turn){
        requestDb
            .whereEqualTo("name", turn.name)
            .whereEqualTo("suffix", turn.suffix)
            .whereEqualTo("day", turn.day)
            .whereEqualTo("time", turn.time)
            .get()
            .addOnSuccessListener {documents ->
                for(document in documents){
                    requestDb.document(document.id).delete()
                        .addOnSuccessListener {
                            getRequest()
                        }
                        .addOnFailureListener{activity!!.toast("Error al eliminar solicitud")}
                }
            }
    }

    private fun getRequest(){
        requestList.clear()
        requestDb.get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    var turn = Turn()
                    turn.name = document.getString("name").toString()
                    turn.suffix = document.getString("suffix").toString()
                    turn.time = document.getString("time").toString()
                    turn.day = document.getString("day").toString()

                    requestList.add(turn)
                }

                adapter.notifyDataSetChanged()

            }.addOnFailureListener{
                activity!!.toast("Error al obtener los datos")
            }
    }

    private fun updateWeekAdd(turn: Turn, range: String){
        var totalAux = ""
        var veteran = ""
        if(turn.time == "M"){
            totalAux = "totalM"
            veteran = "veteranM"
        }else{
            totalAux = "totalA"
            veteran = "veteranA"
        }
        weekDb
            .whereEqualTo("id", turn.day)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val total:Long = document.get(totalAux) as Long +1
                    weekDb.document(document.id).update(totalAux, total)
                    if(range == "1") {weekDb.document(document.id).update(veteran, true)}
                }
            }
    }

    override fun onResume() {
        getRequest()
        super.onResume()
    }
}