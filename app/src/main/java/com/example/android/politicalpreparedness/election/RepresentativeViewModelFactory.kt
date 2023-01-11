package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel

//Factory to generate RepresentativeViewModel with provided election datasource
class RepresentativeViewModelFactory(val app:Application,val savedStateHandle: SavedStateHandle): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RepresentativeViewModel(app,savedStateHandle) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }

}