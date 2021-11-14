package com.aitorramos.siemprefiel.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.aitorramos.siemprefiel.fragments.AfternoonFragment
import com.aitorramos.siemprefiel.fragments.WalkDogFragment

class PageAdapterInfoTurns(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            //0 -> MorningFragment()
            else -> WalkDogFragment()
        }
    }

    override fun getCount(): Int {
        return 1
    }

}