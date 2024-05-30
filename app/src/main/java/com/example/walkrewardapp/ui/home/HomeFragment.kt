package com.example.walkrewardapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.walkrewardapp.R
import com.example.walkrewardapp.databinding.FragmentHomeBinding
import com.example.walkrewardapp.ui.ViewModelFactory
import com.example.walkrewardapp.ui.profile.ProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class HomeFragment : Fragment(), OnMapReadyCallback, SensorEventListener {

    // Binding variable to access UI elements
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var profileViewModel: ProfileViewModel

    // Sensor-related variables
    private lateinit var sensorManager: SensorManager
    private var rotationMatrix = FloatArray(9)
    private var orientation = FloatArray(3)
    private var azimuth: Float = 0f
    private var currentAzimuth: Float = 0f

    // Location-related variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private var polyline: Polyline? = null

    // Step counter sensor
    private var stepCounterSensor: Sensor? = null
    private var totalStepCount: Int = 0 // Total steps
    private var initialStepCount: Int = 0 // Initial step count to track the steps taken during the walk

    // Handler for updating time
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (homeViewModel.isWalking.value == true) {
                homeViewModel.updateTime()
                handler.postDelayed(this, 1000) // Update time every second
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModels updated
        profileViewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        val factory = ViewModelFactory(profileViewModel)
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()

        // Initialize sensor manager and sensors
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)

        // Initialize step counter sensor
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        return root
    }

    private fun setupUI() {
        // UI components
        val startStopButton: ImageButton = binding.startStopButton
        val infoOverlay: LinearLayout = binding.infoOverlay
        val confirmationDialog: RelativeLayout = binding.confirmationDialog
        val yesButton: Button = confirmationDialog.findViewById(R.id.yes_button)
        val noButton: Button = confirmationDialog.findViewById(R.id.no_button)
        val confirmationMessage: TextView =
            confirmationDialog.findViewById(R.id.confirmation_message)

        val infoTime: TextView = binding.infoTime
        val infoDistance: TextView = binding.infoDistance
        val infoSpeed: TextView = binding.infoSpeed
        val infoStep: TextView = binding.infoStep

        // Observe walking state from ViewModel and update UI accordingly
        homeViewModel.isWalking.observe(viewLifecycleOwner, Observer { isWalking ->
            if (isWalking) {
                // Show info overlay when walking
                infoOverlay.visibility = View.VISIBLE

                // Move the start/stop button above the info overlay
                val params = startStopButton.layoutParams as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.ABOVE, R.id.infoOverlay)
                startStopButton.layoutParams = params

                handler.post(updateRunnable) // Start updating time
                // initialStepCount = totalStepCount // Initialize the initial step count with the total step count when starting
            } else {
                // Hide info overlay when not walking
                infoOverlay.visibility = View.GONE

                // Move the start/stop button back to the bottom
                val params = startStopButton.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.removeRule(RelativeLayout.ABOVE)
                startStopButton.layoutParams = params

                handler.removeCallbacks(updateRunnable) // Stop updating time
                polyline?.remove() // Clear the polyline when stopping
            }
        })

        // Observe time from ViewModel and update UI
        homeViewModel.time.observe(viewLifecycleOwner, Observer { time ->
            infoTime.text = "Time: $time"
        })

        // Observe distance from ViewModel and update UI
        homeViewModel.distance.observe(viewLifecycleOwner, Observer { distance ->
            infoDistance.text = "Distance: ${distance}m"
        })

        // Observe speed from ViewModel and update UI
        homeViewModel.speed.observe(viewLifecycleOwner, Observer { speed ->
            infoSpeed.text = "Speed: ${speed}m/min"
        })

        // Observe steps during walk from ViewModel and update UI
        homeViewModel.stepsDuringWalk.observe(viewLifecycleOwner, Observer { stepsDuringWalk ->
            infoStep.text = "Steps: $stepsDuringWalk"
        })

        // Observe total steps from ViewModel and update UI
        homeViewModel.totalSteps.observe(viewLifecycleOwner, Observer { totalSteps ->
            // Do something if needed
        })

        // Observe path from ViewModel and update map
        homeViewModel.path.observe(viewLifecycleOwner, Observer { path ->
            if (homeViewModel.isWalking.value == true) {
                drawPolyline(path)
            }
        })

        // Start/Stop button click listener
        startStopButton.setOnClickListener {
            if (homeViewModel.isWalking.value == true) {
                updateConfirmationDialog()
                confirmationDialog.visibility = View.VISIBLE
                infoOverlay.visibility = View.GONE
            } else {
                homeViewModel.setWalkingState(true)
            }
        }

        // Yes button click listener in confirmation dialog
        yesButton.setOnClickListener {
            val stepsDuringWalk = homeViewModel.stepsDuringWalk.value ?: 0
            val timeString = homeViewModel.time.value ?: "00:00" // Get the time string from LiveData
            val distance = homeViewModel.distance.value ?: 0f

            // Parse the time string to get the total minutes
            val timeParts = timeString.split(":")
            val minutes = timeParts[0].toInt()
            val seconds = timeParts[1].toInt()
            val totalMinutes = minutes + seconds / 60.0

            // Update profile data with new values
            val totalDistance = profileViewModel.totalDistance.value ?: 0f
            profileViewModel.setTotalDistance(totalDistance + distance)

            val rewardPoints = totalMinutes * 1 + distance * 1 + stepsDuringWalk * 1
            val currentRewardPoints = profileViewModel.rewardPoints.value ?: 0
            profileViewModel.setRewardPoints((currentRewardPoints + rewardPoints).toInt())

            confirmationMessage.text =
                "You have accumulated $stepsDuringWalk steps and spent $timeString walking. Reward points: $rewardPoints"

            // Hide confirmation dialog
            confirmationDialog.visibility = View.GONE

            // Reset walking state
            homeViewModel.setWalkingState(false)
        }

        // No button click listener in confirmation dialog
        noButton.setOnClickListener {
            confirmationDialog.visibility = View.GONE
            infoOverlay.visibility = View.VISIBLE
        }
    }

    private fun updateConfirmationDialog() {
        val stepsDuringWalk = homeViewModel.stepsDuringWalk.value ?: 0
        val timeString = homeViewModel.time.value ?: "00:00" // Get the time string from LiveData
        val distance = homeViewModel.distance.value ?: 0f

        // Parse the time string to get the total minutes
        val timeParts = timeString.split(":")
        val minutes = timeParts[0].toInt()
        val seconds = timeParts[1].toInt()
        val totalMinutes = minutes + seconds / 60.0

        // Calculate reward points without updating profile data
        val rewardPoints = totalMinutes * 1 + distance * 1 + stepsDuringWalk * 1
        val confirmationMessage: TextView = binding.confirmationDialog.findViewById(R.id.confirmation_message)
        confirmationMessage.text =
            "You have accumulated $stepsDuringWalk steps and spent $timeString walking. Reward points: $rewardPoints"
    }

    // Draw polyline on the map to show the path
    private fun drawPolyline(path: List<LatLng>) {
        polyline?.remove()
        if (path.isNotEmpty()) {
            polyline = map.addPolyline(
                PolylineOptions()
                    .addAll(path)
                    .width(5f)
                    .color(R.color.purple_500)
            )
        }
    }

    // Google Maps callback when the map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    // Enable user location on the map
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                map.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                homeViewModel.updateLocation(it)
            }
        }
    }

    // Handle location permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        sensorManager.unregisterListener(this)
    }

    // Sensor event listener for rotation vector and step counter
    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            // Update compass when rotation vector sensor event is received
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                val rotateAnimation = android.view.animation.RotateAnimation(
                    -currentAzimuth,
                    -azimuth,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
                )
                rotateAnimation.duration = 500
                rotateAnimation.fillAfter = true

                binding.compassImageView.startAnimation(rotateAnimation)
                currentAzimuth = azimuth
            }
            // Update step count when step counter sensor event is received
            Sensor.TYPE_STEP_COUNTER -> {
                 val currentStepCount = event.values[0].toInt()
                homeViewModel.updateSteps(currentStepCount)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No implementation needed
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
