package app.simple.buyer

import android.view.View
import android.widget.ImageView
import android.widget.TextView

interface IEmptyView {
    val emptyView: View?
    val emptyImageView: ImageView?
    val emptyTextTitle: TextView?
    val emptyTextSubTitle: TextView?
    val emptyData: EmptyData?

    fun toggleEmptyScreen(show: Boolean)
}