package com.pipecodingclub.travelbook.search.deco

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// rv_gridlayout 아이템 간격을 설정하는 ItemDecoration 클래스 생성
class LocalItemDeco(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // 아이템 위치

        // 그리드 레이아웃 아이템 간의 간격 계산
        val column = position % spanCount
        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}