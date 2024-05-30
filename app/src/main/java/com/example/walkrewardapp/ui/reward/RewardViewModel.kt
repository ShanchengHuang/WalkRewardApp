package com.example.walkrewardapp.ui.reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.walkrewardapp.ui.profile.ProfileViewModel

class RewardViewModel(private val profileViewModel: ProfileViewModel) : ViewModel() {

    // Reward points are fetched directly from ProfileViewModel
    val rewardPoints: LiveData<Int> = profileViewModel.rewardPoints

    // Function to update reward points in ProfileViewModel
    fun updateRewardPoints(points: Int) {
        profileViewModel.setRewardPoints(points)
    }
}
