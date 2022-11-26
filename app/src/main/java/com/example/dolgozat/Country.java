package com.example.dolgozat;

public class Country {
    private int id;
    private String country;
    private String city;
    private int population;

    public Country(int id, String country, String city, int population) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.population = population;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public int getPopulation() {
        return population;
    }
}
