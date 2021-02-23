package com.myapp.osmnavigation.detailInfo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.osmnavigation.R

class DetailInfoAboutPlaceFragment : Fragment() {

    companion object {
        fun newInstance() = DetailInfoAboutPlaceFragment()
    }

    private lateinit var viewModel: DetailInfoAboutPlaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_info_about_place_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailInfoAboutPlaceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}