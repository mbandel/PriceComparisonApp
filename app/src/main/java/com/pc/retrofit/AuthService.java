package com.pc.retrofit;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthService {
//    private UserAuthListener userAuthListener;
//    private Connector connector;
//
//    public AuthService(UserAuthListener userAuthListener) {
//        this.userAuthListener=userAuthListener;
//        connector = Connector.getInstance();
//    }
//
//    public void loginUser(com.pc.retrofit.Credentials credentials) {
//        ServerApi client = connector.getRetrofit().create(ServerApi.class);
//
//        Call<String> call = client.loginUser(credentials);
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.isSuccessful())
//                    Log.d("token", response.body());
//                else
//                    Log.d("token", "wrong email or password");
//                System.out.println(call);
//                System.out.println(response);
//                userAuthListener.serviceSuccess(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("response" + " error", t.toString());
//                System.out.println(call);
//                userAuthListener.serviceFailure(new Exception());
//            }
//        });
//    }
}
