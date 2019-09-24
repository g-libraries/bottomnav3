package com.core.bottomnav.bottomnavview

interface OnBottomNavClickListener {
    fun onClicked(pos : Int, id: Int)
}

interface OnBottomNavCreated {
    fun onCreated()
}