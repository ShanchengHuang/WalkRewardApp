package com.example.walkrewardapp.ui.reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RewardViewModel : ViewModel() {

    private val _rewardPoints = MutableLiveData<Int>().apply {
        value = 50 // Initialize with 50 points
    }
    val rewardPoints: LiveData<Int> = _rewardPoints

    fun updateRewardPoints(points: Int) {
        _rewardPoints.value = points
    }
}
