package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.VoterInfoRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(val application: Application) : ViewModel() {
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

    // Live Data for status of the most recent voter information request
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status


    val showSnackBar = MutableLiveData<String>()

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
            try {
                _status.value = ApiStatus.LOADING
                _voterInfo.value = voterInfoRepository.getVoterInfo(id)
                _status.value = ApiStatus.DONE
            }
            catch (e: Exception) {
                Timber.e(application.getString(R.string.error_voter_info)+e.stackTrace)
                _status.value = ApiStatus.ERROR
            }

        }
    }

    /*
    Method to save voter info
     */
    private fun saveVoterInfo(election: Election) {
        viewModelScope.launch {
            try {
                if (election.division.country.isBlank() || election.division.state.isBlank()) {
                    showSnackBar.value = application.getString(R.string.invalid_election_request)
                }
                else {
                    val address = "${election.division.state},${election.division.country}"
                    voterInfoRepository.saveVoterInfo(address, election.id)
                    loadVoterInfo(election.id)
                }
            } catch (e: Exception) {
                Timber.e(application.getString(R.string.error_voter_info)+e.stackTrace)
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
                Timber.e(application.getString(R.string.error_retrive_election_database))
            }
        }

    }
}
