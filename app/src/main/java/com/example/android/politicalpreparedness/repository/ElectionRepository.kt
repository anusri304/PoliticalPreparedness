package com.example.android.politicalpreparedness.repository

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.Util.Companion.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(
    private val civicApi: CivicsApi,
    private val database: ElectionDatabase,
    private val applicationContext: Context
) {

    suspend fun getElections():List<Election> {
         lateinit var elections:List<Election>
        try {
           // val electionResponse = CivicsApi.retrofitService.getElections()
            if(isNetworkAvailable(applicationContext)) {
                elections = civicApi.getElections().elections
                println("elections" + elections)
            }
            else {
                withContext(Dispatchers.IO) {
                    elections = database.getAll()
                }
            }
        }
        catch(e:Exception) {
            e.stackTrace
        }
        return elections
    }

    suspend fun insertElections() {
        database.electionDao.insertAll(getElections())
    }

//    fun getElections():List<Election> {
//        return database.electionDao.getAll()
//    }
}