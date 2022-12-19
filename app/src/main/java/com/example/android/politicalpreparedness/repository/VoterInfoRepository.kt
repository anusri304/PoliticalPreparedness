package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoterInfoRepository( private val civicApi: CivicsApi, private val database: ElectionDatabase) {

    suspend fun saveVoterInfo(address:String, id:Int) {
        withContext(Dispatchers.IO) {
            try {
                val response = civicApi.getVoterInfo(address, id)
                print(response)
                convertToVoterInfo(id,response)?.let { database.insert(it) }
            }
            catch (e:Exception){
                e.stackTrace
            }
        }
    }

    private fun convertToVoterInfo(id: Int, voterInfoResponse: VoterInfoResponse) : VoterInfo? {

        lateinit var voterInfo: VoterInfo
        val state = voterInfoResponse.state

        if (state?.isNotEmpty() == true) {
            var votingLocationUrl = state[0].electionAdministrationBody.votingLocationFinderUrl

            var ballotInfoUrl =  state[0].electionAdministrationBody.ballotInfoUrl

            voterInfo = VoterInfo(
                id,
                state[0].name,
                votingLocationUrl,
                ballotInfoUrl
            )
        }

        return voterInfo
    }

    suspend fun getVoterInfo(id: Int):VoterInfo {
        var voterInfo:VoterInfo
        withContext(Dispatchers.IO) {
            voterInfo = database.getVoterInfo(id)
        }
        return voterInfo
    }

}