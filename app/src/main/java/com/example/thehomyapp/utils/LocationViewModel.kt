package com.example.thehomyapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val _location = MutableLiveData<LocationData?>()
    val location: LiveData<LocationData?> = _location

    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }
}