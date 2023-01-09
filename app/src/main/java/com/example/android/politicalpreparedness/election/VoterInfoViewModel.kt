package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.VoterInfoRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application) : ViewModel() {
    private val database = ElectionDatabase.getInstance(application)
    private val voterInfoRepository = VoterInfoRepository(CivicsApi, database)

    private val electionRepository =
        ElectionRepository(CivicsApi, database, application.applicationContext)

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    private val _selectedElection = MutableLiveData<Election>()
    val selectedElection: LiveData<Election>
        get() = _selectedElection

    // live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfo>()
    val voterInfo: LiveData<VoterInfo>
        get() = _voterInfo

    private val _isElectionFollowed = MutableLiveData<Boolean?>()
    val isElectionFollowed: LiveData<Boolean?>
        get() = _isElectionFollowed

    /*
    Method to display Voter info
     */
    fun displayVoterInfo(election: Election) {
        _selectedElection.value = election
        checkIfElectionSaved(election)
        saveVoterInfo(election)
    }

    // populate voter info
    private fun loadVoterInfo(id: Int) {
        viewModelScope.launch {
            _voterInfo.value = voterInfoRepository.getVoterInfo(id)
        }
    }

    /*
    Method to save voter info
     */
    private fun saveVoterInfo(election: Election) {
        viewModelScope.launch {
            try {
                val MOCK_STATE = "ks"
                val state =
                    if (election.division.state.isEmpty()) MOCK_STATE else election.division.state
                val address = "${state},${election.division.country}"
                voterInfoRepository.saveVoterInfo(address, election.id)
                loadVoterInfo(election.id)
            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    /*
       Method invoked when follow/unfollow election is clicked to update the database
     */
    fun onFollowButtonClick() {
        viewModelScope.launch {
            selectedElection.value?.let {
                if (isElectionFollowed.value == true) {
                    it.isFollowed = false
                    electionRepository.updateElection(it)
                } else {
                    it.isFollowed = true
                    electionRepository.updateElection(it)
                }
                checkIfElectionSaved(it)
            }
        }

    }
    /*
    Covenience method to check if election is saved in the database
     */

    fun checkIfElectionSaved(election: Election) {
        viewModelScope.launch {
            try {
                var existingElection: Election? = electionRepository.getElection(election)
                _isElectionFollowed.postValue(existingElection?.isFollowed)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}
