package com.example.vet.clases;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({"Content-Type: application/json",
            "Authorization: key=AAAAPbEBGqc:APA91bG4gqk7OsaC8E_lnajq4DDoSHxeuBRfbtFkwcNHuaRRsHYCZYii2M_Z-Y734jrOoqBYRkq86DS-HVthDldWJAD42TeJV2bhwjyuEEKB4GQjv8lxrkJB-C6EjKIBEH8SJl2WmxfX"})
    @POST("fcm/send")
    Call<Void> sendNotification(@Body RequestBody requestBody);
}
