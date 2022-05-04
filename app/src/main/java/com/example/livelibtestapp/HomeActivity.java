package com.example.livelibtestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private TextView txtWelcomeUser, txtEmail, txtPassword;
    private BottomNavigationView bottom_nav_view;
    private AppCompatButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtWelcomeUser = findViewById(R.id.txtWelcomeUser);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        bottom_nav_view = findViewById(R.id.bottom_nav_view);
        btnExit = findViewById(R.id.btnExit);

        if (User.getCurrUser().getSeanceId() == 0)
        User.openSeance(HomeActivity.this);

        bottom_nav_view.setSelectedItemId(R.id.homeOnMenu);

        bottom_nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.authorOnMenu:
                        startActivity(new Intent(HomeActivity.this, AuthorsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.booksOnMenu:
                        startActivity(new Intent(HomeActivity.this, BooksActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.infoOnMenu:
                        startActivity(new Intent(HomeActivity.this, InfoActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.homeOnMenu:
                        return true;
                    default:return false;
                }
            }
        });

        txtWelcomeUser.setText("Welcome,"+User.getCurrUser().getLogin()+"!");
        txtEmail.setText("Email:"+User.getCurrUser().getEmail());
        txtPassword.setText("Password:"+User.getCurrUser().getPassword());

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.closeSeance(HomeActivity.this);
                User.setCurrentUser(null);
                SharedPreferences sharedPreferences = getSharedPreferences("curr_user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user",null);
                editor.apply();
                startActivity(new Intent(HomeActivity.this,AuthActivity.class));
            }
        });
        Log.d("tt", "OnCreate");
    }

    @Override
    protected void onDestroy() {
        Log.d("tt", "onDestroyHomeA");
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
                        User.closeSeance(HomeActivity.this);
                        new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) { }
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