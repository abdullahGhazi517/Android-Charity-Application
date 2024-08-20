package com.example.tawfiqthefooddonationapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {

    ImageView fb, linkedln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        fb = findViewById(R.id.imgFb);
        linkedln = findViewById(R.id.imgLink);



        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myWeblink = new Intent(Intent.ACTION_VIEW);
                myWeblink.setData(Uri.parse("https://www.facebook.com/AbdullahGhazi42/"));
                startActivity(myWeblink);
            }
        });

        linkedln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myWeblink = new Intent(Intent.ACTION_VIEW);
                myWeblink.setData(Uri.parse("https://www.linkedin.com/in/abdullah-ghazi-software-engineer/"));
                startActivity(myWeblink);
            }
        });

    }
}