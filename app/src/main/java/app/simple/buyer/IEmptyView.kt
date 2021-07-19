package app.simple.buyer

import android.view.View
import android.widget.ImageView
import android.widget.TextView

interface IEmptyView {
    val emptyViewRoot: View?

    val emptyView: View? get() = emptyViewRoot?.findViewById(R.id.emptyView)
    val emptyImageView: ImageView? get() = emptyViewRoot?.findViewById(R.id.emptyImageView)
    val emptyTextTitle: TextView? get() = emptyViewRoot?.findViewById(R.id.emptyTextTitle)
    val emptyTextSubTitle: TextView? get() = emptyViewRoot?.findViewById(R.id.emptyTextSubTitle)
    val emptyData: EmptyData?

    fun toggleEmptyScreen(show: Boolean)
}