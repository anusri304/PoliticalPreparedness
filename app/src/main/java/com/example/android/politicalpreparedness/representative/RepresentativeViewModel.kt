package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import kotlinx.coroutines.launch

class RepresentativeViewModel(app:Application): ViewModel() {

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>>
        get() = _states

    // Establish live data for representatives and address
    val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val database = ElectionDatabase.getInstance(app)
    private val representativeRepository = RepresentativeRepository(CivicsApi,database,app.applicationContext)

    val representatives = representativeRepository.representatives

    val selectedIndex = MutableLiveData<Int>()

    init {
        _address.value = Address("", "","","Kansas","")
        _states.value = app.resources.getStringArray(R.array.states).toList()
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    // Create function to fetch representatives from API from a provided address
    fun getRepresentatives() {
        viewModelScope.launch {
            try {
                address.value!!.state = getState(selectedIndex.value!!)
                representativeRepository.getRepresentatives( address.value!!.toFormattedString())
            }
            catch(e:Exception) {
               e.stackTrace
            }

        }
    }

    private fun getState(selectedIndex:Int):String {
     return states.value!!.toList()[selectedIndex]

    }

    fun getLocation(address:Address) {
        val stateIndex = _states.value?.indexOf(address.state)
        if (stateIndex != null && stateIndex >= 0) {
            selectedIndex.value = stateIndex!!
            _address.value = address
            getRepresentatives()
        }
    }

}
