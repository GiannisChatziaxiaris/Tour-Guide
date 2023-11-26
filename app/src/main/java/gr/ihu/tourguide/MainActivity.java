package gr.ihu.tourguide;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonSignup;;
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
                // login logikh edw
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // check username and password profanws prepei na to allaxw gia na doulevei
                if (username.equals("your_username") && password.equals("your_password")) {

                } else {
                    // Failed login, display an error message or handle it as needed
                }
                buttonSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Navigate to the signup screen
                        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(intent);
                    }

                });
            }
        });
    };
}