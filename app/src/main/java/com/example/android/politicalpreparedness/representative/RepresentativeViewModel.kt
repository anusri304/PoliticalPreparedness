package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import kotlinx.coroutines.launch

class RepresentativeViewModel(val app:Application): ViewModel() {

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

    val showSnackBar = MutableLiveData<String>()

    private val _status: MutableLiveData<ApiStatus> = MutableLiveData()
    val status: LiveData<ApiStatus>
        get() = _status


    init {
        _address.value = Address("", "","","Kansas","")
        _states.value = app.resources.getStringArray(R.array.states).toList()
    }
    // Create function to fetch representatives from API from a provided address
    fun getRepresentatives() {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                address.value!!.state =states.value!!.toList()[selectedIndex.value!!]
                representativeRepository.getRepresentatives( address.value!!.toFormattedString())
                _status.value = ApiStatus.DONE
            }
            catch(e:Exception) {
               e.stackTrace
                _status.value = ApiStatus.ERROR
                showSnackBar.value = app.getString(R.string.address_not_found)
            }

        }
    }

    fun getLocation(address:Address) {
        // Get the selected state index from the address
        val index = _states.value?.indexOf(address.state)
        if (index != null && index >= 0) {
            // Set the selected index value
            selectedIndex.value = index!!
            _address.value = address
            getRepresentatives()
        }
        else {
            showSnackBar.value = app.getString(R.string.current_location_not_in_us)
        }

    }

}
