package gr.ihu.tourguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gr.ihu.tourguide.Login;
import gr.ihu.tourguide.R;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    TextView userNameTextView;
    TextView userSurnameTextView;
    TextView userEmailTextView;
    TextView userSearchHIstoryTextView;
    Button logoutButton;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://project-database-42bd5-default-rtdb.europe-west1.firebasedatabase.app/");

        userNameTextView = findViewById(R.id.user_name);
        userSurnameTextView = findViewById(R.id.user_surname);
        userEmailTextView = findViewById(R.id.user_email);
        userSearchHIstoryTextView = findViewById(R.id.search_history_text);

        logoutButton = findViewById(R.id.button_logout);


        user = mAuth.getCurrentUser();


        String userID = mAuth.getCurrentUser().getUid();

        // Reference to the user's profile node in the database
        DatabaseReference userRef = mDatabase.getReference().child("users").child(userID);


        // Fetch user data from Firebase Database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    // Retrieve user details
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String name = user.getName();
                        String surname = user.getSurname();
                        String email = mAuth.getCurrentUser().getEmail();

                        // Update TextViews with user details
                        userNameTextView.setText("Name: " + name);
                        userSurnameTextView.setText("Surname: " + surname);
                        userEmailTextView.setText("Email: " + email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        DatabaseReference searchHistoryRef = mDatabase.getReference().child("search_history").child(userID);
        searchHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> searchEntries = new ArrayList<>();

                    // Fetch all search history entries
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String searchedPlace = snapshot.getValue(String.class);
                        searchEntries.add(searchedPlace);
                    }

                    // Ensure we have at least 5 entries
                    int totalEntries = searchEntries.size();
                    int start = (totalEntries > 5) ? totalEntries - 5 : 0;

                    // Display the last 5 (or less) search history entries
                    StringBuilder searchHistory = new StringBuilder();
                    for (int i = start; i < totalEntries; i++) {
                        searchHistory.append(searchEntries.get(i)).append("\n");
                    }

                    // Display the limited search history in a TextView or other UI element
                   userSearchHIstoryTextView.setText(searchHistory.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}