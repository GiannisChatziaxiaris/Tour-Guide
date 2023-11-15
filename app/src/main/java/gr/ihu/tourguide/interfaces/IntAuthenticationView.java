package gr.ihu.tourguide.interfaces;

public interface IntAuthenticationView extends IntView{
    /**
     * Display user-related information.
     *
     * @param username The username of the logged-in user.
     */
    void showUserInfo(String username);

    /**
     * Display a login screen.
     */
    void showLoginScreen();

    /**
     * Display a sign-up screen.
     */
    void showSignUpScreen();

    /**
     * Display a message indicating successful authentication.
     */
    void showAuthenticationSuccess();

    /**
     * Display a message indicating authentication failure.
     */
    void showAuthenticationFailure();

    // Other relevant methods for user interaction can be added based on app requirements.
}