package com.aliziwa.openweatherjpmc.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.aliziwa.domain.AsyncOperation
import com.aliziwa.domain.Completed
import com.aliziwa.domain.Failure
import com.aliziwa.domain.Loading
import com.aliziwa.domain.model.Weather
import com.aliziwa.openweatherjpmc.R
import com.aliziwa.openweatherjpmc.databinding.FragmentSearchBinding
import com.aliziwa.openweatherjpmc.ui.MainViewModel
import com.aliziwa.openweatherjpmc.ui.base.BaseFragment
import com.aliziwa.openweatherjpmc.ui.home.showSnackBar
import com.bumptech.glide.Glide
import com.google.android.material.internal.ViewUtils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(), AdapterView.OnItemSelectedListener {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupView()

        registerObservables()
        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedState = parent?.getItemAtPosition(position)
        //Defaulting to CA, we can decide to throw an exception back to the user here
        mainViewModel.selectedStateFlow.value = selectedState as? String ?: "CA"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Defaulting to CA, we can decide to throw an exception back to the user here
        mainViewModel.selectedStateFlow.value = "CA"
    }

    @SuppressLint("RestrictedApi")
    private fun setupView() {
        //Setup spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.us_states,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.stateSpinner.adapter = it
        }

        //Register to receive selection events
        binding.stateSpinner.onItemSelectedListener = this

        //Register search button click events
        with(binding) {
            searchButton.setOnClickListener {
                hideKeyboard(binding.root)
                if (cityName.text.isNullOrEmpty())
                    root.showSnackBar(getString(R.string.city_and_state_error))
                else
                    mainViewModel.searchForCityWeather(cityName.text.toString(), mainViewModel.selectedStateFlow.value)
            }
        }

        //fetch last searched weather
        mainViewModel.getLastSearchedWeather()
    }

    private fun registerObservables() {
        mainViewModel.weatherData.observe(viewLifecycleOwner, ::onWeatherUpdate)
    }

    /**
     * This method also appears in the [HomeFragment] - It's a quick hack to get things running.
     */
    @SuppressLint("SetTextI18n")
    private fun onWeatherUpdate(operation: AsyncOperation<Weather>) {
        when (operation) {
            is Loading -> {
                binding.loader.isVisible = true
                binding.weatherDetailsView.locationDataContainer.isVisible = false
            }

            is Completed -> {
                binding.loader.isVisible = false
                binding.weatherDetailsView.locationDataContainer.isVisible = true
                operation.onSuccess { weather ->
                    with(binding.weatherDetailsView) {
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
                    val errorMessage = (it as? Failure)?.errorData
                    binding.root.showSnackBar(errorMessage.orEmpty())
                }
            }
        }
    }
}