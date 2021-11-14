package com.aitorramos.siemprefiel.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.activities.TurnsActivity
import com.aitorramos.siemprefiel.activities.WeekToTextActivity
import com.aitorramos.siemprefiel.adapters.WeekAdapter
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.goToActivity
import com.aitorramos.siemprefiel.listeners.RecyclerWeekListener
import com.aitorramos.siemprefiel.models.Week
import com.aitorramos.siemprefiel.toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_week.view.*
import kotlinx.android.synthetic.main.fragment_week.*
import kotlinx.android.synthetic.main.fragment_week.view.*


class WeekFragment : Fragment() {

    private lateinit var _view: View
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var weekDb: CollectionReference
    private lateinit var turnDB: CollectionReference
    private lateinit var requestDb: CollectionReference
    private lateinit var walkDogDb: CollectionReference
    private lateinit var adapter: WeekAdapter
    private val weekList: ArrayList<Week> = ArrayList()

    private val months = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_week, container, false)

        if(preferences!!.range == 1) {
            setHasOptionsMenu(true)
        }

        setUpWeekDb()
        setUpTurnDb()
        setUpRequestDb()
        setUpWalkDogDb()
        setUpRecyclerView()
        //getWeek()
        setSwipeToRefresh()

        return _view
    }

    private fun setUpWeekDb(){
        weekDb = store.collection("weeks")
    }

    private fun setUpRequestDb(){
        requestDb = store.collection("request")
    }

    private fun setUpWalkDogDb(){
        walkDogDb = store.collection("walkdog")
    }

    private fun getWeek(){
        weekList.clear()
        weekDb
            .orderBy("id", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    var week = Week()
                    week.id = document.get("id").toString()
                    week.day = document.getString("day").toString()
                    week.veteranM = document.getBoolean("veteranM")!!
                    week.totalM = document.get("totalM").toString()
                    week.veteranA = document.getBoolean("veteranA")!!
                    week.totalA = document.get("totalA").toString()

                    if(document.get("date") != null) tvDate.text = "Estadillo ${document.get("date").toString()}"

                    weekList.add(week)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener{ exception ->
                activity!!.toast("Error al obtener los datos")
            }
    }

    private fun setUpTurnDb(){
        turnDB = store.collection("turns")
    }

    private fun setUpRecyclerView(){
        val layoutManager = LinearLayoutManager(context)
        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = WeekAdapter(weekList, object: RecyclerWeekListener{
            override fun onClick(week: Week, position: Int) {
                activity!!.goToActivity<TurnsActivity>{putExtra("idDay", week.id)}
            }
        })


        _view.recyclerView.adapter = adapter
    }

    private fun setSwipeToRefresh(){
        _view.swipeToRefresh.setOnRefreshListener{
            getWeek()
            _view.swipeToRefresh.isRefreshing = false
        }
    }

    private fun setNewWeekDialog(){
        var dayFrom: String = ""
        var dayTo: String = ""
        var month: String = ""
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_week, null)
        val builder = AlertDialog.Builder(context).setView(dialogView).setTitle("Nueva semana")

        dialogView.calendar_view.setOnRangeSelectedListener { startDate, endDate, startLabel, endLabel ->
            dayFrom = startDate.date.toString()
            dayTo = endDate.date.toString()
            month = months[endDate.month]

        }
        dialogView.calendar_view.setOnStartSelectedListener{startDate, label ->
            dayFrom = ""
            dayTo = ""
            month = ""
        }

        builder
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar"){_, _ ->

            }
        val dialog = builder.create()
        dialog.setOnShowListener{
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener{
                if(dayFrom.isNotEmpty() || dayTo.isNotEmpty()){
                    createNewWeek("$dayFrom - $dayTo $month")
                    dialog.dismiss()
                }else{
                    activity!!.toast("Debe seleccionar un rango de fechas")
                }
            }
        }
        dialog.show()
    }

    private fun createNewWeek(month: String){
        weekDb
            .orderBy("id", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    weekDb.document(document.id)
                        .update(mapOf(
                                "veteranM" to false,
                                "veteranA" to false,
                                "totalA" to 0,
                                "totalM" to 0,
                                "date" to month
                    )).addOnSuccessListener {

                        }
                        .addOnFailureListener{activity!!.toast("Error al crear nueva semana")}
                }
                deleteTurns()
                deleteRequest()
                deleteWalkDog()
                getWeek()
            }
            .addOnFailureListener{activity!!.toast("Error al crear nueva semana")}
    }

    private fun deleteTurns(){
        turnDB
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    turnDB.document(document.id).delete()
                }
            }
    }

    private fun deleteRequest(){
        requestDb
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    requestDb.document(document.id).delete()
                }
            }
    }

    private fun deleteWalkDog(){
        walkDogDb
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    walkDogDb.document(document.id).delete()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_week, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId)
        {
            R.id.newWeek ->{
                setNewWeekDialog()
                true
            }
            R.id.weekToText ->{
                activity!!.goToActivity<WeekToTextActivity>{putExtra("week", _view.tvDate.text.toString())}
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        getWeek()
    }
}
