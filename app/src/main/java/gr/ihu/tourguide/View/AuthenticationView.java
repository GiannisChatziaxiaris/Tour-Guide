package gr.ihu.tourguide.View;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.View;

import gr.ihu.tourguide.MainActivity;
import gr.ihu.tourguide.interfaces.IntAuthenticationView;

public class AuthenticationView implements IntAuthenticationView {

    @Override
    public void showUserInfo(String username) {

    }

    @Override
    public void showLoginScreen() {

    }

    @Override
    public void showSignUpScreen() {
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the signup screen
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }

        });
    }

    private void startActivity(Intent intent) {

    }

    @Override
    public void showAuthenticationSuccess() {

    }

    @Override
    public void showAuthenticationFailure() {

    }

    @Override
    public void update() {

    }
}
