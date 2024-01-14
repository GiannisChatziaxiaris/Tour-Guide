package gr.ihu.tourguide;

public class User {
    private String name;
    private String email;

    private String surname;
    // Add other user-related fields as needed

    // Required default constructor (for Firebase)
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name,String surname, String email /* add other fields here */) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        // Initialize other fields here if needed
    }

    // Getters and setters for the fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Add other getters and setters as needed for additional fields
}
