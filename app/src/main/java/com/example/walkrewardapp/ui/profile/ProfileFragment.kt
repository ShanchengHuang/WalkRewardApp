package com.example.walkrewardapp.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.walkrewardapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()

        return root
    }

    private fun setupUI() {
        val profileImageView: ImageView = binding.profileImage
        val userNameEditText: EditText = binding.tvUserName
        val applyButton: Button = binding.btnApplyUsername

        profileViewModel.userName.observe(viewLifecycleOwner, Observer { name ->
            userNameEditText.setText(name)
        })

        profileViewModel.rewardPoints.observe(viewLifecycleOwner, Observer { points ->
            binding.tvRwPoints.text = "RW Point: $points"
        })

        profileViewModel.totalDistance.observe(viewLifecycleOwner, Observer { distance ->
            binding.tvTotalDistance.text = "Total distance: $distance km"
        })

        userNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                applyButton.visibility = View.VISIBLE
            }
        }

        applyButton.setOnClickListener {
            profileViewModel.setUserName(userNameEditText.text.toString())
            applyButton.visibility = View.GONE
            userNameEditText.clearFocus()
            hideKeyboard(userNameEditText)
        }

        userNameEditText.setOnClickListener {
            applyButton.visibility = View.VISIBLE
        }

        profileImageView.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            binding.profileImage.setImageURI(imageUri)
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
