package com.aliziwa.openweatherjpmc.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aliziwa.domain.AsyncOperation
import com.aliziwa.domain.Completed
import com.aliziwa.domain.Failure
import com.aliziwa.domain.Loading
import com.aliziwa.domain.model.Weather
import com.aliziwa.openweatherjpmc.R
import com.aliziwa.openweatherjpmc.databinding.FragmentHomeBinding
import com.aliziwa.openweatherjpmc.ui.MainViewModel
import com.aliziwa.openweatherjpmc.ui.base.BaseFragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val mainViewModel: MainViewModel by viewModels()

    //Permissions callback
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onLocationPermissionsUpdate()
            } else {
                /**
                 * Show a simple snack bar on denial! :(
                 */
                binding.root.showSnackBar(getString(R.string.permissions_denial_string))
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        /**
         * Request for permissions only when a user taps the request button
         */
        binding.requestPermissionsView.grantPermissions.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.root.setOnRefreshListener {
            onLocationPermissionsUpdate()
        }

        registerObservables()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        /**
         * Doing this check on resume to reflect permission changes on the UI without a lot of plumbing
         */
        onLocationPermissionsUpdate()
    }

    /**
     * Checks if a user has granted location permissions.
     */
    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Updates UI to toggle between show the permission EducationRationaleUI vs Weather details
     *
     * I'm showing a button to request for permissions if the user hasn't given us any already.
     */
    private fun onLocationPermissionsUpdate() {
        val hasLocationPermissions = hasLocationPermissions()
        with(binding) {
            //Toggle view to show permissions request view or not.
            requestPermissionsView.root.isVisible = !hasLocationPermissions
            weatherDetails.locationDataContainer.isVisible = hasLocationPermissions
        }

        if (hasLocationPermissions) {
            lifecycleScope.launch {
                try {
                    LocationHelper.getLatLong(requireContext()).take(1).collect { lastLocation ->
                        mainViewModel.getCurrentWeather(lastLocation.latitude, lastLocation.longitude)
                    }
                } catch (e: IllegalStateException) {
                    binding.root.showSnackBar(getString(R.string.permissions_denial_string))
                }
            }
        }
    }

    private fun registerObservables() {
        mainViewModel.weatherData.observe(viewLifecycleOwner, ::onWeatherUpdate)
    }

    @SuppressLint("SetTextI18n")
    private fun onWeatherUpdate(operation: AsyncOperation<Weather>) {
        when (operation) {
            is Loading -> {
                binding.loader.isVisible = true
                binding.weatherDetails.locationDataContainer.isVisible = false
            }

            is Completed -> {
                binding.root.isRefreshing = false
                binding.loader.isVisible = false
                binding.weatherDetails.locationDataContainer.isVisible = true
                operation.onSuccess { weather ->
                    with(binding.weatherDetails) {
                        locationText.text = weather.currentWeather.location
                        with(weather.currentWeather.details) {
                            highTempText.text = "High: $highTemp"
                            lowTempText.text = "Low: $lowTemp"
                            currentTempText.text = "$currentTemp°F"
                            Glide.with(requireContext())
                                .load("https://openweathermap.org/img/wn/${icon}@2x.png")
                                .into(iconImageView)
                        }
                    }
                }
                operation.onFailure {
                    val errorMessage = (it as? Failure)?.errorData ?: it.message
                    binding.root.showSnackBar(errorMessage.orEmpty())
                }
            }
        }
    }
}

fun View.showSnackBar(message: String) = Snackbar.make(
    this,
    message,
    Snackbar.LENGTH_LONG
).show()

/**
 * Simple helper to grab lat and long once permissions are granted.
 */
object LocationHelper {
    /**
     * Since locationListeners from [LocationManager] are not flows,
     * I use a callbackFlow to transform them to Flows here.
     */
    @SuppressLint("MissingPermission")
    fun getLatLong(context: Context): Flow<Location> {
        return callbackFlow {
            val locationListener = LocationListener { p0 -> trySend(p0) }
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

            awaitClose {
                channel.close()
            }
        }
    }
}