package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(val app: Application, val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>>
        get() = _states

    // Establish live data for representatives and address
  //  val _address = MutableLiveData<Address>()
    val address = MutableLiveData(Address("", "", "", "", ""))




    private val database = ElectionDatabase.getInstance(app)
    private val representativeRepository =
        RepresentativeRepository(CivicsApi, database, app.applicationContext)

    val representatives = representativeRepository.representatives

    val selectedIndex = MutableLiveData<Int>()

    val showSnackBar = MutableLiveData<String>()

    private val _status: MutableLiveData<ApiStatus> = MutableLiveData()
    val status: LiveData<ApiStatus>
        get() = _status

    private val addressObserver = Observer<Address> {
        address.value = it
        getRepresentatives()
    }

    init {
        _states.value = app.resources.getStringArray(R.array.states).toList()
        savedStateHandle.getLiveData<Address>("address").observeForever(addressObserver)
    }

    // Create function to fetch representatives from API from a provided address
    fun getRepresentatives() {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                address.value!!.state = states.value!!.toList()[selectedIndex.value!!]
                representativeRepository.getRepresentatives(address.value!!.toFormattedString())
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Timber.e(app.getString(R.string.error_representatives) + e.stackTrace)
                _status.value = ApiStatus.ERROR
                showSnackBar.value = app.getString(R.string.address_not_found)
            }

        }
    }

    fun setAddress(currentAddress: Address) {
        address.value = currentAddress
        savedStateHandle["address"] = currentAddress
    }

    fun getLocation(currentAddress: Address) {
        // Get the selected state index from the address
        val index = _states.value?.indexOf(currentAddress.state)
        if (index != null && index >= 0) {
            // Set the selected index value
            selectedIndex.value = index!!
            address.value = currentAddress
            getRepresentatives()
        } else {
            showSnackBar.value = app.getString(R.string.current_location_not_in_us)
        }
    }
    override fun onCleared() {
        super.onCleared()
        savedStateHandle.getLiveData<Address>("address").removeObserver(addressObserver)
    }

}
