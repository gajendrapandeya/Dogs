package com.example.dogs.viewmodel

import android.app.Application
import android.app.NotificationManager
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.dogs.api.DogsApiService
import com.example.dogs.db.DogDatabase
import com.example.dogs.model.DogBreed
import com.example.dogs.util.NotificationsHelper
import com.example.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())

    //if the data we have retrieved is older than 5 minutes, we gonna load data from endPoint
    //else from database
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    private val dogsService = DogsApiService()

    //this allows us to observe the observable that the api gives us
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {

        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabase()
        } else {
            Toast.makeText(getApplication(), "Dog retrieved from endPoint", Toast.LENGTH_SHORT).show()
            fetchFromRemote()
        }
    }

    private fun checkCacheDuration() {

        val cachePreference = prefHelper.getCacheDuration()

        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times( 1000 * 1000 * 1000L)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun fetchFromDatabase() {
        loading.value = true
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "Dogs retrieved from Database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromRemote() {
        //making progressbar visible
        loading.value = true
        disposable.add(
                //this will return the single we defined the ApiService class
                dogsService.getDogs()
                        //passing the request call to background thread
                        .subscribeOn(Schedulers.newThread())
                        //passing the result of the process to the main thread
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {

                            override fun onSuccess(dogList: List<DogBreed>) {
                                storeDogsLocally(dogList)
                            }

                            override fun onError(e: Throwable) {

                                dogsLoadError.value = true
                                loading.value = false
                                e.printStackTrace()
                            }

                        })
        )
    }

    fun refreshBypassCache() {
        fetchFromRemote()
        NotificationsHelper(getApplication()).createNotification()
    }

    private fun dogsRetrieved(dogsList: List<DogBreed>) {
        dogs.value = dogsList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(dogsList: List<DogBreed>) {
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            //when we run the app 2nd time, we don't want our db polluted with the previous data
            dao.deleteAllDogs()
            val result = dao.insertAll(*dogsList.toTypedArray())
            var i = 0
            while (i < dogsList.size) {
                dogsList[i].uuid = result[i].toInt()
                i++
            }
            dogsRetrieved(dogsList)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}