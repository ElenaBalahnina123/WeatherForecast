package com.elena_balakhnina.weatherforecast

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.elena_balakhnina.weatherforecast.databinding.WeatherFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.annotation.meta.When
import javax.inject.Inject


@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.weather_fragment) {

   private val viewModel by viewModels<WeatherFragmentVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        WeatherFragmentBinding.bind(view).run {
            viewModel._state.onEach {
                when(it) {
                    is WeatherVMState.Content -> {
                        nameTv.text = it.weather.name
                        descriptionTv.text = it.weather.description
                        tempTv.text = it.weather.temp.toString()
                        textView4.text = it.weather.feels_like.toString()
                    }
                    WeatherVMState.Loading -> {
                        nameTv.text = "not found"
                        descriptionTv.text = "not found"
                        tempTv.text = "not found"
                        textView4.text = "not found"}
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }
}

@HiltViewModel
class WeatherFragmentVM @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {

    val mutableState = MutableStateFlow<WeatherVMState>(WeatherVMState.Loading)

    val _state : StateFlow<WeatherVMState>
        get() = mutableState.asStateFlow()

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            mutableState.value = WeatherVMState.Loading

            mutableState.value = try {
                WeatherVMState.Content(
                    weatherRepository.getWeather()
                )
            } catch (err : Throwable) {
                if(err is CancellationException) throw err
                WeatherVMState.Error(err)
            }
        }

    }
}


sealed class WeatherVMState {
    object Loading : WeatherVMState()
    data class Content (
        val weather : Weather
            ) : WeatherVMState()
   data class Error (
       val error : Throwable
           ) : WeatherVMState()
        }