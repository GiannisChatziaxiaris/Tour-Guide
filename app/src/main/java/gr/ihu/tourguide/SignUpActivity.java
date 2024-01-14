package gr.ihu.tourguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextEmail,editTextPassword,editTextPasswordConfirm,editTextName,editTextSurname;

    Button signupButton, loginButton;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_up);
        mAuth= FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance("https://project-database-42bd5-default-rtdb.europe-west1.firebasedatabase.app/");
        editTextName  = findViewById(R.id.signup_name);
        editTextSurname = findViewById(R.id.signup_surname);
        editTextEmail = findViewById(R.id.signup_email);
        editTextPassword = findViewById(R.id.signup_password);
        editTextPasswordConfirm = findViewById(R.id.signup_password_confirm);
        signupButton = findViewById(R.id.signup_button);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        })


        ;

        signupButton.setOnClickListener(view -> {
            String name,surname,email,password,passwordConfirm;
            name = editTextName.getText().toString();
            surname = editTextSurname.getText().toString();
            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();
            passwordConfirm = editTextPasswordConfirm.getText().toString();

            if(TextUtils.isEmpty(name)){
                Toast.makeText(SignUpActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(surname)){
                Toast.makeText(SignUpActivity.this, "Enter surname", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(email)){
                Toast.makeText(SignUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(passwordConfirm)){
                Toast.makeText(SignUpActivity.this, "Confirm password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform user authentication (sign-up or login)
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                            startActivity(intent);
                            finish();

                            // User creation successful
                            String userID = mAuth.getCurrentUser().getUid();

                            // Save user profile data to the database
                            DatabaseReference usersRef = mDatabase.getReference().child("users").child(userID);

                            User newUser = new User(name,surname, email /*other details*/);

                            // Save user profile data
                            usersRef.setValue(newUser)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            // User profile data saved successfully

                                            DatabaseReference searchHistoryRef = mDatabase.getReference()
                                                    .child("search_history")
                                                    .child(userID);

                                            // Set initial search history or proceed with other operations/UI changes



                                        } else {
                                            // Failed to save user profile data
                                        }
                                    });
                        } else {
                            // User creation failed
                        }
                    });


        });

    }
}