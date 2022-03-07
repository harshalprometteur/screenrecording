package com.example.screenrecodinglibrary.config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceCall {

    private final Call<ResponseBody> mResponseBodyCall;
    private final ServiceCallListner mServiceCallListner;
    private final Context mActivity;


    public ServiceCall(Context mActivity, Call<ResponseBody> mResponseBodyCall, ServiceCallListner mServiceCallListner) {
        this.mResponseBodyCall = mResponseBodyCall;
        this.mServiceCallListner = mServiceCallListner;
        this.mActivity = mActivity;
        serviceCall();
    }

    private void serviceCall() {

        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
//            Toast.makeText(mActivity, "Check your network connection", Toast.LENGTH_SHORT).show();
            mServiceCallListner.onFailuer(null, "Please check Network Connection");
            return;
        }


        mServiceCallListner.showProgress();
        mResponseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                mServiceCallListner.hideProgress();

                try {
                    Log.e("Response", response.toString());

                    String mResponseString = response.body().string();

                    Log.e("Response=========>", mResponseString);

                    if (mResponseString.equals("Successfully uploaded the video")) {
                        mServiceCallListner.onSuccess(new JSONObject(), mResponseString);
                    } else {
                        mServiceCallListner.onFailuer(new JSONObject(), mResponseString);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mServiceCallListner.hideProgress();
            }
        });
    }
}
