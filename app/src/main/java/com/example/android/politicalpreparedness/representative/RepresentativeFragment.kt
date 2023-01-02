package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.election.RepresentativeViewModelFactory
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.R
import java.util.Locale
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.snackbar.Snackbar

class RepresentativesFragment : Fragment() {
    private val viewModel: RepresentativeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        // ViewModelProvider(this).get(MainViewModel::class.java)
        ViewModelProvider(this, RepresentativeViewModelFactory(activity.application)).get(
            RepresentativeViewModel::class.java
        )
    }

    companion object {
        //TODO: Add Constant for Location request
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val TAG = "RepresentativesFragment"
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    }

    //TODO: Declare ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO: Establish bindings
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        val adapter = RepresentativeListAdapter()
        binding.recyclerRepresentative.adapter = adapter

        viewModel.representatives.observe(viewLifecycleOwner, { representatives ->
            adapter.submitList(representatives)
        })
        binding.buttonSearch.setOnClickListener({
            viewModel.getRepresentatives()
        })

        binding.buttonLocation.setOnClickListener({
            checkLocationPermissions()
        })

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        return binding.root

    }

    private fun checkLocationPermissions() {
          if(foregroundPermissionApproved()) {
              checkDeviceLocationSettingsAndFetchLocation()
          }
          else {
              requestForegroundLocationPermissions()
          }
    }

    @TargetApi(29)
    private fun requestForegroundLocationPermissions() {
         val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(
            permissionsArray,
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )

    }

    private fun checkDeviceLocationSettingsAndFetchLocation(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE, null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(
                        TAG,
                        "Error getting location settings resolution: " + sendEx.message
                    )
                }
            } else {
//                Snackbar.make(
//                    binding.root,
//                    R.string.location_required_error, Snackbar.LENGTH_LONG
//                ).show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                getLocation()
            }
        }
    }


    @TargetApi(29)
    private fun foregroundPermissionApproved(): Boolean {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (foregroundPermissionApproved()) {
             checkDeviceLocationSettingsAndFetchLocation(true)
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                Snackbar.make(
//                    binding.root,
//                    R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
//                )
//                    .setAction(R.string.settings) {
//                        requestForegroundLocationPermissions()
//                    }.show()

            } else {
                displayAlertDialog()
            }
        }
        //TODO: Handle location permission result to get location on permission granted
    }

    fun displayAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())

        alertDialogBuilder.setMessage(com.example.android.politicalpreparedness.R.string.manual_enable_while_use_permission)
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { arg0, arg1 -> requireActivity().finish() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

//    private fun checkLocationPermissions(): Boolean {
//        return if (isPermissionGranted()) {
//            true
//        } else {
//            //TODO: Request Location permissions
//            false
//        }
//    }

//    private fun isPermissionGranted() : Boolean {
//        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
//    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (foregroundPermissionApproved()) {
            initLocationRequestAndCallback()
        }
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    @SuppressLint("MissingPermission")
    private fun initLocationRequestAndCallback() {

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult.let {

                    val address = geoCodeLocation(it.lastLocation)
                    viewModel.getLocation(address)

                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        Looper.myLooper()?.let {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                it
            )
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())

        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }


    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}