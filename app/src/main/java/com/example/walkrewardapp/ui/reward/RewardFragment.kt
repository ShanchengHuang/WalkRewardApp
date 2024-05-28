package com.example.walkrewardapp.ui.reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.walkrewardapp.R
import com.example.walkrewardapp.databinding.FragmentRewardBinding

class RewardFragment : Fragment() {

    private var _binding: FragmentRewardBinding? = null
    private val binding get() = _binding!!
    private lateinit var rewardViewModel: RewardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rewardViewModel = ViewModelProvider(this).get(RewardViewModel::class.java)
        _binding = FragmentRewardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()

        return root
    }

    private fun setupUI() {
        val rewardPointsTextView: TextView = binding.tvRwPoints
        val confirmationDialog: RelativeLayout = binding.confirmationDialog
        val confirmationSuccessDialog: RelativeLayout = binding.confirmationSuccessDialog
        val couponContainer: LinearLayout = binding.llCoupons

        rewardViewModel.rewardPoints.observe(viewLifecycleOwner, Observer { points ->
            rewardPointsTextView.text = "Reward Point: $points"
        })

        fun showConfirmationDialog(couponPoints: Int) {
            confirmationDialog.visibility = View.VISIBLE
            val currentPoints = rewardViewModel.rewardPoints.value ?: 0
            val confirmationMessage = if (currentPoints >= couponPoints) {
                "Are you sure you want to redeem it?\nYou have $currentPoints points. After the redemption is completed, you will have ${currentPoints - couponPoints} points left."
            } else {
                "You don't have enough points."
            }
            confirmationDialog.findViewById<TextView>(R.id.confirmation_message).text = confirmationMessage
        }

        fun showSuccessDialog(isSuccess: Boolean, couponCode: String = "") {
            confirmationDialog.visibility = View.GONE
            confirmationSuccessDialog.visibility = View.VISIBLE
            val successMessage = if (isSuccess) {
                "You have successfully redeemed!\nCoupon code: $couponCode"
            } else {
                "You don't have enough points to redeem this coupon."
            }
            confirmationSuccessDialog.findViewById<TextView>(R.id.confirmation_success_message).text = successMessage
        }

        fun hideAllDialogs() {
            confirmationDialog.visibility = View.GONE
            confirmationSuccessDialog.visibility = View.GONE
        }

        // Setup click listeners for apply buttons
        val nikeCouponButton = binding.root.findViewById<Button>(R.id.apply_button)
        nikeCouponButton.setOnClickListener {
            showConfirmationDialog(500)
            confirmationDialog.findViewById<Button>(R.id.confirm_button).setOnClickListener {
                val currentPoints = rewardViewModel.rewardPoints.value ?: 0
                if (currentPoints >= 500) {
                    rewardViewModel.updateRewardPoints(currentPoints - 500)
                    showSuccessDialog(true, "XXXX-XXXX-XXXX") // Assuming coupon code is static
                } else {
                    showSuccessDialog(false)
                }
            }
            confirmationDialog.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                hideAllDialogs()
            }
        }

        val adidasCouponButton = binding.root.findViewById<Button>(R.id.apply_button2)
        adidasCouponButton.setOnClickListener {
            showConfirmationDialog(600)
            confirmationDialog.findViewById<Button>(R.id.confirm_button).setOnClickListener {
                val currentPoints = rewardViewModel.rewardPoints.value ?: 0
                if (currentPoints >= 600) {
                    rewardViewModel.updateRewardPoints(currentPoints - 600)
                    showSuccessDialog(true, "XXXX-XXXX-XXXX") // Assuming coupon code is static
                } else {
                    showSuccessDialog(false)
                }
            }
            confirmationDialog.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                hideAllDialogs()
            }
        }

        confirmationSuccessDialog.findViewById<Button>(R.id.close_button).setOnClickListener {
            hideAllDialogs()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
