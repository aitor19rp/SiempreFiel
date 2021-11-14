package com.aitorramos.siemprefiel.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.aitorramos.siemprefiel.fragments.AfternoonFragment
import com.aitorramos.siemprefiel.fragments.MorningFragment

class PageAdapterTurns(fm: FragmentManager): FragmentPagerAdapter (fm) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> MorningFragment()
            else -> AfternoonFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

}
