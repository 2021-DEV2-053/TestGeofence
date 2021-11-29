package com.geofenceapp.ui.maps

import android.app.ActivityManager
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.geofenceapp.R
import com.geofenceapp.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.slider.Slider
import android.content.res.Resources
import android.util.Log
import androidx.fragment.app.viewModels
import com.geofenceapp.core.GeofenceService
import com.geofenceapp.data.model.Geofence
import com.geofenceapp.util.Utils.Companion.isServiceRunning
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

    private var radiusCircle: Circle? = null
    private var radiusMarker: Marker? = null
    private var radius: Double = 0.0

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

        binding.buttonService.setOnClickListener {
            if(!isServiceRunning(requireActivity())){
                if(isValid()){
                    val geofence = Geofence(radius,
                        radiusMarker!!.position.latitude,
                        radiusMarker!!.position.longitude
                    )
                    GeofenceService.start(requireContext(), geofence)
                    binding.buttonService.setText(R.string.Stop)
                    binding.textViewMessage.setText(R.string.GeofenceApp_is_running)
                }
            }else{
                GeofenceService.stop(requireContext())
                binding.buttonService.setText(R.string.Start)
                binding.textViewMessage.text = ""
            }
        }

        binding.SliderRadius.addOnChangeListener { slider, value, fromUser ->
            radius = value.toDouble()
            binding.textViewRadius.text = radius.toString()
            if(radiusCircle != null){
                radiusCircle!!.radius = radius
            }
        }

        if(isServiceRunning(requireActivity())){
            binding.buttonService.setText(R.string.Stop)
            binding.textViewMessage.setText(R.string.GeofenceApp_is_running)
        }
    }

    private fun isValid(): Boolean {
        var isValid = true
        if(radiusMarker == null){
            binding.textViewMessage.append(getString(R.string.Select_a_position))
            isValid = false
        }
        if(radius == 0.0){
            binding.textViewMessage.append("\n")
            binding.textViewMessage.append(getString(R.string.Select_a_radius))
            isValid = false
        }

        return isValid
    }

    override fun onMapReady(gm: GoogleMap) {
        try {
            googleMap = gm
            googleMap!!.setOnMapClickListener(this)
            googleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_light_json
                )
            )
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