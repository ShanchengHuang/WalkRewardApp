package com.example.walkrewardapp.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.walkrewardapp.ui.profile.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.round

class HomeViewModel(private val profileViewModel: ProfileViewModel) : ViewModel() {


    val rewardPoints: LiveData<Int> = profileViewModel.rewardPoints

    private val _totalSteps = MutableLiveData<Int>()
    val totalSteps: LiveData<Int> = _totalSteps

    private val _stepsDuringWalk = MutableLiveData<Int>().apply { value = 0 }
    val stepsDuringWalk: LiveData<Int> = _stepsDuringWalk


    // LiveData for walking state
    private val _isWalking = MutableLiveData<Boolean>().apply {
        value = false // Initial state
    }
    val isWalking: LiveData<Boolean> = _isWalking

    // LiveData for elapsed time
    private val _time = MutableLiveData<String>().apply {
        value = "00:00" // Initial time
    }
    val time: LiveData<String> = _time

    // LiveData for distance traveled
    private val _distance = MutableLiveData<Float>().apply {
        value = 0f // Initial distance
    }
    val distance: LiveData<Float> = _distance

    // LiveData for step count (OLD)
//    private val _steps = MutableLiveData<Int>().apply {
//        value = 0 // Initial step count
//    }
//    val steps: LiveData<Int> = _steps

    // LiveData for speed
    private val _speed = MutableLiveData<Float>().apply {
        value = 0f // Initial speed
    }
    val speed: LiveData<Float> = _speed

    // LiveData for path (list of LatLng points)
    private val _path = MutableLiveData<List<LatLng>>().apply {
        value = emptyList() // Initial path
    }
    val path: LiveData<List<LatLng>> = _path

    private var startTime: Long = 0 // Variable to store the start time of walking
    private var lastLocation: Location? = null // Variable to store the last location

    // Function to set the walking state

    private var initialStepCount = 0
    fun setWalkingState(isWalking: Boolean) {
        _isWalking.value = isWalking
        if (isWalking) {
            startTime = System.currentTimeMillis() // Record the start time
            lastLocation = null // Reset last location
            _path.value = emptyList() // Clear the path when starting

            initialStepCount = _totalSteps.value ?: 0
            _stepsDuringWalk.value = 0
        }
    }

    // Function to update the step count (OLD)
//    fun updateSteps(stepCount: Int) {
//        _steps.value = stepCount
//    }

    fun updateSteps(totalSteps: Int) {
        _totalSteps.value = totalSteps
        _stepsDuringWalk.value = totalSteps - initialStepCount
    }

    // Function to update the location and calculate distance
    fun updateLocation(location: Location) {
        lastLocation?.let {
            val distanceIncrement = it.distanceTo(location) // Calculate the distance increment
            _distance.value =
                (_distance.value ?: 0f) + distanceIncrement // Update the total distance
        }
        lastLocation = location // Update the last location
        val currentPath = _path.value?.toMutableList() ?: mutableListOf()
        currentPath.add(
            LatLng(
                location.latitude,
                location.longitude
            )
        ) // Add new location to the path
        _path.value = currentPath
    }

    // Function to update the elapsed time and calculate speed
    fun updateTime() {
        val elapsedMillis = System.currentTimeMillis() - startTime // Calculate elapsed time
        val elapsedSeconds = (elapsedMillis / 1000).toInt()
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        _time.value = String.format("%02d:%02d", minutes, seconds) // Format time as MM:SS

        val currentDistance = _distance.value ?: 0f
        val elapsedMinutes = if (minutes > 0) minutes else 1 // To avoid division by zero
        _speed.value =
            round((currentDistance / elapsedMinutes) * 100) / 100 // Speed in meters per minute, rounded to 2 decimal places
    }
}
