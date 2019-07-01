package app.simple.buyer

import android.view.View
import android.widget.ImageView
import android.widget.TextView

interface IEmptyView {
    var emptyView: View?
    var emptyImageView: ImageView?
    var emptyTextTitle: TextView?
    var emptyTextSubTitle: TextView?
    val emptyData: EmptyData?

    fun toggleEmptyScreen(show: Boolean)
}