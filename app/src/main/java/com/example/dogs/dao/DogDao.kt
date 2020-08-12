package com.example.dogs.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.dogs.model.DogBreed

@Dao
interface DogDao {

    @Insert
    //this will insert parameter of type dogBreed into db
    //this will return a list of long i.e. the primary key we have defined as a list
    //suspend: this function need to access the db in the background thread
    suspend fun insertAll(vararg dogs: DogBreed): List<Long>

    @Query("SELECT * FROM dogbreed")
    suspend fun getAllDogs(): List<DogBreed>

    @Query("SELECT * FROM DogBreed WHERE uuid = :dogId")
    suspend fun getDog(dogId: Int): DogBreed

    @Query("DELETE FROM dogbreed")
    suspend fun deleteAllDogs()

}