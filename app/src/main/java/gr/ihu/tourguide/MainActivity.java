package gr.ihu.tourguide;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String CORRECT_USERNAME = "user"; //"user" prepei na allaxh me thn vash tou mike
    private static final String CORRECT_PASSWORD = "password"; // "password" to idio me panw
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonSignup;
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                buttonSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Navigate to the signup screen
                        Intent intent = new Intent(gr.ihu.tourguide.MainActivity.this, SignupActivity.class);
                        startActivity(intent);
                    }


                    class Login extends AppCompatActivity {

                        @Override
                        protected void onCreate(Bundle savedInstanceState) {
                            super.onCreate(savedInstanceState);
                            setContentView(R.layout.activity_main);


                            Button loginButton = findViewById(R.id.buttonLogin);

                            loginButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    performLogin();
                                }
                            });
                        }
                    }

                    private void performLogin() {
                        // Get references to the username and password EditText fields
                        EditText usernameEditText = findViewById(R.id.editTextUsername);
                        EditText passwordEditText = findViewById(R.id.editTextPassword);

                        // edw pairnw ta stoixeia pou plhktrologise o xrhsths
                        String enteredUsername = usernameEditText.getText().toString();
                        String enteredPassword = passwordEditText.getText().toString();

                        // Checkarw an einai ola swsta
                        if (enteredUsername.equals(CORRECT_USERNAME) && enteredPassword.equals(CORRECT_PASSWORD)) {
                            // Successful login
                            Toast.makeText(mainmainactivity.this, "Συνδεθήκατε επιτυχώς!", Toast.LENGTH_SHORT).show();

                           //den yparxei onoma gia to epomeno activity opote to "vaftisa" main main activity
                        } else {
                            // Failed login
                            Toast.makeText(gr.ihu.tourguide.MainActivity.this, "Λανθασμένο όνομα ή κωδικός χρήστη", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

