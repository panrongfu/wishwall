package org.ecfex.app.teamwork.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author panRongFu on 2016/4/8.
 * @Description
 * @email pan@ipushan.com
 */
public class ApiClient {

    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.1.1.179:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static ApiService apiService = retrofit.create(ApiService.class);

}
