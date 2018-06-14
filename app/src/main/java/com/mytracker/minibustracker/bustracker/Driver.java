package com.mytracker.minibustracker.bustracker;

/**
 * Created by haroonpc on 3/12/2018.
 */

public class Driver
{
    Driver()
    {}

    public String name,email,password,vehiclenumber,lat,lng;

    public Driver(String name, String email, String password, String vehiclenumber, String lat, String lng) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.vehiclenumber = vehiclenumber;
        this.lat = lat;
        this.lng = lng;
    }
}
