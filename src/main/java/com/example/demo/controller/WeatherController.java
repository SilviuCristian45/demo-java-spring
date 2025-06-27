package com.example.demo.controller;

import com.example.demo.dto.LocationDto;
import com.example.demo.model.WeatherResponse;
import com.example.demo.services.UserService;
import com.example.demo.services.WeatherService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService service;

    // Injectăm service-ul prin constructor
    public WeatherController(WeatherService service) {
        this.service = service;
    }


    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> postLocation(@RequestBody @Valid LocationDto location) throws BadRequestException {
        return this.service.getCurrentTemperature(location);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ceva")
    public ResponseEntity<WeatherResponse> getWeather(@RequestBody @Valid  LocationDto location) throws BadRequestException {
        return ResponseEntity.ok(this.service.getWeatherResponse(location));
    }
}
