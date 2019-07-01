package app.simple.buyer.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R


class FragmentMain : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_main

    override val title: Int
        get() = R.string.app_name

    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(mActivity)

//        results = realm.getAllAsync()
//        results.addChangeListener( this )
//        recyclerView.adapter = FragmentDocsAdapter(mActivity, this::showError, results)

    }

}