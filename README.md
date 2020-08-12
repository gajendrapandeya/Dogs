This is a complete demo app made using Kotlin and Modern Jetpack Components.
The app simply provides the list of dog fetched from the endPoint.
It uses Retrofit for Network Operation and Room for caching.
User have full controll to add the caching time i.e. the data will be fetched from endpoint
based on the refreshTime set by the user.
The app also have swipeTorefresh functionality. WhenEver user launch the swipeToRefresh Functionality
data is fetched from the remote api and get the notification about the retrieved dog
The jetpack Components used in this app are:
1:) Navigation 
2:) MVVM and LiveData
3:) Retrofit and RxJava to retrieve data from endPoint
4:) Glide and Ktx Extensions
5:) Room for caching
6:) Data Binding
7:) Notifications
11:) Prefereces
12:) Sharing
13:) MultiDex
