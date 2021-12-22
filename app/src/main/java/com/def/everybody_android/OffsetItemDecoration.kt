package com.def.everybody_android

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class OffsetItemDecoration(private val ctx: Context) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset = (screenWidth / 2.toFloat()).toInt()
        val lp = view.layoutParams as MarginLayoutParams
        when {
            parent.getChildAdapterPosition(view) == 0 -> {
                lp.leftMargin = 0
                setupOutRect(outRect, offset, true)
            }
            parent.getChildAdapterPosition(view) == state.itemCount - 1 -> {
                lp.rightMargin = 0
                setupOutRect(outRect, offset, false)
            }
            else -> {
                outRect.left = ctx.convertDpToPx(6)
                outRect.right = ctx.convertDpToPx(6)
            }
        }
    }

    private fun setupOutRect(rect: Rect, offset: Int, start: Boolean) {
        if (start) {
            rect.left = offset
            rect.right = ctx.convertDpToPx(6)
        } else {
            rect.right = offset
            rect.left = ctx.convertDpToPx(6)
        }
    }

    private val screenWidth: Int
        get() {
            val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }

}