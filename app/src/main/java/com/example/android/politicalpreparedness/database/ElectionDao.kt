package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {
    // insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(election: List<Election>)

    // Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAll():List<Election>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(election: Election)

    //Select single election query
    @Query("SELECT * FROM election_table WHERE id = :id")
    fun get(id: Int) : Election

    @Query("SELECT * FROM election_table WHERE isFollowed = :isFollowed")
    fun getFollowedElections(isFollowed: Boolean = true) : LiveData<List<Election>>

    @Query("update election_table set isFollowed=:isFollowed where id=:electionId")
    fun updateElection(isFollowed:Boolean,electionId:Int)




}