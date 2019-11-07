package com.core.bottomnav.bottomnavview

interface OnBottomNavClickListener {
    fun onClicked(pos: Int, id: Int): Boolean
}

interface OnBottomNavCreated {
    fun onCreated()
}