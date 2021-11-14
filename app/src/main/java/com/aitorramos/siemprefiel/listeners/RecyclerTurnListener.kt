package com.aitorramos.siemprefiel.listeners

import com.aitorramos.siemprefiel.models.Turn

interface RecyclerTurnListener {
    fun onClick(turns: Turn, position: Int)
    fun onDelete(turns: Turn, position: Int)
}