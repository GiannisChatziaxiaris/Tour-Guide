package gr.ihu.tourguide.model.attractions;

public class Attraction {
    private int id;
    private String name;
    private String description;
    private Location location;

    // Constructor
    public Attraction(int id, String name, String description, Location location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }
}
