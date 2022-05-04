package com.enveramil.savelocation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Location(
    @ColumnInfo(name = "LocationName")
    var locationName : String,

    @ColumnInfo(name = "Latitude")
    var latitude : Double,

    @ColumnInfo(name = "Longitude")
    var longitude : Double)  : Serializable{

    // Body kısmında id bilgisini göstereceğiz
    @PrimaryKey(autoGenerate = true)
    var uid = 0
}