package com.example.livelibtestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import papaya.in.sendmail.SendMail;

public class RecovPasswordActivity extends AppCompatActivity {

    private TextView txtAccess,txtChangeEdit;
    private EditText txtNickName,txtPersonEmail,txtPersonCode, txtNewPass, txtNewPass_second;
    private AppCompatButton btnSend;
    private boolean remNickName = true;
    private int generatedCode = 0;
    private int user_id = 0;
    private boolean isCodeSend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recov_password);
        initElements();

        txtAccess.setVisibility(View.GONE);
        txtPersonEmail.setVisibility(View.GONE);
        txtNewPass.setVisibility(View.GONE);
        txtPersonCode.setVisibility(View.GONE);
        txtNewPass_second.setVisibility(View.GONE);
        btnSend.setText("Send nickname");

        txtChangeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remNickName){
                    remNickName = false;
                    txtNickName.setVisibility(View.GONE);
                    txtPersonEmail.setVisibility(View.VISIBLE);
                    txtChangeEdit.setText("Forgot email?");
                    btnSend.setText("Send email");
                } else {
                    remNickName = true;
                    txtNickName.setVisibility(View.VISIBLE);
                    txtPersonEmail.setVisibility(View.GONE);
                    txtChangeEdit.setText("Forgot nickname?");
                    btnSend.setText("Send nickname");
                }

                isCodeSend = false;

                txtPersonCode.setText("");
                txtNickName.setText("");
                txtPersonEmail.setText("");
                txtNewPass.setText("");
                txtNewPass_second.setText("");

                txtAccess.setVisibility(View.GONE);
                txtPersonCode.setVisibility(View.GONE);
                txtNewPass.setVisibility(View.GONE);
                txtNewPass_second.setVisibility(View.GONE);

                txtPersonCode.setEnabled(true);
                txtNickName.setEnabled(true);
                txtPersonEmail.setEnabled(true);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCodeSend){
                    if (remNickName){
                        if (txtNickName.getText().toString().equals(""))
                            Toast.makeText(RecovPasswordActivity.this, "Nickname is null!", Toast.LENGTH_SHORT).show();
                        else {
                            //txtPersonCode.setVisibility(View.VISIBLE);
                            putGeneratedCode(txtNickName.getText().toString(),true);
                        }

                    } else {
                        if (txtPersonEmail.getText().toString().equals(""))
                            Toast.makeText(RecovPasswordActivity.this, "Email is null!", Toast.LENGTH_SHORT).show();
                        else {
                           // txtPersonCode.setVisibility(View.VISIBLE);
                            putGeneratedCode(txtPersonEmail.getText().toString(),false);
                        }
                    }
                } else {
                    if (txtPersonCode.getText().toString().equals(String.valueOf(generatedCode))){
                        if(!txtPersonCode.isEnabled()) {
                            if(!txtNewPass.getText().toString().equals(txtNewPass_second.getText().toString())&&txtNewPass.getText().toString().equals("")){
                                Toast.makeText(RecovPasswordActivity.this, "Different passwords!", Toast.LENGTH_SHORT).show();
                            } else {
                                changePassword(txtNewPass.getText().toString());
                            }
                        } else {
                            txtAccess.setVisibility(View.VISIBLE);
                            txtNewPass.setVisibility(View.VISIBLE);
                            txtNewPass_second.setVisibility(View.VISIBLE);
                            txtPersonCode.setEnabled(false);
                            if (remNickName) txtNickName.setEnabled(false);
                            else txtPersonEmail.setEnabled(false);
                            Toast.makeText(RecovPasswordActivity.this, "Code is success!", Toast.LENGTH_SHORT).show();
                            btnSend.setText("Send new password");
                        }

                    } else {
                        Toast.makeText(RecovPasswordActivity.this, "Code is fail!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void changePassword(String newPassword) {
        String url = "http://192.168.43.163/changepass/"+user_id;
        Map<String,String> params = new HashMap<String, String>();
        params.put("password", newPassword);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")){
                        Toast.makeText(RecovPasswordActivity.this, "Message:"+response.getString("message"), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             Toast.makeText(RecovPasswordActivity.this, "errorChPass:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(RecovPasswordActivity.this).add(jsonObjectRequest);
    }

    private void putGeneratedCode(String strInput, boolean isNickname) {
        String url = "http://192.168.43.163/recov";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        generatedCode = jsonObject.getInt("code");
                        user_id = jsonObject.getInt("id_user");
                        //txtPersonCode.setText(jsonObject.getString("code"));
                        txtPersonCode.setVisibility(View.VISIBLE);
                        isCodeSend = true;
                        btnSend.setText("Send code");
                        sendCodeOnEmail(jsonObject.getString("email"));
                    }
                    Toast.makeText(RecovPasswordActivity.this, "Message: "+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RecovPasswordActivity.this, "errorRecPass:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(isNickname?"login":"email",strInput);
                 return params;
            }
        };
        Volley.newRequestQueue(RecovPasswordActivity.this).add(stringRequest);
    }

    private void sendCodeOnEmail(String email) {
       try {
           new SendMail(
                   "1ntellektLognn123@gmail.com",
                   "19dinavi",
                   email,
                   "Testing Email Sending to Rec",
                   "Livelib generated code to change password: "+generatedCode
           ).execute();
       } catch (Exception e){
           e.printStackTrace();
           Toast.makeText(RecovPasswordActivity.this, "errorSendEmail:"+e.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }

    private void initElements() {
        txtAccess = findViewById(R.id.txtAccess);
        txtNickName = findViewById(R.id.txtNickName);
        txtPersonEmail = findViewById(R.id.txtPersonEmail);
        txtPersonCode = findViewById(R.id.txtPersonCode);
        txtNewPass = findViewById(R.id.txtNewPass);
        txtNewPass_second = findViewById(R.id.txtNewPass_second);
        btnSend = findViewById(R.id.btnSend);
        txtChangeEdit = findViewById(R.id.txtChangeEdit);
    }
}