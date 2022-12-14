package com.example.android.politicalpreparedness.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.util.Constants.DATABASE_NAME

@Database(entities = [Election::class, VoterInfo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ElectionDatabase : RoomDatabase() {

    abstract val electionDao: ElectionDao

    abstract val voterInfoDao: VoterInfoDao

    companion object {

        @Volatile
        private var INSTANCE: ElectionDatabase? = null

        fun getInstance(context: Context): ElectionDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ElectionDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }

    }

    fun getAll() = electionDao.getAll()
    fun insertVoterInfo(voterInfo: VoterInfo) = voterInfoDao.insert(voterInfo)
    fun insertElection(election: Election) = electionDao.insert(election)
    fun getVoterInfo(id: Int) = voterInfoDao.get(id)
    fun getElection(id: Int) = electionDao.get(id)
    fun getFollowedElections() = electionDao.getFollowedElections()



}