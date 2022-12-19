package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo

@Dao
interface VoterInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(voterInfo: VoterInfo)

    @Query("SELECT * FROM voter_info WHERE id = :id")
    fun get(id: Int) : VoterInfo


}