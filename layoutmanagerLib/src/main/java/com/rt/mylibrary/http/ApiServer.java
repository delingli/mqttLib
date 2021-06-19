package com.rt.mylibrary.http;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServer {
    @GET("http://wthrcdn.etouch.cn/weather_mini")
    Observable<String> getWeather(@Query("city") String city);
}

