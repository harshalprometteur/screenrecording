package com.example.screenrecodinglibrary;

import android.app.Activity;
import android.widget.Toast;

public class ToastModule {

    public static void showToast(Activity activity){
        Toast.makeText(activity, "HERE", Toast.LENGTH_SHORT).show();
    }
}
