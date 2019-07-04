package app.simple.buyer.fragments

import android.view.View
import android.widget.TextView
import app.simple.buyer.R
import app.simple.buyer.util.views.HolderData
import app.simple.buyer.util.views.MultiBindHolder

class ViewHolderSample(cellView: View) : MultiBindHolder<String>(cellView) {
    companion object {
        var holderData: HolderData<String> = HolderData(R.layout.cell_main_menu) { itemView: View -> ViewHolderSample(itemView) }
    }

    private val dayTitle: TextView = cellView.findViewById(R.id.tv_title)

    override fun bind(data: String) {
        dayTitle.text = data
    }
}