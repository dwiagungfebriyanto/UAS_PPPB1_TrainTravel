package com.example.traintravel.auth

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter (actitivy: AppCompatActivity) : FragmentStateAdapter(actitivy) {
    val page = arrayOf<Fragment>(RegisterFragment(), LoginFragment())

    override fun getItemCount(): Int {
        return page.size
    }

    override fun createFragment(position: Int): Fragment {
        return page[position]
    }
}