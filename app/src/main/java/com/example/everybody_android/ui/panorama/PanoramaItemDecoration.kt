package com.example.everybody_android.ui.panorama

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everybody_android.convertDpToPx


class PanoramaItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = lp.spanIndex
        if (spanIndex == 0) outRect.right = view.context.convertDpToPx(1)
        if (spanIndex == 1) {
            outRect.right = view.context.convertDpToPx(1)
            outRect.left = view.context.convertDpToPx(1)
        } else outRect.left = view.context.convertDpToPx(1)
    }
}