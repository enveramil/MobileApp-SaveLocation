package com.enveramil.savelocation.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.enveramil.savelocation.model.Location
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface LocationDao {
    @Query("SELECT * FROM Location")
    fun getAllData() : Flowable<List<Location>>

    @Insert
    fun insertData(location: Location) : Completable

    @Delete
    fun deleteData(location: Location) : Completable
}