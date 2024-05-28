package com.example.walkrewardapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.walkrewardapp.R
import com.example.walkrewardapp.databinding.FragmentHomeBinding
import com.example.walkrewardapp.ui.profile.ProfileViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var isWalking = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        profileViewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()

        return root
    }

    private fun setupUI() {
        val startStopButton: ImageButton = binding.startStopButton
        val infoOverlay: LinearLayout = binding.infoOverlay
        val confirmationDialog: RelativeLayout = binding.confirmationDialog
        val yesButton: Button = confirmationDialog.findViewById(R.id.yes_button)
        val noButton: Button = confirmationDialog.findViewById(R.id.no_button)
        val confirmationMessage: TextView = confirmationDialog.findViewById(R.id.confirmation_message)

        // Set the initial visibility
        infoOverlay.visibility = View.GONE

        startStopButton.setOnClickListener {
            if (!isWalking) {
                // Start walking
                isWalking = true
                infoOverlay.visibility = View.VISIBLE
                val params = startStopButton.layoutParams as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.ABOVE, R.id.infoOverlay)
                startStopButton.layoutParams = params
            } else {
                // Show confirmation dialog
                confirmationDialog.visibility = View.VISIBLE
                infoOverlay.visibility = View.GONE
            }
        }

        yesButton.setOnClickListener {
            // Handle confirmation and update profile data
            val steps = 1000 // Example step count
            val time = 60 // Example time in minutes
            val distance = 1000f // Example distance in meters

            // Update total distance in profile
            val totalDistance = profileViewModel.totalDistance.value ?: 0f
            profileViewModel.setTotalDistance(totalDistance + distance)

            // Calculate reward points
            val rewardPoints = time * 1 + distance * 1 + steps * 1
            val currentRewardPoints = profileViewModel.rewardPoints.value ?: 0
            profileViewModel.setRewardPoints((currentRewardPoints + rewardPoints).toInt())

            // Update confirmation message
            confirmationMessage.text = "You have accumulated $steps Steps and spent $time minutes walking"

            // Hide confirmation dialog
            confirmationDialog.visibility = View.GONE
            isWalking = false
        }

        noButton.setOnClickListener {
            // Dismiss the confirmation dialog and resume walking
            confirmationDialog.visibility = View.GONE
            infoOverlay.visibility = View.VISIBLE
            isWalking = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
