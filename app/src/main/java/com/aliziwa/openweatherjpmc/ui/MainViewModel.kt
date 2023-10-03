package com.aliziwa.openweatherjpmc.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliziwa.domain.AsyncOperation
import com.aliziwa.domain.failure
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.model.WeatherByCityNameRequest
import com.aliziwa.domain.model.WeatherByLatLongRequest
import com.aliziwa.domain.success
import com.aliziwa.domain.usecase.GetLastSearchUseCase
import com.aliziwa.domain.usecase.GetWeatherByCityNameUseCase
import com.aliziwa.domain.usecase.GetWeatherByLatLonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [MainViewModel] - Using one view model for both fragments here to save time.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherByCityNameUseCase: GetWeatherByCityNameUseCase,
    private val getWeatherByLatLonUseCase: GetWeatherByLatLonUseCase,
    private val getLastSearchUseCase: GetLastSearchUseCase,
    /**
     * Using the default CoroutineDispatchers here.
     * In real production code, I would wrap these in our own application scope dispatcher.
     * That way we don't have to worry about making lots of modifications should the Android team
     * decide to change the structure.
     */
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    @VisibleForTesting
    var ioScope: CoroutineScope? = null

    /**
     * Using the viewModelScope as is for the sake of time.
     * In production code, I'd concatenate it with a custom CoroutineExceptionHandler to have something like:
     * ```
     *      val viewModelSafeScope = viewModelScope + customCoroutineExceptionHandler
     * ```
     */
    private val scope by lazy { ioScope ?: viewModelScope }
    private val _weatherData = MutableLiveData<AsyncOperation<Weather>>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    val weatherData: LiveData<AsyncOperation<Weather>> = _weatherData
    val selectedStateFlow = MutableStateFlow("CA")

    /**
     * Get current weather using latitude and longitude
     *
     * @param lat - [Double]
     * @param lon - [Double]
     */
    fun getCurrentWeather(lat: Double, lon: Double) {
        scope.launch(ioDispatcher) {
            getWeatherByLatLonUseCase
                .execute(WeatherByLatLongRequest(lat, lon))
                .catch { _weatherData.postValue(failure(it)) }
                .collect { operation ->
                    _weatherData.postValue(operation)
                }
        }
    }

    /**
     * Get search by city
     *
     * @param cityName - A user defined input type
     * @param state - Hard coded scope of states.
     */
    fun searchForCityWeather(cityName: String, state: String) {
        scope.launch(ioDispatcher) {
            getWeatherByCityNameUseCase
                .execute(WeatherByCityNameRequest(cityName, state))
                .catch { _weatherData.postValue(failure(it)) }
                .collect { operation ->
                    _weatherData.postValue(operation)
                }
        }
    }

    /**
     * Get last searched city
     */
    fun getLastSearchedWeather() {
        scope.launch(ioDispatcher) {
            getLastSearchUseCase(Unit).catch {
                _weatherData.postValue(failure(it))
            }.collect { weather ->
                _weatherData.postValue(success(weather))
            }
        }
    }
}