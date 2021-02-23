package com.myapp.osmnavigation.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.osmnavigation.R

class SearchPlacesFragment : Fragment() {

    companion object {
        fun newInstance() = SearchPlacesFragment()
    }

    private lateinit var viewModel: SearchPlacesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_places_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchPlacesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}