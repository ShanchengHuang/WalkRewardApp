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
import com.example.walkrewardapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



//        // Find your views
//        val startStopButton: ImageButton = binding.startStopButton
//        val infoOverlay: LinearLayout = binding.infoOverlay
//
//        // Set a listener or condition to check if the infoOverlay is gone
//        infoOverlay.visibility = View.GONE
//
//        // Update the layout parameters of startStopButton
//        val params = startStopButton.layoutParams as RelativeLayout.LayoutParams
//        params.removeRule(RelativeLayout.ABOVE)
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
//        startStopButton.layoutParams = params

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
