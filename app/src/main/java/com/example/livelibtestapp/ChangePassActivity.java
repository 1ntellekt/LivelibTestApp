package com.example.livelibtestapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassActivity extends AppCompatActivity {

    private TextView titleSign;
    private EditText txtCurrPass, txtNewPass, txtNewPass_second;
    private AppCompatButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        titleSign = findViewById(R.id.titleSign);
        txtCurrPass = findViewById(R.id.txtCurrPass);
        txtNewPass = findViewById(R.id.txtNewPass);
        txtNewPass_second = findViewById(R.id.txtNewPass_second);
        btnSend = findViewById(R.id.btnSend);

        User.openSeance(ChangePassActivity.this);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtCurrPass.getText().toString().equals(""))
                    Toast.makeText(ChangePassActivity.this, "Current password is null!", Toast.LENGTH_SHORT).show();
                else if (!txtCurrPass.getText().toString().equals(String.valueOf(User.getCurrUser().getPassword())))
                    Toast.makeText(ChangePassActivity.this, "Wrong current password!", Toast.LENGTH_SHORT).show();
                else if (txtNewPass.getText().toString().equals(""))
                    Toast.makeText(ChangePassActivity.this, "New password is null!", Toast.LENGTH_SHORT).show();
                else if (!txtNewPass.getText().toString().equals(txtNewPass_second.getText().toString()))
                    Toast.makeText(ChangePassActivity.this, "Different passwords!", Toast.LENGTH_SHORT).show();
                else {
                    changePassword(txtNewPass.getText().toString());
                }
            }
        });

    }


    private void changePassword(String newPassword) {
        String url = "http://192.168.43.163/changepass/"+User.getCurrUser().getId();
        Map<String,String> params = new HashMap<String, String>();
        params.put("password", newPassword);
        params.put("id_seance",String.valueOf(User.getCurrUser().getSeanceId()));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")){
                        Toast.makeText(ChangePassActivity.this, "Message:"+response.getString("message"), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChangePassActivity.this, "errorChPass:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(ChangePassActivity.this).add(jsonObjectRequest);
    }

    @Override
    protected void onDestroy() {
        User.closeSeance(ChangePassActivity.this);
        super.onDestroy();
    }
}