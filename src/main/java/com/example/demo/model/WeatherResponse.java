package com.example.demo.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private long id;
    private String name;
    private int cod;

    // Getters and setters

    public Coord getCoord() { return coord; }
    public void setCoord(Coord coord) { this.coord = coord; }

    public List<Weather> getWeather() { return weather; }
    public void setWeather(List<Weather> weather) { this.weather = weather; }

    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }

    public Main getMain() { return main; }
    public void setMain(Main main) { this.main = main; }

    public int getVisibility() { return visibility; }
    public void setVisibility(int visibility) { this.visibility = visibility; }

    public Wind getWind() { return wind; }
    public void setWind(Wind wind) { this.wind = wind; }

    public Clouds getClouds() { return clouds; }
    public void setClouds(Clouds clouds) { this.clouds = clouds; }

    public long getDt() { return dt; }
    public void setDt(long dt) { this.dt = dt; }

    public Sys getSys() { return sys; }
    public void setSys(Sys sys) { this.sys = sys; }

    public int getTimezone() { return timezone; }
    public void setTimezone(int timezone) { this.timezone = timezone; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCod() { return cod; }
    public void setCod(int cod) { this.cod = cod; }
}

