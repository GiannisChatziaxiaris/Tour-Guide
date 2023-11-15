package gr.ihu.tourguide.interfaces;

import gr.ihu.tourguide.model.TourGuideModel;

public interface IntUserService {
    boolean createUser(TourGuideModel model, String username, String password);
}
