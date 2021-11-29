package com.geofenceapp.ui.maps

import android.content.Context
import android.content.SharedPreferences
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

    private val binding get() = _binding!!

    //Maps view
    private var googleMap: GoogleMap? = null
    private var zoomLevel: Float = 13f

    //attributes for Geofence
    private var radiusCircle: Circle? = null
    private var radiusMarker: Marker? = null
    private var radius: Double = 0.0

    //Location background service
    private var isServiceRunning: Boolean = false
    //get sharedpref to save the geofence
    private var sharedPref: SharedPreferences? = null

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

        isServiceRunning = isServiceRunning(requireActivity())

        binding.buttonService.setOnClickListener {
            if(!isServiceRunning(requireActivity())){
                if(isValid()){
                    startGeofencing()
                }
            }else{
                stopGeofencing()
            }
        }
        // change the radius with a Slider
        binding.SliderRadius.addOnChangeListener { slider, value, fromUser ->
            radius = value.toDouble()
            binding.textViewRadius.text = String.format("%.2f", radius) + " m"
            if(radiusCircle != null){
                radiusCircle!!.radius = radius
            }
        }

        // disable the slider when the service background is running
        binding.SliderRadius.isEnabled = !isServiceRunning

        // check if the background service is running to display a message
        if(isServiceRunning){
            binding.buttonService.setText(R.string.Stop)
            binding.textViewMessage.setText(R.string.GeofenceApp_is_running)
        }
    }
    /**
     * get the last data radius, latitude, longitude from shared preferences
     * draw marker and circle on google maps.
     */
    private fun fetchDataFromSharedPref() {
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        radius = sharedPref!!.getString(getString(R.string.prefs_Radius_key), "0.0")!!.toDouble()

        if(radius != 0.0){
            binding.SliderRadius.value = radius.toFloat()
            val latitude = sharedPref!!.getString(getString(R.string.prefs_Latitude_key), "0.0")!!.toDouble()
            val longitude = sharedPref!!.getString(getString(R.string.prefs_Longitude_key), "0.0")!!.toDouble()
            val point = LatLng(
                latitude,
                longitude
            )
            drawRadiusMarker(point)
            drawRadiusCircle(point)

            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel))
        }
    }
    /**
     * check if the user pick a location and provide a radius
     */
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
    /**
     * start the GeofenceService
     * save the geofence into sharefpref, and update the UI.
     */
    private fun startGeofencing() {
        val geofence = Geofence(radius,
            radiusMarker!!.position.latitude,
            radiusMarker!!.position.longitude
        )
        with (sharedPref!!.edit()) {
            putString(getString(R.string.prefs_Radius_key), radius.toString())
            apply()
        }
        GeofenceService.start(requireContext(), geofence)
        binding.buttonService.setText(R.string.Stop)
        binding.textViewMessage.setText(R.string.GeofenceApp_is_running)
        binding.SliderRadius.isEnabled = false
        isServiceRunning = true
    }
    /**
     * stop the GeofenceService
     * update the UI.
     */
    private fun stopGeofencing() {
        GeofenceService.stop(requireContext())
        binding.buttonService.setText(R.string.Start)
        binding.textViewMessage.text = ""
        binding.SliderRadius.isEnabled = true
        isServiceRunning = false
    }

    override fun onMapReady(gm: GoogleMap) {
        try {
            googleMap = gm
            googleMap!!.setOnMapClickListener(this)
            /**
             * change the style of the map
             */
            googleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_light_json
                )
            )
            fetchDataFromSharedPref()
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsFragment", "Can't find style. Error: ", e)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }
    /**
     * Draw a marker and circle only if the GeofenceService is not running,
     * save the latitude and longitude of radius into sharedpreference
     */
    override fun onMapClick(point: LatLng) {
        if(!isServiceRunning){
            drawRadiusMarker(point)
            drawRadiusCircle(point)
            with (sharedPref!!.edit()) {
                putString(getString(R.string.prefs_Latitude_key), point.latitude.toString())
                putString(getString(R.string.prefs_Longitude_key), point.longitude.toString())
                apply()
            }
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel))
        }
    }
    /**
     * Save the zoom level, it will keep the same level when move the camera
     */
    override fun onCameraMove() {
        val cameraPosition = googleMap!!.cameraPosition
        zoomLevel = cameraPosition.zoom
    }
    /**
     * Draw a marker on Google map, it will be the center of the radius
     */
    private fun drawRadiusMarker(point: LatLng) {
        if(radiusMarker != null){
            radiusMarker!!.remove()
        }
        radiusMarker = googleMap!!.addMarker(
            MarkerOptions()
                .position(point)
                .title("Radius")
                .flat(true)
        )
    }
    /**
     * Draw a circle, it will represent the radius on the map
     */
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