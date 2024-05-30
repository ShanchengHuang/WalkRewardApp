package com.example.walkrewardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.walkrewardapp.ui.profile.ProfileViewModel
import com.example.walkrewardapp.ui.home.HomeViewModel
import com.example.walkrewardapp.ui.reward.RewardViewModel

// Assisted by ChatGPT and CodeLabs, "Android Kotlin Fundamentals: Lifecycles and Advanced Navigation"
// ViewModelFactory class to create instances of HomeViewModel and RewardViewModel
class ViewModelFactory(private val profileViewModel: ProfileViewModel) : ViewModelProvider.Factory {
    // Create ViewModel instances
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(profileViewModel) as T
            modelClass.isAssignableFrom(RewardViewModel::class.java) -> RewardViewModel(profileViewModel) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
