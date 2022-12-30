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
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import kotlinx.coroutines.launch

class RepresentativeViewModel(app:Application): ViewModel() {

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>>
        get() = _states

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
    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

    fun getRepresentatives() {
        viewModelScope.launch {
            try {
                address.value!!.state = getState(selectedIndex.value!!)
                val address = address.value!!.toFormattedString()
                representativeRepository.getRepresentatives(address)
            }
            catch(e:Exception) {
               e.stackTrace
            }

        }
    }

    private fun getState(selectedIndex:Int):String {
     return states.value!!.toList()[selectedIndex]

    }

}
