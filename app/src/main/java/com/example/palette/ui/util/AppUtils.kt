package com.example.palette.ui.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.palette.R

fun changeFragment(fragment: Fragment, supportFragmentManager: FragmentManager) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.mainContent, fragment)

    transaction.commit()
}