package com.graduation.travelbook2.search.listenerNcallback

interface ItemTouchHelperListener {
    fun onItemMove(form_position: Int, to_position: Int): Boolean
    fun onItemSwipe(position: Int)
}