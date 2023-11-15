package gr.ihu.tourguide.View;

import gr.ihu.tourguide.interfaces.IntNotificationView;

public class NotificationView implements IntNotificationView {
    @Override
    public void showNotification(String message) {
        // Display the notification message
        System.out.println("Notification: " + message);
    }

    @Override
    public void update() {

    }
}
