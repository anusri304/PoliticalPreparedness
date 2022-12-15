package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application): ViewModel() {

    private val database = getInstance(application)
    private val electionRepository = ElectionRepository(CivicsApi,database,application.applicationContext)
    //TODO: Create live data val for upcoming elections

    private val _upcomingElections = MutableLiveData<List<Election>>()

    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _navigateToSelectedElection = MutableLiveData<Election>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedElection: LiveData<Election>
        get() = _navigateToSelectedElection


    init {
        viewModelScope.launch {
            try {
                _upcomingElections.value = electionRepository.getElections()
                println("  _upcomingElections.value"+ _upcomingElections.value!!.size)
            }
            catch (e:Exception) {

            }
        }
    }

    fun displayElection(election: Election) {
        _navigateToSelectedElection.value = election
    }

    fun displayElectionComplete() {
        _navigateToSelectedElection.value = null
    }

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}