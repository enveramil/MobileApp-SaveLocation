package com.enveramil.savelocation.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.enveramil.savelocation.model.Location

@Database(entities = arrayOf(Location::class), version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao() : LocationDao

}