package com.example.tawfiqthefooddonationapp;


import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, SignIn.class).setFlags(FLAG_ACTIVITY_CLEAR_TASK));
            }
        }, 3000);

    }


}
