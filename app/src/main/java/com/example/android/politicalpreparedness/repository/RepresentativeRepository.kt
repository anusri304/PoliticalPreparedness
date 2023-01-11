package com.example.android.politicalpreparedness.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.model.Representative
import timber.log.Timber

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
           Timber.e(applicationContext.getString(R.string.error_representatives))
        }
    }
}