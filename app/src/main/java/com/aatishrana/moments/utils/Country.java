package com.aatishrana.moments.utils;

/**
 * Created by Aatish on 6/22/2017.
 */

public class Country
{
    private final String name;
    private final String code;
    private final Double avgAge;

    public Country(String name, String code, Double avgAge)
    {
        this.name = name;
        this.code = code;
        this.avgAge = avgAge;
    }

    public String getName()
    {
        return name;
    }

    public String getCode()
    {
        return code;
    }

    public Double getAvgAge()
    {
        return avgAge;
    }
}
