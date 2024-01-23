package com.example.chatappsoketio.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ChatItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private val itemMargin: Int = 8

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        // Add spacing to the bottom of each item
        outRect.bottom = itemMargin
    }
}