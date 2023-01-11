package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class ElectionsViewModel(application: Application) : ViewModel() {

    private val database = getInstance(application)
    private val electionRepository =
        ElectionRepository(CivicsApi, database, application.applicationContext)

    private val _upcomingElections = MutableLiveData<List<Election>>()

    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    // Live Data to store the status of the election request
    private val _status: MutableLiveData<ApiStatus> = MutableLiveData()
    val status: LiveData<ApiStatus>
        get() = _status



    private val _navigateToSelectedElection = MutableLiveData<Election>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedElection: LiveData<Election>
        get() = _navigateToSelectedElection


    init {
        viewModelScope.launch {
            try {
                //LiveData to hold the list of elections from the API
                _status.value = ApiStatus.LOADING
                _upcomingElections.value = electionRepository.getElections()
                Timber.i("  _upcomingElections.value" + _upcomingElections.value!!.size)
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Timber.e(application.getString(R.string.error_elections)+e.stackTrace)
                _status.value = ApiStatus.ERROR
            }
        }
    }

    // Livedata to get the saved elections from the datbase
    val followedElections = electionRepository.followedElection


    fun displayElection(election: Election) {
        _navigateToSelectedElection.value = election
    }

//     fun displayFollowedElection(){
//        viewModelScope.launch {
//            _followedElections.value = electionRepository.getFollowedElection(true)
//        }
//    }

    fun displayElectionComplete() {
        _navigateToSelectedElection.value = null
    }


}