package cz.jscelectronics.adeon.adapters

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.R

class RecyclerAttributeTouchHelper(
    dragDirs: Int,
    swipeDirs: Int,
    private val listener: RecyclerAttributeTouchHelperListener
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    interface RecyclerAttributeTouchHelperListener {
        fun onSwiped(viewholder: RecyclerView.ViewHolder, position: Int)
        fun onMove(viewholder: RecyclerView.ViewHolder, from: Int, to: Int)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, viewHolder.adapterPosition)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        listener.onMove(viewHolder, viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        (viewHolder as SwipableViewHolder?)?.let {
            getDefaultUIUtil().onSelected(it.swipableView)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            getDefaultUIUtil().onDraw(
                c, recyclerView, (viewHolder as SwipableViewHolder).swipableView, dX, dY,
                actionState, isCurrentlyActive
            )
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            (viewHolder as SwipableViewHolder?)?.let {
                getDefaultUIUtil().onDrawOver(
                    c, recyclerView, it.swipableView, dX, dY,
                    actionState, isCurrentlyActive
                )
            }
        } else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        getDefaultUIUtil().clearView((viewHolder as SwipableViewHolder).swipableView)
    }
}