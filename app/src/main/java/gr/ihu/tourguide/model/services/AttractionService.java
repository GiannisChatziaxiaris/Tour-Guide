package gr.ihu.tourguide.model.services;

import java.util.List;

import gr.ihu.tourguide.interfaces.IntAttractionService;
import gr.ihu.tourguide.interfaces.IntModel;
import gr.ihu.tourguide.model.attractions.Attraction;

public class AttractionService implements IntAttractionService {

    @Override
    public Attraction getAttraction(IntModel model, int attractionId) {
        return null;
    }

    @Override
    public List<Attraction> searchAttractions(IntModel model, String query) {
        return null;
    }
}
