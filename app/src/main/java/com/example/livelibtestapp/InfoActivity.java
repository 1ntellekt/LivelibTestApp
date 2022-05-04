package com.example.livelibtestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InfoActivity extends AppCompatActivity {

    private BottomNavigationView bottom_nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        bottom_nav_view = findViewById(R.id.bottom_nav_view);

        bottom_nav_view.setSelectedItemId(R.id.infoOnMenu);
        bottom_nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.authorOnMenu:
                        startActivity(new Intent(InfoActivity.this, AuthorsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.booksOnMenu:
                        startActivity(new Intent(InfoActivity.this, BooksActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.homeOnMenu:
                        startActivity(new Intent(InfoActivity.this, HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.infoOnMenu:
                        return true;
                    default:return false;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d("tt", "onDestroyHomeInfo");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        doYouExit();
        //super.onBackPressed();
    }

    private void doYouExit() {
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
        builderDialog.setMessage("Do you want to exit application?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User.closeSeance(InfoActivity.this);
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                         /*finish();
                                System.exit(0);*/
                        finishAffinity();
                    }
                }.start();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setTitle("Choose").show();
    }
}