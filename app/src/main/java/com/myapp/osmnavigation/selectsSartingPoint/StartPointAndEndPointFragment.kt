package com.myapp.osmnavigation.selectsSartingPoint

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
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.myapp.osmnavigation.R
import com.myapp.osmnavigation.adapterRecycler.AdapterResultSearch
import com.myapp.osmnavigation.adapterRecycler.ListenerCallbackTouch
import com.myapp.osmnavigation.databinding.StartPointAndEndPointFragmentBinding
import com.myapp.osmnavigation.netWork.toInfoAddress
import com.myapp.osmnavigation.util.Const
import com.myapp.osmnavigation.util.InfoPlacesList
import kotlinx.coroutines.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.osmdroid.config.Configuration


class StartPointAndEndPointFragment : Fragment() {

    private lateinit var viewModel: StartPointAndEndPointViewModel
    private lateinit var startPoint: Array<String>
    private lateinit var endPoint: Array<String>
    private var userLocation: Location? = null
    private lateinit var country: String
    private lateinit var binding: StartPointAndEndPointFragmentBinding
    private lateinit var myScope: CoroutineScope
    private val job = Job()
    private lateinit var myAdapterResults: AdapterResultSearch
    private val listAnswers = arrayListOf<InfoPlacesList>()
    private var startPointInput = false
    private var endPointInput = false
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var textInputUser = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.start_point_and_end_point_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
// устанавливаем пользовательский агент
        Configuration.getInstance().userAgentValue = context?.packageName

        myScope = CoroutineScope(Dispatchers.IO + job)
        // слушаем нажатия пользователя и в зависимости от переменных назначаем точки для начала или конца))
        myAdapterResults = AdapterResultSearch(arrayListOf(), ListenerCallbackTouch {
            if (startPointInput && !endPointInput) {
                startPoint =
                    arrayOf(it.lat, it.lon)
                binding.editTextStart.setText(it.name, TextView.BufferType.EDITABLE)
                binding.listStart.adapter = null
                binding.linearEndPoint.visibility = View.VISIBLE
                binding.listEnd.visibility = View.VISIBLE
                binding.buttonSendPoints.visibility = View.VISIBLE
            } else {
                endPoint = arrayOf(it.lat, it.lon)
                binding.editTextEnd.setText(it.name, TextView.BufferType.EDITABLE)
                binding.buttonSendPoints.visibility = View.VISIBLE
            }
            listAnswers.clear()
            myAdapterResults.setData(listAnswers)
        }
        )
        // принимаем сообщения
        broadcastReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.getParcelableExtra<Location>(Const.KEY_LAT_LNG) != null) {
                        userLocation =
                            intent.getParcelableExtra(Const.KEY_LAT_LNG) as Location

                        myScope.launch {
                            viewModel.getPlaceWithLocationUser(userLocation!!)
                        }
                    }
                }
            }

        // регтстрируем broadcast
        val intentFilter = IntentFilter(Const.SET_ACTION)
        context?.let{
            LocalBroadcastManager.getInstance(it).registerReceiver(broadcastReceiver, intentFilter)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, StartAndEndPointViewModelFactory(context)).get(
            StartPointAndEndPointViewModel::class.java
        )

        binding.viewStartAndEndPoint = viewModel
// узнаём название страны
        viewModel.placesUser.observe(viewLifecycleOwner, {
            Log.e("tyi", "country ${it.features.first().properties?.address?.country}")
            Log.e("tyi", "icon ${it.features.first().properties?.icon}")
            country = it.features.first().properties?.address?.country ?: ""
        })
// по нажатию на иконку добавить свою локацию, записываем её в переменную и и добавляем надпись в поле начальной точки
        binding.addLocation.setOnClickListener {
            startPoint =
                arrayOf(userLocation?.latitude.toString(), userLocation?.longitude.toString())
            binding.editTextStart.setText(
                getString(R.string.select_user_location),
                TextView.BufferType.EDITABLE
            )
        }

        binding.buttonSendPoints.setOnClickListener {
            this.findNavController().navigate(
                    StartPointAndEndPointFragmentDirections.actionStartPointAndEndPointFragmentToMapFragment(
                        startPoint,
                        endPoint
                    )
                )
        }
// слушаем клавиатуру
        activity?.let { active ->
            KeyboardVisibilityEvent.setEventListener(active, viewLifecycleOwner, {
                if (!it && textInputUser.isNotEmpty()) {
                    myScope.launch {
                        // добавляем запятую после каждого пробела для лучшего поиска и вызываем поиск
                        viewModel.getAddress(country, textInputUser.replace(' ', ',', false))
                        Log.e("tyi", "input text ${textInputUser.replace(' ', ',', false)}")
                    }
                }
            })
        }
// слушаем ввод пользователя начальной точки и конечной и вызываем поиск
        inputStartPointUser()

        inputEndPointUser()
// слушаем ответ на поиск и выводим его в список
        viewModel.answerAddress.observe(viewLifecycleOwner, {
            if (startPointInput && !endPointInput) {
                binding.listStart.adapter = myAdapterResults
                listAnswers.addAll(it.toInfoAddress())
                myAdapterResults.setData(listAnswers)
                binding.linearEndPoint.visibility = View.GONE
                binding.listEnd.visibility = View.GONE
                binding.buttonSendPoints.visibility = View.GONE
            } else {
                binding.listEnd.adapter = myAdapterResults
                listAnswers.addAll(it.toInfoAddress())
                myAdapterResults.setData(listAnswers)
            }
        })
    }

    private fun inputEndPointUser() {
        binding.editTextEnd.doAfterTextChanged {
            startPointInput = false
            endPointInput = true
            if (it.toString().length >= 4) {
                textInputUser = it.toString().replace(' ', ',', false)
            }
        }
    }

    private fun inputStartPointUser() {
        binding.editTextStart.doAfterTextChanged {
            if (it.toString().length >= 4 && it.toString() != getString(R.string.select_user_location)) {
                startPointInput = true
                endPointInput = false
                textInputUser = it.toString().replace(' ', ',', false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        context?.unregisterReceiver(broadcastReceiver)
    }
}