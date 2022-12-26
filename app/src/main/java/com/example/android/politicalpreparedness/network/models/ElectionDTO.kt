package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*
@Parcelize
data class ElectionDTO (
    val id: Int,
    val name: String,
   val electionDay: Date,
    @Json(name="ocdDivisionId")
    val division: Division
): Parcelable

fun List<ElectionDTO>.asDatabaseModel(): List<Election> {
    return map {
        Election(
            id=it.id,
            name=it.name,
            electionDay = it.electionDay,
            division =  it.division,
        isFollowed = false
        )
    }
}