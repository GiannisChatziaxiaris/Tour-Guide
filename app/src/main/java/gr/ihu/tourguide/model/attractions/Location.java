package gr.ihu.tourguide.model.attractions;

public class Location {
    private double latitude;
    private double longitude;

    // Constructors, getters, and setters

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
