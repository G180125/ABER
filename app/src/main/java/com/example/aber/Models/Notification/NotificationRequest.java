package com.example.aber.Models.Notification;

public class NotificationRequest {
    private Notification notification;
    private String userId;
    private String to;

    public NotificationRequest(Notification notification, String userId, String to) {
        this.notification = notification;
        this.userId = userId;
        this.to = to;
    }
}

