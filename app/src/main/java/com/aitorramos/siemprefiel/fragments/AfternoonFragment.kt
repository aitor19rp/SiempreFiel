package com.aitorramos.siemprefiel.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.activities.InfoTurnActivity
import com.aitorramos.siemprefiel.adapters.TurnAdapter
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.goToActivity
import com.aitorramos.siemprefiel.listeners.RecyclerTurnListener
import com.aitorramos.siemprefiel.models.Turn
import com.aitorramos.siemprefiel.toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_turn.view.*
import kotlinx.android.synthetic.main.fragment_afternoon.view.*

class AfternoonFragment : Fragment() {
    private lateinit var _view: View

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var turnDB: CollectionReference
    private lateinit var weekDB: CollectionReference
    private lateinit var adapter: TurnAdapter
    private lateinit var requestDb: CollectionReference
    private var turnList: ArrayList<Turn> = ArrayList()
    val daysOfWeek = arrayOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
    private var day = "0"
    var totalVeteran = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_afternoon, container, false)
        setHasOptionsMenu(true)
        day = activity!!.intent.getStringExtra("idDay")

        setUpTurnDb()
        setUpWeekDb()
        setRequestDb()
        setUpRecyclerView()
        //getTurns()
        setSwipeToRefresh()

        return _view
    }

    private fun getTurns(){
        turnList.clear()
        turnDB
            .whereEqualTo("day", day)
            .whereEqualTo("time", "A")
            .orderBy("range", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {documents ->
                totalVeteran = 0
                for(document in documents){
                    var turn = Turn()
                    turn.name = document.getString("name").toString()
                    turn.suffix = document.getString("suffix").toString()
                    turn.range = document.getString("range").toString()
                    turn.day = document.getString("day").toString()
                    turn.time = document.getString("time").toString()

                    if(turn.range == "1") {
                        totalVeteran++
                    }

                    turnList.add(turn)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener{exception ->
                activity!!.toast("Error al obtener los datos")
            }
    }

    private fun setSwipeToRefresh(){
        _view.swipeToRefresh.setOnRefreshListener{
            getTurns()
            _view.swipeToRefresh.isRefreshing = false
        }
    }

    private fun setUpTurnDb(){
        turnDB = store.collection("turns")
    }

    private fun setUpWeekDb(){
        weekDB = store.collection("weeks")
    }

    private fun setRequestDb(){
        requestDb = store.collection("request")
    }

    private fun setUpRecyclerView(){
        val layoutManager = LinearLayoutManager(context)
        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()

        adapter = TurnAdapter(turnList, object: RecyclerTurnListener {
            override fun onClick(turns: Turn, position: Int) {
            }

            override fun onDelete(turns: Turn, position: Int) {
                deleteTurn(turns.name, turns.suffix, turns.range)
            }

        })

        _view.recyclerView.adapter = adapter
    }

    private fun setAddTurnDialog(){
        var name = ""
        var suffix = ""
        var range = ""

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_turn, null)
        val builder = AlertDialog.Builder(context).setView(dialogView).setTitle("Añadir al turno de tarde")
        builder
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar"){_, _ ->

            }
        val dialog = builder.create()
        dialog.setOnShowListener{
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener{
                if(dialogView.etName.text.isNotEmpty()){
                    name = dialogView.etName.text.toString().capitalize()
                    suffix = dialogView.etSuffix.text.toString().toUpperCase()
                    when(dialogView.rbGroup.checkedRadioButtonId){
                        R.id.rbVeteran -> range = "1"
                        R.id.rbSemiVeteran -> range = "2"
                        R.id.rbVolunteer -> range = "3"
                    }

                    addTurn(name, suffix, range)

                    dialog.dismiss()
                }else{
                    activity!!.toast("El nombre debe estar relleno")
                }
            }
        }
        dialog.show()
    }

    private fun setAddRequestDialog(){
        var name = ""
        var suffix = ""

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_turn, null)
        val builder = AlertDialog.Builder(context).setView(dialogView).setTitle("Solicitar turno ${daysOfWeek[day.toInt() -1]} tarde")
        builder
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar"){_, _ ->

            }

        dialogView.rbVeteran.visibility = View.INVISIBLE
        dialogView.rbSemiVeteran.visibility = View.INVISIBLE
        dialogView.rbVolunteer.visibility = View.INVISIBLE

        val dialog = builder.create()
        dialog.setOnShowListener{
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener{
                if(dialogView.etName.text.isNotEmpty()){
                    name = dialogView.etName.text.toString().capitalize()
                    suffix = dialogView.etSuffix.text.toString().toUpperCase()

                    addRequest(name, suffix)

                    dialog.dismiss()
                }else{
                    activity!!.toast("El nombre debe estar relleno")
                }
            }
        }
        dialog.show()
    }

    private fun addTurn(name: String, suffix: String, range: String){
        val turn = hashMapOf(
            "name" to name,
            "suffix" to suffix,
            "range" to range,
            "day" to day,
            "time" to "A"
        )
        turnDB.add(turn)
            .addOnSuccessListener {
                updateWeekAdd(range)
                getTurns()
            }
            .addOnFailureListener{activity!!.toast("Error al añadir al turno")}
    }

    private fun deleteTurn(name: String, suffix: String, range: String){
        turnDB
            .whereEqualTo("name", name)
            .whereEqualTo("suffix", suffix)
            .whereEqualTo("range", range)
            .whereEqualTo("day", day)
            .whereEqualTo("time", "A")
            .get()
            .addOnSuccessListener {documents ->
                for(document in documents){
                    turnDB.document(document.id).delete()
                        .addOnSuccessListener {
                            updateWeekDelete(range)
                            //getTurns()
                            Snackbar.make(activity!!.findViewById(R.id.layout),"$name $suffix borrado del turno", Snackbar.LENGTH_LONG).setAction("Deshacer", View.OnClickListener {
                                addTurn(name, suffix, range)
                            }).show()
                        }
                        .addOnFailureListener{activity!!.toast("Error al quitar del turno")}
                }
            }.addOnFailureListener{activity!!.toast("Error al quitar del turno")}
    }

    private fun updateWeekAdd(range: String){
        weekDB
            .whereEqualTo("id", day)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val total:Long = document.get("totalA") as Long +1
                    weekDB.document(document.id).update("totalA", total)
                    if(range == "1") {weekDB.document(document.id).update("veteranA", true)}
                }
            }
    }

    private fun updateWeekDelete(range: String){
        /*if(range == "1"){
            turnDB
                .whereEqualTo("range", range)
                .whereEqualTo("day", day)
                .whereEqualTo("time", "A")
                .get()
                .addOnSuccessListener { documents ->
                    totalVeteran = documents.size() +1
                }
        }*/
        weekDB
            .whereEqualTo("id", day)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val total: Long = document.get("totalA") as Long - 1
                    weekDB.document(document.id).update("totalA", total)
                    if (range == "1" && totalVeteran == 1) {
                        weekDB.document(document.id).update("veteranA", false)
                            .addOnSuccessListener {
                                getTurns()
                            }
                    }
                }
            }
    }

    private fun addRequest(name: String, suffix: String){
        val request = hashMapOf(
            "name" to name,
            "suffix" to suffix,
            "day" to day,
            "time" to "A"
        )
        requestDb.add(request)
            .addOnSuccessListener {
                activity!!.toast("Turno solicitado correctamente")
            }
            .addOnFailureListener{activity!!.toast("Error al solicitar el turno")}
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_turns, menu)
        if(preferences!!.range == 2) menu.getItem(0).setVisible(false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            R.id.newTurn ->{
                setAddTurnDialog()
                true
            }
            R.id.turnInfo ->{
                activity!!.goToActivity<InfoTurnActivity>{
                    putExtra("day" , day)
                    putExtra("time", "A")
                }
                true
            }
            R.id.requestTurn ->{
                setAddRequestDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        getTurns()
    }

}
