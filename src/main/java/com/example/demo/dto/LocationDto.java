package com.example.demo.dto;

import com.example.demo.config.ValidationMessages;
import jakarta.validation.constraints.*;

public class LocationDto {

    @NotNull(message = ValidationMessages.latitude)
    @DecimalMin(value = "0.01", message = "Latitudinea trebuie să fie mai mare ca 0")
    private Double lat;

    @NotNull(message = ValidationMessages.longitude)
    @DecimalMin(value = "0.01", message = "Longitudinea trebuie să fie mai mare ca 0")
    private Double lon;

    // Getters & Setters
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }
}

