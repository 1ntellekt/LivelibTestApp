package com.example.livelibtestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private EditText txtNickName, txtPassword, txtEmail, txtConfirmPassword;
    private AppCompatButton btnAutho;
    private RequestQueue requestQueue;
    private TextView txtToggle,titleSign, txtTimer, txtForgotPassword;
    private boolean isLogin = false;
    private ArrayList<String> loginList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        requestQueue = Volley.newRequestQueue(this);
        loginList = new ArrayList<>();

        txtNickName = findViewById(R.id.txtNickName);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtPassword = findViewById(R.id.txtPassword);
        txtEmail = findViewById(R.id.txtEmail);
        titleSign = findViewById(R.id.titleSign);
        btnAutho = findViewById(R.id.btnAutho);
        txtToggle = findViewById(R.id.txtToggle);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        txtTimer = findViewById(R.id.txtTimer);

        txtForgotPassword.setVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.GONE);
        titleSign.setText("Sign Up User");
        txtConfirmPassword.setVisibility(View.GONE);
        txtEmail.setVisibility(View.GONE);
        btnAutho.setText("Sign up");
        txtToggle.setText("Click to Log In");

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthActivity.this,RecovPasswordActivity.class));
            }
        });

        txtToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    clearAllEdits();
                    titleSign.setText("Log In User");
                    txtConfirmPassword.setVisibility(View.VISIBLE);
                    txtEmail.setVisibility(View.VISIBLE);
                    btnAutho.setText("Log in");
                    txtToggle.setText("Click to Sign Up");
                    txtForgotPassword.setVisibility(View.GONE);
                    isLogin = true;
                    txtTimer.setVisibility(View.GONE);
                } else {
                    clearAllEdits();
                    txtForgotPassword.setVisibility(View.VISIBLE);
                    titleSign.setText("Sign Up User");
                    txtConfirmPassword.setVisibility(View.GONE);
                    txtEmail.setVisibility(View.GONE);
                    btnAutho.setText("Sign up");
                    txtToggle.setText("Click to Log In");
                    txtTimer.setVisibility(View.GONE);
                    isLogin = false;
                }
            }
        });

        btnAutho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin){
                    if (txtNickName.getText().toString().equals("")){
                        Toast.makeText(AuthActivity.this, "Nickname is null!", Toast.LENGTH_SHORT).show();
                    } else if(txtPassword.getText().toString().equals("")){
                        Toast.makeText(AuthActivity.this, "Password is null!", Toast.LENGTH_SHORT).show();
                    } else {
                        sign_User(txtNickName.getText().toString(), txtPassword.getText().toString());
                    }

                } else {
                    if (txtNickName.getText().toString().equals("")){
                        Toast.makeText(AuthActivity.this, "Nickname is null!", Toast.LENGTH_SHORT).show();
                    } else if(txtEmail.getText().toString().equals("")){
                        Toast.makeText(AuthActivity.this, "Email is null!", Toast.LENGTH_SHORT).show();
                    } else if(txtPassword.getText().toString().equals("")){
                        Toast.makeText(AuthActivity.this, "Password is null!", Toast.LENGTH_SHORT).show();
                    } else if(!txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())){
                        Toast.makeText(AuthActivity.this, "Different passwords!", Toast.LENGTH_SHORT).show();
                    } else {
                        login_User(txtNickName.getText().toString(),txtEmail.getText().toString(),txtPassword.getText().toString());
                    }
                }

            }
        });

    }

    private void clearAllEdits() {
        txtNickName.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtConfirmPassword.setText("");
    }

    private void sign_User(String login, String password) {
        String url = "http://192.168.43.163/auth"+"/"+login+"/"+password;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")){
                        JSONObject jsonObject = response.getJSONObject("user");
                        User currUser = new User(
                            jsonObject.getInt("id"),
                            jsonObject.getString("login"),
                            jsonObject.getString("password"),
                            jsonObject.getString("email"),
                            Boolean.parseBoolean(jsonObject.getString("banned"))
                        );
                          User.setCurrentUser(currUser);
                            if (!response.getBoolean("ch_pass")){
                                saveToSharedPref(currUser);
                                startActivity(new Intent(AuthActivity.this,HomeActivity.class));
                            } else {
                                startActivity(new Intent(AuthActivity.this, ChangePassActivity.class));
                            }

                    } else {
                        if (response.getString("message").equals("Неправильно введен пароль!")){
                            checkToBanUser(login);
                        }
                    }
                    Toast.makeText(AuthActivity.this, "Message:"+response.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AuthActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void login_User(String login, String email, String password){
        String url = "http://192.168.43.163/log";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj.getBoolean("status")){
                        User currUser = new User(
                           jsonObj.getInt("post_id"),
                           login,
                           password,
                           email,
                           false
                        );
                        User.setCurrentUser(currUser);
                        saveToSharedPref(currUser);
                        startActivity(new Intent(AuthActivity.this,HomeActivity.class));
                    }
                 Toast.makeText(AuthActivity.this, "Message:"+jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             Toast.makeText(AuthActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("login",login);
                params.put("password",password);
                params.put("email",email);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void checkToBanUser(String login){
        loginList.add(login);
        int k=0;
        for (String cLogin:loginList){
            if (login.equals(cLogin)){
                k++;
            }
        }
        if (k==4){
            banUserByLogin(login);
        } else {
            setTimerErrorPassword(k);
        }

    }

    private void banUserByLogin(String login) {
        String url = "http://192.168.43.163/ban/"+login;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(AuthActivity.this, "Message:"+response.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AuthActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void setTimerErrorPassword(int k) {
        txtTimer.setVisibility(View.VISIBLE);
        txtNickName.setEnabled(false);
        txtPassword.setEnabled(false);
        btnAutho.setEnabled(false);
        txtToggle.setEnabled(false);
        CountDownTimer countDownTimer = new CountDownTimer(k*10000L,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Blocking input {"+k+"}: "+createTimeFromMilliseconds((int)millisUntilFinished));
            }

            @Override
            public void onFinish() {
            txtTimer.setVisibility(View.GONE);
            txtNickName.setEnabled(true);
            txtPassword.setEnabled(true);
            btnAutho.setEnabled(true);
            txtToggle.setEnabled(true);
            }
        };
        countDownTimer.start();
    }

    private String createTimeFromMilliseconds(int millisUntilFinished) {
        String time = "";
        int min = millisUntilFinished/1000/60;
        int sec = millisUntilFinished/1000%60;
        if(min<10){
            time+="0";
        }
        time+=min+":";
        if (sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }

    private void saveToSharedPref(User currUser) {
        Gson gson = new Gson();
        String json = gson.toJson(currUser);
        SharedPreferences sharedPreferences = getSharedPreferences("curr_user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user",json);
        editor.apply();
    }

}