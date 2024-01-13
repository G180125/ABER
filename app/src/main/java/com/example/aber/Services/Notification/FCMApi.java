package com.example.aber.Services.Notification;

import com.example.aber.Models.Notification.NotificationRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMApi {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer AAAAd5Y-mGM:APA91bFj8joDbFKd7b77YuozGJkuqZJVK9N6r4L1CpgLbWOu-nIukEJOPQLzx3LURpu9hfMySlcvN9-WL8sXhqSoXDQuVtZvmYdZX7ZfEGKOENCLJbi9HRfaN00XoaNWixohJRzsnFWB"
    })
    @POST("fcm/send")
    static Call<Void> sendNotification(@Body NotificationRequest notificationRequest);
}
