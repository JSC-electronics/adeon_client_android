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
        viewHolder?.let {
            val foregroundView: View = it.itemView.findViewById(R.id.view_foreground)
            getDefaultUIUtil().onSelected(foregroundView)
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
            val foregroundView: View = viewHolder.itemView.findViewById(R.id.view_foreground)
            getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
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
            viewHolder?.let {
                val foregroundView: View = viewHolder.itemView.findViewById(R.id.view_foreground)
                getDefaultUIUtil().onDrawOver(
                    c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive
                )
            }
        } else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View = viewHolder.itemView.findViewById(R.id.view_foreground)
        getDefaultUIUtil().clearView(foregroundView)
        super.clearView(recyclerView, viewHolder)
    }
}