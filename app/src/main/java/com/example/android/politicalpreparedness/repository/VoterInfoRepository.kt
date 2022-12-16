package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoterInfoRepository( private val civicApi: CivicsApi, private val database: ElectionDatabase) {

    suspend fun saveVoterInfo(address:String, id:Int) {
        withContext(Dispatchers.IO) {
            try {
                val response = civicApi.getVoterInfo(address, id)
                print(response)
            }
            catch (e:Exception){
                e.stackTrace
            }
        }
    }
}