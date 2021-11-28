package com.geofenceapp.ui.maps

import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.geofenceapp.R
import com.geofenceapp.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.slider.Slider
import android.content.res.Resources
import android.util.Log
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnCameraMoveListener
{

    private var _binding: FragmentMapsBinding? = null
    private val viewModel: MapsViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var zoomLevel: Float = 13f

    private var radius: Double = 0.0
    private var radiusCircle: Circle? = null
    private var radiusMarker: Marker? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_MapsFragment_to_SettingsFragment)
        }

        binding.SliderRadius.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
            }
        })

        binding.SliderRadius.addOnChangeListener { slider, value, fromUser ->
            radius = value.toDouble()
            binding.textViewRadius.text = radius.toString()
            if(radiusCircle != null){
                radiusCircle!!.radius = radius
            }
        }
    }

    override fun onMapReady(gm: GoogleMap) {
        try {
            googleMap = gm
            googleMap!!.setOnMapClickListener(this)
            val success = googleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_light_json
                )
            )
            if (!success) {
                Log.e("MapsFragment", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsFragment", "Can't find style. Error: ", e)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }


    override fun onMapClick(point: LatLng) {
        drawRadiusMarker(point)
        drawRadiusCircle(point)

        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel))
    }

    override fun onCameraMove() {
        val cameraPosition = googleMap!!.cameraPosition
        zoomLevel = cameraPosition.zoom
    }
    private fun drawRadiusMarker(point: LatLng) {
        if(radiusMarker != null){
            radiusMarker!!.remove()
        }
        radiusMarker = googleMap!!.addMarker(
            MarkerOptions()
                .position(point)
                .title("Marker")
                .flat(true)
        )
    }

    private fun drawRadiusCircle(point: LatLng) {
        if(radiusCircle != null){
            radiusCircle!!.remove()
        }
        radiusCircle = googleMap!!.addCircle(
            CircleOptions()
                .center(point)
                .radius(radius)
                .strokeColor(Color.RED)
                .strokeWidth(5f)
                .fillColor(0x220000FF)
        )
    }
}