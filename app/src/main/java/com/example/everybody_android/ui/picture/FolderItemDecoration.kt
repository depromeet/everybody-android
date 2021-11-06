package com.example.everybody_android.ui.picture

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FolderItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = lp.spanIndex
        if (spanIndex == 0) {

        } else {

        }
    }
}