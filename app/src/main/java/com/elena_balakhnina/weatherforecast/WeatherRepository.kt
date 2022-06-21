package com.elena_balakhnina.weatherforecast

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherRepoModule(service: RetrofitService) {

    @Binds
    abstract fun weatherRepoBinds(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository
}

interface WeatherRepository {
    suspend fun getWeather(): Weather
}

class WeatherRepositoryImpl @Inject constructor(private val retrofitService: RetrofitService) :
    WeatherRepository {

    companion object {
        private const val KEY = "d1206facd3bf321c5552416e0185e1f9"
    }

    override suspend fun getWeather(): Weather {
        val longitude = 49.65f
        val latitude = 58.59f
        val lang = "ru"
        val units = "metric"

        return retrofitService.getWeather(
            longitude = longitude,
            latitude = latitude,
            appId = KEY,
            lang = lang,
            units = units
        )
    }

}
