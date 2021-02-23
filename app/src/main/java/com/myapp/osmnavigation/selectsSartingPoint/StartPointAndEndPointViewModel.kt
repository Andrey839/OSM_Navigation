package com.myapp.osmnavigation.selectsSartingPoint

import android.content.Context
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myapp.osmnavigation.netWork.ReverseLocationToPlace
import com.myapp.osmnavigation.netWork.SearchAddress
import com.myapp.osmnavigation.repository.RepositoryNetWork

class StartPointAndEndPointViewModel(private val context: Context?) : ViewModel() {

    private val repository = RepositoryNetWork()
    private val _answerAddress = MutableLiveData<List<SearchAddress>>()
    val answerAddress: LiveData<List<SearchAddress>>
        get() = _answerAddress
    private val _answerSearch = MutableLiveData<List<ReverseLocationToPlace>>()
    val answerSearch: LiveData<List<ReverseLocationToPlace>>
        get() = _answerSearch

    //узнаём место пользователя
    var placesUser: LiveData<ReverseLocationToPlace> = repository.userPlace

    // поиск по запросу
    suspend fun getSearch(country: String, search: String) {
        _answerSearch.postValue(repository.requestPlaces(search, country))
    }

    // поиск по адресу
    suspend fun getAddress(country: String, address: String) {
        _answerAddress.postValue(repository.requestAddress(address, country))
    }

    suspend fun getPlaceWithLocationUser(location: Location) {
        repository.requestWithLocation(location)
    }
}


//  фабрика для получения контекста во StartPointAndEndPoint
class StartAndEndPointViewModelFactory(
    private val context: Context?
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartPointAndEndPointViewModel::class.java)) {
            return StartPointAndEndPointViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}