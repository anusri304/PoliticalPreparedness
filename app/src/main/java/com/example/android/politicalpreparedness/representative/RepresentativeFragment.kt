package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.RepresentativeViewModelFactory
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.util.Util
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

class RepresentativesFragment : Fragment() {
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
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
        // Constant for Location request
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val TAG = "RepresentativesFragment"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        // Define and assign Representative adapter

        val adapter = RepresentativeListAdapter()

        // Populate Representative adapter
        binding.recyclerRepresentative.adapter = adapter

        viewModel.representatives.observe(viewLifecycleOwner) { representatives ->
            adapter.submitList(representatives)
            if (representatives != null) {
                if (representatives.isNotEmpty()) {
                    try {
                        //https://knowledge.udacity.com/questions/815081
                        val motionStat = savedInstanceState?.getInt("motion_stat")
                        if (motionStat != null) {
                            binding.motionLayout.transitionToState(motionStat)
                        }
                    } catch (e: Exception) {
                        Timber.e(getString(R.string.error_restore))
                    }
                }
            }
        }


        // Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            if(Util.isNetworkAvailable(requireContext())) {
                viewModel.getRepresentatives()
            }
            else{
                Snackbar.make(requireView(), R.string.err_no_connection,Snackbar.LENGTH_LONG).show()
            }

        }

        binding.buttonLocation.setOnClickListener {
            if(Util.isNetworkAvailable(requireContext())) {
                checkLocationPermissions()
            }
            else{
                Snackbar.make(requireView(), R.string.err_no_connection,Snackbar.LENGTH_LONG).show()
            }
        }

        savedInstanceState?.getParcelable<Address>("address")?.let{
            //https://knowledge.udacity.com/questions/815081
            viewModel.getRepresentatives()
        }

        viewModel.status.observe(viewLifecycleOwner) { apiStatus ->
            when (apiStatus) {
                ApiStatus.ERROR -> {
                    Snackbar.make(requireView(), R.string.error_representatives, Snackbar.LENGTH_LONG)
                        .show()
                }
                else -> {}
            }
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.showSnackBar.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        }
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
                        REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.e("Error getting location settings resolution: " + sendEx.message)

                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_LONG
                ).show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                getLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON && resultCode!=-1) {
            checkDeviceLocationSettingsAndFetchLocation(false)
        }
    }
    @TargetApi(29)
    private fun foregroundPermissionApproved(): Boolean {
        //Check if permission is already granted and return (true = granted, false = denied/other)
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
            // Request Location permissions
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(
                    binding.root,
               R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.settings) {
                        // Handle location permission result to get location on permission granted
                        requestForegroundLocationPermissions()
                    }.show()

            } else {
                displayAlertDialog()
            }
        }

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

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (foregroundPermissionApproved()) {
            initLocationRequestAndCallback()
        }
    }

    //Get location from LocationServices
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
    // The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("address",binding.viewModel?._address?.value)
        outState.putInt("motion_stat",binding.motionLayout.currentState)
    }

}