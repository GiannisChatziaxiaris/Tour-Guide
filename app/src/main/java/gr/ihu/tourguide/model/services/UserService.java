package gr.ihu.tourguide.model.services;

import gr.ihu.tourguide.interfaces.IntUserService;
import gr.ihu.tourguide.model.TourGuideModel;

public class UserService implements IntUserService {
    @Override
    public boolean createUser(TourGuideModel model, String username, String password) {
        return false;
    }
}
