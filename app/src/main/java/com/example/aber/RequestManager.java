package com.example.aber;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aber.Models.PlaceResponse.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestManager {
    private Context context;
    private Retrofit retrofit;

    public RequestManager(Context context) {
        this.context = context;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void getPlaceDetails(String placeId, String apiKey, final OnFetchDataListener listener) {
        PlacesApiService apiService = retrofit.create(PlacesApiService.class);

        Call<String> call = apiService.getPlaceDetails(placeId, apiKey);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // If the response is successful, pass the response string to the listener
                    listener.onFetchData(response.body());
                } else {
                    // If the response is not successful, pass the error message to the listener
                    listener.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // If the request fails, pass the error message to the listener
                listener.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public interface PlacesApiService {
        @GET("details/json")
        Call<String> getPlaceDetails(
                @Query("place_id") String placeId,
                @Query("key") String apiKey
        );
    }

    public interface OnFetchDataListener {
        void onFetchData(String response);
        void onError(String message);
    }
}