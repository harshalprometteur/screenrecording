package com.example.recording;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.screenrecodinglibrary.ToastModule;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToastModule.showToast(MainActivity.this);
    }
}