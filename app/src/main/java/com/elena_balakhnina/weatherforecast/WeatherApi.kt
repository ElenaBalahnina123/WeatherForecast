package com.elena_balakhnina.weatherforecast

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"


interface RetrofitService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Float,
        @Query("lon") longitude: Float,
        @Query("appid") appId: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): Weather
}

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    fun retrofitProvide(client: OkHttpClient) :Retrofit {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(Json{
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit) : RetrofitService {
        return retrofit.create()
    }
}


@Serializable
data class Weather(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("temp")
    val temp: Float,
    @SerialName("feels_like")
    val feels_like: Float
)