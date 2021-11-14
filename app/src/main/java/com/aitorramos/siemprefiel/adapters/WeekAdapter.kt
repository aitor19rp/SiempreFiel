package com.aitorramos.siemprefiel.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.inflate
import com.aitorramos.siemprefiel.listeners.RecyclerWeekListener
import com.aitorramos.siemprefiel.models.Week
import kotlinx.android.synthetic.main.adapter_turn.view.*
import kotlinx.android.synthetic.main.adapter_week.view.*

class WeekAdapter(private val items: List<Week>, private val listener: RecyclerWeekListener)
    : RecyclerView.Adapter<WeekAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(week: Week, listener: RecyclerWeekListener) = with(itemView){
            tvDayWeek.text = week.day
            cbMorning.isChecked = week.veteranM
            tvTotalMorning.text = "${week.totalM}"
            cbAfter.isChecked = week.veteranA
            tvTotalAfter.text = "${week.totalA}"

            setOnClickListener{listener.onClick(week, adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.adapter_week))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)
    override fun getItemCount() = items.size
}