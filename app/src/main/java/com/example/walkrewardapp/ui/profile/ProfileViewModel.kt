package com.example.walkrewardapp.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _userName = MutableLiveData<String>().apply {
        value = "type in your username" // Default value
    }
    val userName: LiveData<String> = _userName

    private val _rewardPoints = MutableLiveData<Int>().apply {
        value = 500 // New default value
    }
    val rewardPoints: LiveData<Int> = _rewardPoints

    private val _totalDistance = MutableLiveData<Float>().apply {
        value = 0f // Default value
    }
    val totalDistance: LiveData<Float> = _totalDistance

    // LiveData for profile image URI
    private val _profileImageUri = MutableLiveData<Uri>()
    val profileImageUri: LiveData<Uri> = _profileImageUri

    // Functions to update the profile data
    fun setUserName(name: String) {
        _userName.value = name
    }

    fun setRewardPoints(points: Int) {
        _rewardPoints.value = points
    }

    fun setTotalDistance(distance: Float) {
        _totalDistance.value = distance
    }

    // Function to update the profile image URI
    fun setProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }
}
