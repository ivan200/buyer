package app.simple.buyer

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

interface IRefreshView : SwipeRefreshLayout.OnRefreshListener {
    var refreshView: SwipeRefreshLayout?
    fun toggleRefreshing(spin: Boolean)
}