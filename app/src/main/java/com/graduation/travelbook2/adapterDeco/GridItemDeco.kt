package com.graduation.travelbook2.adapterDeco

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// rv_gridlayout 아이템 간격을 설정하는 ItemDecoration 클래스 생성
class GridItemDeco(
    private val spanCount: Int,
    private val space: Int,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount + 1      // 1부터 시작

        /** 첫번째 행(row-1)에 있는 아이템인 경우 상단에 [space] 만큼의 여백을 추가한다 */
        if (position < spanCount){
            outRect.top = space
        }
        /** 마지막 열(column-N)에 있는 아이템인 경우 우측에 [space] 만큼의 여백을 추가한다 */
        if (column == spanCount){
            outRect.right = space
        }
        /** 모든 아이템의 좌측과 하단에 [space] 만큼의 여백을 추가한다. */
        outRect.left = space
        outRect.bottom = space

    }
}