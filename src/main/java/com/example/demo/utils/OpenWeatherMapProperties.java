package com.example.demo.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "openweathermap")
@Component
@Data
public class OpenWeatherMapProperties {
    private String apiKey;
}
