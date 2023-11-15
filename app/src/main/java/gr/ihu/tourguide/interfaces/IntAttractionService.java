package gr.ihu.tourguide.interfaces;

import java.util.List;

import gr.ihu.tourguide.model.attractions.Attraction;

public interface IntAttractionService {
    Attraction getAttraction(IntModel model, int attractionId);
    List<Attraction> searchAttractions(IntModel model, String query);
}
