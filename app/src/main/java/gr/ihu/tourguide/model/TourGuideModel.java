package gr.ihu.tourguide.model;

import android.location.Location;


import java.util.List;

import gr.ihu.tourguide.interfaces.IntModel;
import gr.ihu.tourguide.model.attractions.Attraction;


public class TourGuideModel implements IntModel {


    @Override
    public Location getCurrentLocation() {
        return null;
    }

    @Override
    public List<Attraction> getNearbyAttractions(Location location) {
        return null;
    }

    @Override
    public Attraction getAttraction(int attractionId) {
        return null;
    }

    @Override
    public List<Attraction> searchAttractions(String query) {
        return null;
    }

    @Override
    public boolean createUser(String username, String password) {
        return false;
    }
}
