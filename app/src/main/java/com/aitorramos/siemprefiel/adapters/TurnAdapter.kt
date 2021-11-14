package com.aitorramos.siemprefiel.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.inflate
import com.aitorramos.siemprefiel.listeners.RecyclerTurnListener
import com.aitorramos.siemprefiel.models.Turn
import kotlinx.android.synthetic.main.adapter_turn.view.*

class TurnAdapter (private val items: List<Turn>, private val listener: RecyclerTurnListener)
    : RecyclerView.Adapter<TurnAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(turn: Turn, listener: RecyclerTurnListener) = with(itemView){

            etName.text = turn.name
            tvSuffix.text = turn.suffix
            if(turn.range == "1"){
                range.setBackgroundColor(resources.getColor(R.color.colorAccentDark))
            }else if(turn.range == "2") {
                range.setBackgroundColor(resources.getColor(R.color.colorAccent))
            }else if(turn.range == "3") {
                range.setBackgroundColor(resources.getColor(R.color.gray))
            }

            if(preferences!!.range != 1) delete.visibility = View.INVISIBLE

            setOnClickListener{listener.onClick(turn, adapterPosition)}
            delete.setOnClickListener{listener.onDelete(turn, adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.adapter_turn))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)
    override fun getItemCount() = items.size


}