package com.example.android.politicalpreparedness.repository

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.asDatabaseModel
import com.example.android.politicalpreparedness.util.Util.Companion.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ElectionRepository(
    private val civicApi: CivicsApi,
    private val database: ElectionDatabase,
    private val applicationContext: Context
) {
     val followedElection:LiveData<List<Election>> = database.electionDao.getFollowedElections(true)


    suspend fun getElections():List<Election> {
         lateinit var elections:List<Election>
        try {
           // val electionResponse = CivicsApi.retrofitService.getElections()
            if(isNetworkAvailable(applicationContext)) {
                elections = civicApi.getElections().elections.asDatabaseModel()
                for(election in elections){
                    election.isFollowed = false
                }
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

    suspend fun insertElection(election:Election) {
        database.electionDao.insert(election)
    }

    suspend fun updateElection(election:Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.updateElection(election.isFollowed, election.id)
        }
    }

    suspend fun getElection(election:Election): Election? {
        var existingElection:Election? =null
        withContext(Dispatchers.IO) {
            existingElection= election?.id?.let { database.electionDao.get(it) }
        }
        return existingElection
    }

     suspend fun getFollowedElections(): LiveData<List<Election>>? {
         var existingElections: LiveData<List<Election>>? =null

         withContext(Dispatchers.IO) {
             existingElections = database.electionDao.getFollowedElections()
         }
         return existingElections
    }

//    fun getElections():List<Election> {
//        return database.electionDao.getAll()
//    }
}