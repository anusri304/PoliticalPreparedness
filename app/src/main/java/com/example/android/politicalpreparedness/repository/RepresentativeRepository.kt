package com.example.android.politicalpreparedness.repository

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.asDatabaseModel
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.Util.Companion.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class RepresentativeRepository(
    private val civicApi: CivicsApi,
    private val database: ElectionDatabase,
    private val applicationContext: Context
) {
    private val _representatives = MutableLiveData<List<Representative>?>()
    val representatives: LiveData<List<Representative>?>
        get() = _representatives

    suspend fun getRepresentatives(address: String) {
        try {
            val response = civicApi.getRepresentatives(address)
            _representatives.value = response.offices.flatMap { office ->
                office.getRepresentatives(response.officials)
            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }
}