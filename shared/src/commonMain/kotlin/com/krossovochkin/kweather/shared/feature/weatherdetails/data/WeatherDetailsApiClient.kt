package com.krossovochkin.kweather.shared.feature.weatherdetails.data

import com.krossovochkin.kweather.shared.common.domain.CityId
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"

internal class WeatherDetailsApiClient(
    private val client: HttpClient,
    private val apiKey: String
) : WeatherDetailsApi {

    override suspend fun getWeatherDetails(cityId: CityId): WeatherDetailsDto {
        return client.get("$BASE_URL?id=${cityId.id}&appid=$apiKey&units=metric")
    }
}
