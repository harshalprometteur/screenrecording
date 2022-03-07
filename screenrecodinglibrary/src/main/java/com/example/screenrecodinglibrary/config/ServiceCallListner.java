package com.example.screenrecodinglibrary.config;

import org.json.JSONObject;

public interface ServiceCallListner {

    void onSuccess(JSONObject jsonObject, String response_msg);

    void onFailuer(JSONObject jsonObject, String response_msg);

    void showProgress();

    void hideProgress();
}
