package com.pc.retrofit;

import com.pc.R;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Getter
public class Connector {

    private static Connector connector = null;
    private OkHttpClient.Builder httpClient;
    private Retrofit.Builder builder;
    private Retrofit retrofit;
    public ServerApi serverApi;

    private Connector(){
        httpClient = new OkHttpClient.Builder();

        builder = new Retrofit.Builder()
                .baseUrl(Config.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.client(
                httpClient.build())
                .build();

        serverApi = retrofit.create(ServerApi.class);
    }

    public static Connector getInstance() {
        if (connector == null) {
            connector = new Connector();
        }
        return connector;
    }


}
