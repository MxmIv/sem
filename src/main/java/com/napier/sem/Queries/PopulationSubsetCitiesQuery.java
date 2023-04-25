package com.napier.sem.Queries;

import com.napier.sem.City;
import com.napier.sem.Country;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PopulationSubsetCitiesQuery {
    //get city data
    public static void populationCitiesInSubset (Connection con, int limitBy){
        List<City> allCities = getAllCities(con);
        List<Country> allCountries = CountriesQueries.getAllCountries(con);
        List<City> citiesInDistrict = getCitiesInDistrict("England", allCities);
        List<City> citiesInCountry = getCitiesInCountry("Germany", allCities, allCountries);
        List<City> citiesInRegion = getCitiesInRegion("Eastern Asia", allCities, allCountries);
        List<City> citiesInContinent = getCitiesInContinent("Europe", allCities, allCountries);
        List<City> allCitiesLimited = getCitiesLimitedBy(limitBy, allCities);
        List<City> citiesInDistrictLimited = getCitiesLimitedBy(limitBy, citiesInDistrict);
        List<City> citiesInCountryLimited = getCitiesLimitedBy(limitBy, citiesInCountry);
        List<City> citiesInRegionLimited = getCitiesLimitedBy(limitBy, citiesInRegion);
        List<City> citiesInContinentLimited = getCitiesLimitedBy(limitBy, citiesInContinent);

        //columns format
        String format = "%-30s %-50s %-40s %-15s";

        //report generation
        printReport(
                "All the cities in the world organised by largest population to smallest",
                format,
                allCities);
        printReport(
                "All the cities in a district organised by largest population to smallest.",
                format,
                citiesInDistrict);
        printReport(
                "All the cities in a country organised by largest population to smallest.",
                format,
                citiesInCountry);
        printReport(
                "All the cities in a region organised by largest population to smallest.",
                format,
                citiesInRegion);
        printReport(
                "All the cities in a continent organised by largest population to smallest.",
                format,
                citiesInContinent);
        printReport(
                "The top N populated cities in the world where N is provided by the user.",
                format,
                allCitiesLimited);
        printReport(
                "The top N populated cities in a district where N is provided by the user.",
                format,
                citiesInDistrictLimited);
        printReport(
                "The top N populated cities in a country where N is provided by the user.",
                format,
                citiesInCountryLimited);
        printReport(
                "The top N populated cities in a region where N is provided by the user.",
                format,
                citiesInRegionLimited);
        printReport(
                "The top N populated cities in a continent where N is provided by the user.",
                format,
                citiesInContinentLimited);

    }

    public static List<City> getAllCities(Connection con) {
        //create list to hold data
        List<City> allCities = new ArrayList<>();

        try
        {
            //create a SQL statement
            Statement stmt = con.createStatement();
            //execute SQL statement
            ResultSet rset = stmt.executeQuery(
                    "SELECT city.CountryCode AS CountryCode, "
                            + "city.Name AS Name, "
                            + "city.District AS District, "
                            + "city.Population AS Population "
                            + "FROM city "
                            + "ORDER BY city.Population DESC ");

            while (rset.next())
            {
                //map query result to country object and add to list
                City city = new City();

                city.setCountryCode(rset.getString("CountryCode"));
                city.setName(rset.getString("Name"));
                city.setDistrict(rset.getString("District"));
                city.setPopulation(rset.getInt("Population"));

                allCities.add(city);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve city details");
        }

        return allCities;
    }

    //use list of all cities to get cities in a district
    public static List<City> getCitiesInDistrict(String district, List<City> cities) {
        List<City> citiesInDistrict = new ArrayList<>();

        for (City city:cities) {
            if (city.getDistrict().equals(district)) {
                citiesInDistrict.add(city);
            }
        }

        return citiesInDistrict;
    }
    //use list of all cities/countries to get cities in a country
    public static List<City> getCitiesInCountry(String country, List<City> cities, List<Country> countries) {
        List<City> citiesInCountry = new ArrayList<>();
        String countryCode = "";
        for (Country c:countries){
            if (c.getName().equals(country)){
                countryCode = c.getCode();
            }
        }

        for (City city:cities) {
            if (city.getCountryCode().equals(countryCode)) {
                citiesInCountry.add(city);
            }
        }

        return citiesInCountry;
    }
    //use list of all cities/countries to get cities in a region
    public static List<City> getCitiesInRegion(String region, List<City> cities, List<Country> countries) {
        List<City> citiesInRegion = new ArrayList<>();
        List<Country> countriesInRegion = CountriesQueries.getCountriesInRegion(region, countries);
        List<String> countryCodes = new ArrayList<>();

        for (Country c:countriesInRegion){
            countryCodes.add(c.getCode());
        }

        for (City city:cities) {
            for (String code: countryCodes) {
                if (city.getCountryCode().equals(code)) {
                    citiesInRegion.add(city);
                }
            }
        }

        return citiesInRegion;
    }
    //use list of all cities/countries to get cities in a region
    public static List<City> getCitiesInContinent(String continent, List<City> cities, List<Country> countries) {
        List<City> citiesInContinent = new ArrayList<>();
        List<Country> countriesInContinent = CountriesQueries.getCountriesInContinent(continent, countries);
        List<String> countryCodes = new ArrayList<>();

        for (Country c:countriesInContinent){
            countryCodes.add(c.getCode());
        }

        for (City city:cities) {
            for (String code: countryCodes) {
                if (city.getCountryCode().equals(code)) {
                    citiesInContinent.add(city);
                }
            }
        }

        return citiesInContinent;
    }

    //use list to get top n cities
    public static List<City> getCitiesLimitedBy(int limit, List<City> cities){
        List<City> citiesLimited = new ArrayList<>();

        if (limit > cities.size()) {
            limit = cities.size();
        }

        for (int i=0; i < limit; i++){
            citiesLimited.add(cities.get(i));
        }

        return citiesLimited;
    }


    //method to print a report from a list
    public static void printReport(String header, String format, List<City> list) {
        System.out.println(String.format(header));

        System.out.println(String.format(format,
                "Name", "Country", "District", "Population"));

        // Loop over all countries in the list
        for (City city : list)
        {
            if (city == null) {
                System.out.println("City is null");
                continue;
            }

            System.out.println(String.format(format,
                    city.getName(),
                    city.getCountryCode(),
                    city.getDistrict(),
                    city.getPopulation()));
        }
    }
}

