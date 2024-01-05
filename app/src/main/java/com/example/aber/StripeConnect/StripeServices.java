package com.example.aber.StripeConnect;

import com.example.aber.Models.User.Customer;
import io.reactivex.Observable;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface StripeServices {

    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Authorization: Bearer sk_test_51ORWc2DplToi6UQra8zjmk6QtCmT8dRv8z2GTumoVzl1wQ9CRlrkIueYLrZVpF921C7MbJNOJTKjlZw4v05ajfTU002X3C38GY"
    })

    @FormUrlEncoded
    @POST("customers")
    Observable<Customer> createCustomer(
            @Field("email") String email
    );
}
