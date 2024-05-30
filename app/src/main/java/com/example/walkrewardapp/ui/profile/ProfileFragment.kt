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

    // Binding variable to access UI elements
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    // ViewModel instance for profile data
    private lateinit var profileViewModel: ProfileViewModel
    // Request code for image picker
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        profileViewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        // Inflate the layout using view binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up UI components
        setupUI()

        return root
    }

    private fun setupUI() {
        // UI components
        val profileImageView: ImageView = binding.profileImage
        val userNameEditText: EditText = binding.tvUserName
        val applyButton: Button = binding.btnApplyUsername

        // Observe userName LiveData from ViewModel and update EditText
        profileViewModel.userName.observe(viewLifecycleOwner, Observer { name ->
            userNameEditText.setText(name)
        })

        // Observe rewardPoints LiveData from ViewModel and update TextView
        profileViewModel.rewardPoints.observe(viewLifecycleOwner, Observer { points ->
            binding.tvRwPoints.text = "RW Point: $points"
        })

        // Observe totalDistance LiveData from ViewModel and update TextView
        profileViewModel.totalDistance.observe(viewLifecycleOwner, Observer { distance ->
            binding.tvTotalDistance.text = "Total distance: $distance km"
        })

        // Observe profileImageUri LiveData from ViewModel and update ImageView
        profileViewModel.profileImageUri.observe(viewLifecycleOwner, Observer { uri ->
            uri?.let {
                profileImageView.setImageURI(it)
            }
        })

        // Show apply button when EditText gains focus
        userNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                applyButton.visibility = View.VISIBLE
            }
        }

        // Apply button click listener to update user name and hide button
        applyButton.setOnClickListener {
            profileViewModel.setUserName(userNameEditText.text.toString())
            applyButton.visibility = View.GONE
            userNameEditText.clearFocus()
            hideKeyboard(userNameEditText)
        }

        // Show apply button when EditText is clicked
        userNameEditText.setOnClickListener {
            applyButton.visibility = View.VISIBLE
        }

        // ImageView click listener to open image picker
        profileImageView.setOnClickListener {
            openImagePicker()
        }
    }

    // Function to open the image picker
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle result from image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            binding.profileImage.setImageURI(imageUri)
            profileViewModel.setProfileImageUri(imageUri)
        }
    }

    // Function to hide the keyboard
    private fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding when view is destroyed
    }
}
