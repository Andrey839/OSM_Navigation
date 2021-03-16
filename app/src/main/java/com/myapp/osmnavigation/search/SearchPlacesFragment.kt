package com.myapp.osmnavigation.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.myapp.osmnavigation.R
import com.myapp.osmnavigation.adapterRecycler.AdapterSelectPOIPlaces
import com.myapp.osmnavigation.adapterRecycler.ListenerCallbackPOISelect
import com.myapp.osmnavigation.databinding.SearchPlacesFragmentBinding
import com.myapp.osmnavigation.util.NameTypes
import kotlinx.coroutines.*
import org.osmdroid.bonuspack.location.FlickrPOIProvider
import org.osmdroid.bonuspack.location.GeoNamesPOIProvider
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.location.POI

class SearchPlacesFragment : Fragment() {

    private lateinit var viewModel: SearchPlacesViewModel
    private lateinit var binding: SearchPlacesFragmentBinding
    private lateinit var poiProvader: NominatimPOIProvider
    private lateinit var poiFlickrProvider: FlickrPOIProvider
    private lateinit var poiGeoNames: GeoNamesPOIProvider
    private lateinit var job: Job
    private lateinit var myScope: CoroutineScope
    private lateinit var adapter: AdapterSelectPOIPlaces
    private val listPlaces = arrayListOf<POI>()
    private val listPOIRecycler = arrayListOf<POI>()
    private val addedChipName = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.search_places_fragment, container, false)

        job = Job()
        myScope = CoroutineScope(Dispatchers.IO + job)
// слушаем какие места выбирает пользователь и добавляем их в список выбранного
        adapter = AdapterSelectPOIPlaces(arrayListOf(), ListenerCallbackPOISelect {
            listPlaces.add(it)
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchPlacesViewModel::class.java)
// инициализируем провайдеры
        poiProvader = NominatimPOIProvider(context?.packageName)
        poiFlickrProvider = FlickrPOIProvider(getString(R.string.key_flickr))
        poiGeoNames = GeoNamesPOIProvider(getString(R.string.geo_names))

        val argumentRoad = arguments?.let { SearchPlacesFragmentArgs.fromBundle(it) }?.listRoad
// получаем данные вдоль дороги и изображение
        myScope.launch {
            viewModel.poisRoad(poiProvader, argumentRoad)
        }

        viewModel.pois.observe(viewLifecycleOwner, {
// добавляем кнопки выбора типов мест рядом
            addChipToGroupChip(it)

            myScope.launch {
                viewModel.poisFlickr(it, getString(R.string.key_flickr))
            }
        })

        viewModel.poisFlickr.observe(viewLifecycleOwner, {

            listPOIRecycler.addAll(it)
            binding.recyclerResultsPOI.adapter = adapter
            adapter.setData(listPOIRecycler)
        })
        // слушаем нажатия по фильтру
        binding.chipGroupTypes.setOnCheckedChangeListener { group, checkedId ->

        }
    }

    private fun addChipToGroupChip(it: ArrayList<POI>) {
        for (nameType in NameTypes.values()) {
            for (poi in it){
                if (nameType.s.equals(poi.mType, ignoreCase = true)) {
                    val chip = LayoutInflater.from(context).inflate(R.layout.chip_standart, null, false) as Chip
                    chip.text = nameType.name
                    if (nameType.name !in addedChipName){
                        binding.chipGroupTypes.addView(chip)
                        addedChipName.add(nameType.name)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

}