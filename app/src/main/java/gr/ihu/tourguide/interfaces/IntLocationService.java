package gr.ihu.tourguide.interfaces;



import java.util.List;

import gr.ihu.tourguide.model.TourGuideModel;
import gr.ihu.tourguide.model.attractions.Attraction;
import gr.ihu.tourguide.model.attractions.Location;

public interface IntLocationService {
    Location getCurrentLocation(TourGuideModel model);
    List<Attraction> getNearbyAttractions(TourGuideModel model, Location location);


}
