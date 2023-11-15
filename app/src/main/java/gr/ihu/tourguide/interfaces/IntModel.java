package gr.ihu.tourguide.interfaces;
import android.location.Location;

import java.util.List;

import gr.ihu.tourguide.model.attractions.Attraction;


public interface IntModel {
    /**
     * Get the current location of the user.
     *
     * @return The current location.
     */
    Location getCurrentLocation();

    /**
     * Get a list of nearby attractions based on the given location.
     *
     * @param location The user's location.
     * @return A list of nearby attractions.
     */
    List<Attraction> getNearbyAttractions(Location location);

    /**
     * Get details of a specific attraction.
     *
     * @param attractionId The ID of the attraction.
     * @return Details of the attraction.
     */
    Attraction getAttraction(int attractionId);

    /**
     * Search for attractions based on a query.
     *
     * @param query The search query.
     * @return A list of attractions matching the query.
     */
    List<Attraction> searchAttractions(String query);

    /**
     * Create a new user.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @return True if the user is created successfully, false otherwise.
     */
    boolean createUser(String username, String password);

    // Other relevant methods can be added based on app requirements.
}