package com.aitorramos.siemprefiel.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.inflate
import com.aitorramos.siemprefiel.listeners.RecyclerDogListener
import com.aitorramos.siemprefiel.models.Dog
import com.aitorramos.siemprefiel.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_dog.view.*
import kotlinx.android.synthetic.main.fragment_week.view.*

class DogAdapter (private val items: List<Dog>, private val listener: RecyclerDogListener, val type: String)
    : RecyclerView.Adapter<DogAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(dog: Dog, listener: RecyclerDogListener, type: String) = with(itemView){

            if(dog.url.isNotBlank()){
                Picasso.get().load(dog.url).resize(100, 100)
                    .centerCrop().transform(CircleTransform()).into(ivPhoto)
            }

            etName.text = dog.name
            if(preferences!!.range != 1) ivDeleteDog.visibility = View.INVISIBLE
            if(type == "0") cbSelect.visibility = View.INVISIBLE
            if(type == "1") ivDeleteDog.visibility = View.INVISIBLE
            if(type == "2"){
                cbSelect.visibility = View.INVISIBLE
                ivDeleteDog.visibility = View.INVISIBLE
            }
            setOnClickListener{listener.onClick(dog, adapterPosition)}
            cbSelect.setOnClickListener{listener.onSelect(dog, adapterPosition, cbSelect.isChecked)}
            ivDeleteDog.setOnClickListener{listener.onDelete(dog, adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.adapter_dog))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener, type)
    override fun getItemCount() = items.size


}