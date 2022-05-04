package com.example.livelibtestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

public class LoadActivity extends AppCompatActivity {

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        logo = findViewById(R.id.logo);
        //logo.animate().scaleX(0).scaleY(0).setDuration(1500);
        //logo.animate().scaleX(1).scaleY(1).setDuration(1500);
        logo.animate().alpha(0).scaleX(0.0f).scaleY(0.0f).setDuration(4000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                SharedPreferences sharedPreferences = getSharedPreferences("curr_user", MODE_PRIVATE);
                String json = sharedPreferences.getString("user","");
                User currUser = gson.fromJson(json,User.class);

                if (currUser==null){
                    startActivity(new Intent(LoadActivity.this,AuthActivity.class));
                } else {
                    User.setCurrentUser(currUser);
                    startActivity(new Intent(LoadActivity.this,HomeActivity.class));
                }
                finish();

            }
        },4000);
    }
}