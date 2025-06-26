package com.example.demo.services;

import com.example.demo.dto.LocationDto;
import com.example.demo.model.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class WeatherService {

    private final Logger logger;
    private final OkHttpClient httpClient;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherService() {
        this.logger = Logger.getLogger(WeatherService.class.getName());
        this.httpClient = new OkHttpClient();
    }

    public ResponseEntity<String> getCurrentTemperature(LocationDto location) throws BadRequestException {
        this.logger.log(Level.INFO, "api key : " +  apiKey);
        WeatherResponse weather = this.getWeather(location.getLat(), location.getLon());
        double currentTemperature = weather.getMain().getTemp();
        return ResponseEntity.ok(Double.toString(currentTemperature));
    }

    public WeatherResponse getWeatherResponse(LocationDto locationDto) throws BadRequestException {
        return this.getWeather(locationDto.getLat(), locationDto.getLon());
    }

    private WeatherResponse getWeather(Double lat, Double lon) throws BadRequestException {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric",
                lat, lon, apiKey
        );
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BadRequestException("Unexpected code " + response);
            }
            assert response.body() != null;
            String responseBody = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseBody, WeatherResponse.class);
        } catch (IOException e) {
            this.logger.log(Level.WARNING, "Error occurred while trying to get weather response", e);
            throw new BadRequestException("Error occured while trying to get weather");
        }
    }
}
