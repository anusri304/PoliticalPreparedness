package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.VoterInfoRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application) : ViewModel() {
    private val database = ElectionDatabase.getInstance(application)
    private val voterInfoRepository = VoterInfoRepository(CivicsApi,database)
    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    private val _selectedElection = MutableLiveData<Election>()
    val selectedElection : LiveData<Election>
        get() = _selectedElection

    fun displayElectionInfo(data: Election) {
        _selectedElection.value = data
        displayVoterInfo(data)
    }

    private fun displayVoterInfo(election: Election) {
        viewModelScope.launch {
            try {
                val MOCK_STATE = "ks"
                val state = if(election.division.state.isEmpty()) MOCK_STATE else election.division.state
                val address = "${state},${election.division.country}"
                voterInfoRepository.saveVoterInfo(address,election.id)
            }
           catch(e:Exception) {
                   e.stackTrace
           }
        }
    }

}