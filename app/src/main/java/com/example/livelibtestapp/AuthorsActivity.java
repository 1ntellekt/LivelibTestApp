package com.example.livelibtestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
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
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kevincodes.recyclerview.ItemDecorator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorsActivity extends AppCompatActivity {

    private Author_Adapter authorAdapter;
    private List <Author> authorList;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddAuthor;
    private SearchView searchView;
    private BottomNavigationView bottomNavigationView;
    private static final String url_Authors ="http://192.168.43.163/authors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddAuthor = findViewById(R.id.btnAddAuthor);
        searchView = findViewById(R.id.searchView);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setSelectedItemId(R.id.authorOnMenu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = Volley.newRequestQueue(this);
        authorList = new ArrayList<>();
        authorAdapter = new Author_Adapter(authorList, AuthorsActivity.this);
        recyclerView.setAdapter(authorAdapter);
        allAuthorsFromUrl();

        btnAddAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateUpdateAuthorMenu(null,0);
            }
        });

        authorAdapter.setOnAuthorClickListener(new AuthorClickListener() {
            @Override
            public void onAuthorClick(int position) {
               // Toast.makeText(AuthorsActivity.this, "Clicked id:"+position, Toast.LENGTH_SHORT).show();
                showCreateUpdateAuthorMenu(authorList.get(position),position);
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                    if(direction==ItemTouchHelper.RIGHT){
                        Author author = authorList.get(position);
                        deleteAuthor(author.getId(),position);
                        Snackbar.make(recyclerView,"Deleted author: "+author.getFull_Name(), Snackbar.LENGTH_LONG)
                                .setAction("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        HashMap<String,String> data = new HashMap<String, String>();
                                        data.put("full_name",author.getFull_Name());
                                        data.put("biography", author.getBiography());
                                        data.put("img_res", author.getImg_res());
                                        addNewAuthor(data);
                                    }
                                }).show();
                    }


            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int colorRed = ContextCompat.getColor(AuthorsActivity.this, R.color.red);
                int whiteColor = ContextCompat.getColor(AuthorsActivity.this, R.color.white);
                int greenColor = ContextCompat.getColor(AuthorsActivity.this,R.color.green);

                new ItemDecorator.Builder(c, recyclerView, viewHolder, dX, actionState)
                        .setFromStartToEndBgColor(colorRed)
                        .setFromStartToEndIcon(R.drawable.ic_delete_24)
                        .setFromStartToEndIconTint(whiteColor)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSearch(newText);
                return true;
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.booksOnMenu:
                        startActivity(new Intent(AuthorsActivity.this, BooksActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.infoOnMenu:
                        startActivity(new Intent(AuthorsActivity.this, InfoActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.homeOnMenu:
                        startActivity(new Intent(AuthorsActivity.this, HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.authorOnMenu:
                        return true;
                    default:return false;
                }
            }
        });

    }

    private void filterSearch(String newText) {
        List<Author> filteredSearchList = new ArrayList<>();
        for (Author author: authorList){
            if (author.getFull_Name().toLowerCase().contains(newText.toLowerCase())){
                filteredSearchList.add(author);
            }
        }
        authorAdapter.filterListSearch(filteredSearchList);
    }

    private void showCreateUpdateAuthorMenu(Author author, int position) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_author_menu,null);
        TextView txtAuthorName, edit_create_TxtView;
        EditText txtLinkImg, txtEditAuthorName, txtEditAuthorBiography;
        ImageView imgAuthor;
        ImageButton btnCheckImg;

        txtAuthorName = view.findViewById(R.id.txtAuthorName);
        txtLinkImg = view.findViewById(R.id.txtLinkImg);
        txtEditAuthorName = view.findViewById(R.id.txtEditAuthorName);
        txtEditAuthorBiography = view.findViewById(R.id.txtEditAuthorBiography);
        imgAuthor = view.findViewById(R.id.imgAuthor);
        btnCheckImg = view.findViewById(R.id.btnCheckImg);
        edit_create_TxtView = view.findViewById(R.id.edit_create_TxtView);

        if (author!=null){
            txtAuthorName.setVisibility(View.VISIBLE);
            txtAuthorName.setText(author.getFull_Name()+" id:"+author.getId());
            txtLinkImg.setText(author.getImg_res());
            txtEditAuthorName.setText(author.getFull_Name());
            txtEditAuthorBiography.setText(author.getBiography());
            Glide.with(imgAuthor.getContext()).load(author.getImg_res()).into(imgAuthor);
            edit_create_TxtView.setText("Edit author menu");
        } else {
            txtAuthorName.setVisibility(View.GONE);
            edit_create_TxtView.setText("Create author menu");
        }

        btnCheckImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_img =  txtLinkImg.getText().toString();
                Glide.with(imgAuthor.getContext()).load(url_img).into(imgAuthor);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(AuthorsActivity.this);
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton(author!=null?"Update":"Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(txtLinkImg.getText().toString().equals(""))
                Toast.makeText(AuthorsActivity.this, "Image url is null!", Toast.LENGTH_SHORT).show();
                else if (txtEditAuthorName.getText().toString().equals(""))
                Toast.makeText(AuthorsActivity.this, "Author name is null!", Toast.LENGTH_SHORT).show();
                else if (txtEditAuthorBiography.getText().toString().equals(""))
                    Toast.makeText(AuthorsActivity.this, "Author biography is null!", Toast.LENGTH_SHORT).show();
                else {
                    Map<String,String> data = new HashMap<String, String>();
                    data.put("full_name",txtEditAuthorName.getText().toString());
                    data.put("biography",txtEditAuthorBiography.getText().toString());
                    data.put("img_res",txtLinkImg.getText().toString());
                    if(author!=null)
                    updateAuthor(data,author.getId(),position);
                    else addNewAuthor(data);
                }
            }
        }).show();
    }

    private void allAuthorsFromUrl() {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url_Authors, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("authors");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Author author = new Author(
                                jsonObject.getInt("id_author"),
                                jsonObject.getString("full_name"),
                                jsonObject.getString("biography"),
                                jsonObject.getString("img_res")
                        );
                        authorList.add(author);
                    }
                    authorAdapter.notifyDataSetChanged();
                    //Toast.makeText(AuthorsActivity.this, "Loaded:"+authorList.size(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AuthorsActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void addNewAuthor(Map<String,String> params){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_Authors, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                      Toast.makeText(AuthorsActivity.this, "AddingResponse:Inserted!", Toast.LENGTH_LONG).show();
                      Author author = new Author(
                              jsonObject.getInt("post_id"),
                              params.get("full_name"),
                              params.get("biography"),
                              params.get("img_res")
                      );
                      authorList.add(author);
                      authorAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AuthorsActivity.this, "AddingResponse:Fail Inserted!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            Toast.makeText(AuthorsActivity.this, "errorAdding:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateAuthor(Map<String,String> params, int id_update, int position){
        String url = url_Authors+"/"+id_update;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")){
                        Toast.makeText(AuthorsActivity.this, "UpdateResponse:Updated!", Toast.LENGTH_LONG).show();
                        authorList.set(position,new Author(
                                id_update,
                                params.get("full_name"),
                                params.get("biography"),
                                params.get("img_res"))
                        );
                        //authorAdapter.notifyDataSetChanged();
                         authorAdapter.notifyItemChanged(position);
                    } else {
                        Toast.makeText(AuthorsActivity.this, "UpdateResponse:Fail Updated!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            Toast.makeText(AuthorsActivity.this, "errorUpdate:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void deleteAuthor(int id_delete, int position){
        String url = url_Authors+"/"+id_delete;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        Toast.makeText(AuthorsActivity.this, "DeleteResponse:Deleted!", Toast.LENGTH_LONG).show();
                        authorList.remove(position);
                        authorAdapter.notifyItemRemoved(position);
                        //authorAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AuthorsActivity.this, "DeleteResponse:Fail Deleted!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            Toast.makeText(AuthorsActivity.this, "errorDelete:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        Log.d("tt", "onDestroyHomeAuthors");
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
                        User.closeSeance(AuthorsActivity.this);
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

