package gr.ihu.tourguide.model.services;



import java.util.List;

import gr.ihu.tourguide.interfaces.IntLocationService;
import gr.ihu.tourguide.model.TourGuideModel;
import gr.ihu.tourguide.model.attractions.Attraction;
import gr.ihu.tourguide.model.attractions.Location;

public class LocationService implements IntLocationService {
    @Override
    public Location getCurrentLocation(TourGuideModel model) {
        return null;
    }

    @Override
    public List<Attraction> getNearbyAttractions(TourGuideModel model, Location location) {
        return null;
    }
}
