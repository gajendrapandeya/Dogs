package com.example.dogs.api

import com.example.dogs.model.DogBreed
import io.reactivex.Single
import retrofit2.http.GET

interface DogsApi {

    @GET("DevTides/DogsApi/master/dogs.json")
    fun getDogs(): Single<List<DogBreed>>
}