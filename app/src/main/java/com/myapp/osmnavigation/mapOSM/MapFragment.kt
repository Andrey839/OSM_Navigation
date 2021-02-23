package com.myapp.osmnavigation.mapOSM

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myapp.osmnavigation.R
import com.myapp.osmnavigation.databinding.MapFragmentBinding
import kotlinx.coroutines.*
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var binding: MapFragmentBinding

    private lateinit var viewModel: MapViewModel

    private val job = Job()
    private lateinit var myScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.map_fragment, container, false)

        myScope = CoroutineScope(Dispatchers.IO+job)
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
        Log.e("tyi","start end ${startPoint?.first()}${startPoint?.last()} ${endPoint?.first()}${endPoint?.last()}")

        val mapController = binding.myMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }.controller
        mapController.setZoom(25)
        mapController.setCenter(GeoPoint(startPoint?.first()?.toDouble()!!, startPoint.last().toDouble()))
        val markerStart = Marker(binding.myMap)
        val markerEnd = Marker(binding.myMap)
        markerStart.position = GeoPoint(startPoint.first().toDouble(), startPoint.last().toDouble())
        markerStart.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        markerEnd.position = GeoPoint(endPoint?.first()?.toDouble()!!, endPoint.last().toDouble())
        markerEnd.setAnchor(Marker.ANCHOR_TOP, Marker.ANCHOR_CENTER)
        binding.myMap.invalidate()

        myScope.launch {
            val roadManager = OSRMRoadManager(context)
            val roadList = arrayListOf(GeoPoint(startPoint.first().toDouble(), startPoint.last().toDouble()),  GeoPoint(
                endPoint.first().toDouble(), endPoint.last().toDouble()))
            val road = roadManager.getRoad(roadList)
            val poligon = RoadManager.buildRoadOverlay(road)
            binding.myMap.overlayManager.add(poligon)
        }
        if (job.isCompleted) binding.myMap.invalidate()

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}