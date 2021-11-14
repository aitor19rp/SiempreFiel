package com.aitorramos.siemprefiel.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.inflate
import com.aitorramos.siemprefiel.listeners.RecyclerTurnListener
import com.aitorramos.siemprefiel.models.Turn
import kotlinx.android.synthetic.main.adapter_request.view.*

class RequestAdapter (private val items: List<Turn>, private val listener: RecyclerTurnListener)
    : RecyclerView.Adapter<RequestAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val daysOfWeek = arrayOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")

        fun bind(turn: Turn, listener: RecyclerTurnListener) = with(itemView){

            tvName.text = turn.name
            tvSuffix.text = turn.suffix
            tvDay.text = daysOfWeek[turn.day.toInt() -1]
            if(turn.time == "M")
            {
                ivTime.setImageResource(R.drawable.ic_sun)
            }else{
                ivTime.setImageResource(R.drawable.ic_moon)
            }

            setOnClickListener{listener.onClick(turn, adapterPosition)}
            delete.setOnClickListener{listener.onDelete(turn, adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.adapter_request))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RequestAdapter.ViewHolder, position: Int) = holder.bind(items[position], listener)
}