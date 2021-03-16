package com.myapp.osmnavigation.mapOSM

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.myapp.osmnavigation.R
import com.myapp.osmnavigation.adapterRecycler.AdapterPOIRoad
import com.myapp.osmnavigation.adapterRecycler.ListenerCallbackPOI
import com.myapp.osmnavigation.databinding.MapFragmentBinding
import com.myapp.osmnavigation.repository.RepositoryNetWork
import com.myapp.osmnavigation.util.Const
import kotlinx.coroutines.*
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.location.FlickrPOIProvider
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var binding: MapFragmentBinding

    private lateinit var viewModel: MapViewModel
    private lateinit var broadCastReceiver: BroadcastReceiver

    private val job = Job()
    private lateinit var myScope: CoroutineScope

    private lateinit var poiProvader: NominatimPOIProvider
    private lateinit var poiFlickrProvider: FlickrPOIProvider
    private val pois = arrayListOf<POI>()
    private var listPoisFlicks = arrayListOf<POI>()
    private lateinit var road: Road

    private lateinit var adapterPOI: AdapterPOIRoad

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.map_fragment, container, false)

        myScope = CoroutineScope(Dispatchers.IO + job)

        // устанавливаем пользовательский агент
        Configuration.getInstance().userAgentValue = context?.packageName
        poiProvader = NominatimPOIProvider(context?.packageName)
        poiFlickrProvider = FlickrPOIProvider(getString(R.string.key_flickr))

// слушаем нажатия и переходим в детальную информацию места
        adapterPOI = AdapterPOIRoad(arrayListOf(), ListenerCallbackPOI {

        })
        val manager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerPOI.adapter = adapterPOI
        binding.recyclerPOI.layoutManager = manager

        // слушаем перемещение пользователя
        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (findNavController().currentDestination?.id == R.id.mapFragment) myLocationInMap(
                    intent
                )
            }
        }

        val intentFilter = IntentFilter(Const.SET_ACTION)
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(broadCastReceiver, intentFilter)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        binding.viewMap = viewModel
        binding.lifecycleOwner = this

        val argument = arguments?.let { MapFragmentArgs.fromBundle(it) }

        val startPoint = argument?.startPoint
        val endPoint = argument?.endPoint

        val mapController = binding.myMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }.controller
// устанавливаем начальный пунккт
        markerStartPoint(mapController, startPoint)
// прокладываем маршрут
        buildRoad(startPoint, endPoint)
// устанавливаем конечную точку
        markerEndPoint(endPoint)

        // ищем фото
        viewModel.pois.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                myScope.async { viewModel.poisFlickr(it, getString(R.string.key_flickr))}.onAwait
                myScope.async { viewModel.wikipediaPlaces(it) }.onAwait
            }
        })
        // выводим список мест
        viewModel.poisWikipedia.observe(viewLifecycleOwner,{
            pois.addAll(it)
            adapterPOI.setData(pois)
        })

        binding.imageSearch.setOnClickListener {
            this.findNavController().navigate(
                MapFragmentDirections.actionMapFragmentToSearchPlacesFragment(
                    road
                )
            )
        }
    }

    private fun myLocationInMap(intent: Intent?) {
        val markerMe = Marker(binding.myMap)
        markerMe.position =
            GeoPoint(intent?.getParcelableExtra(Const.KEY_LAT_LNG) as Location)
        markerMe.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        markerMe.title = getString(R.string.my_location)
        markerMe.icon = resources.getDrawable(R.drawable.osm_ic_follow_me)
        binding.myMap.overlays.add(markerMe)
        binding.myMap.invalidate()
    }

    private fun buildRoad(
        startPoint: Array<String>?,
        endPoint: Array<String>?
    ) {
        myScope.launch {
            val roadManager = OSRMRoadManager(context)
            val roadList = arrayListOf(
                GeoPoint(
                    startPoint?.first()?.toDouble()!!, startPoint.last()
                        .toDouble()
                ), GeoPoint(
                    endPoint?.first()?.toDouble()!!, endPoint.last().toDouble()
                )
            )
            road = roadManager.getRoad(roadList)
            val poligon = RoadManager.buildRoadOverlay(road)
            binding.myMap.overlayManager.add(poligon)
            withContext(Dispatchers.Main) {
                binding.myMap.invalidate()
            }
            viewModel.poisRoad(poiProvader, road)
        }
    }

    private fun markerEndPoint(endPoint: Array<String>?) {
        val markerEnd = Marker(binding.myMap)
        markerEnd.position = GeoPoint(endPoint?.first()?.toDouble()!!, endPoint.last().toDouble())
        markerEnd.setAnchor(Marker.ANCHOR_TOP, Marker.ANCHOR_CENTER)
        markerEnd.title = getString(R.string.end_position)
        markerEnd.icon = resources.getDrawable(R.drawable.marker_default)
        binding.myMap.overlays.add(markerEnd)
        binding.myMap.invalidate()
    }

    private fun markerStartPoint(
        mapController: IMapController,
        startPoint: Array<String>?
    ) {
        mapController.setZoom(14)
        mapController.setCenter(
            GeoPoint(
                startPoint?.first()?.toDouble()!!,
                startPoint.last().toDouble()
            )
        )
        val markerStart = Marker(binding.myMap)
        markerStart.position =
            GeoPoint(startPoint?.first()?.toDouble()!!, startPoint.last().toDouble())
        markerStart.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        markerStart.title = getString(R.string.start_position)
        markerStart.icon = resources.getDrawable(R.drawable.marker_default)
        binding.myMap.overlays.add(markerStart)
        binding.myMap.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(broadCastReceiver)
        job.cancel()
    }

}