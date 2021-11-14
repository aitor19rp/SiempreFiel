package com.aitorramos.siemprefiel.listeners

import com.aitorramos.siemprefiel.models.Dog

interface RecyclerDogListener {
    fun onClick(dogs: Dog, position: Int)
    fun onDelete(dogs: Dog, position: Int)
    fun onSelect(dogs: Dog, position: Int, status: Boolean)
}