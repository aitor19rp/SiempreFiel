package com.aitorramos.siemprefiel.listeners

import com.aitorramos.siemprefiel.models.Week

interface RecyclerWeekListener{
    fun onClick(week: Week, position: Int)
}