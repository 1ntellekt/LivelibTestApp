package com.example.livelibtestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.kevincodes.recyclerview.ItemDecorator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BooksActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddBook;
    private List<Book> bookList;
    private Book_Adapter bookAdapter;
    private static final String url_Books = "http://192.168.43.163/books";
    private RequestQueue requestQueue;
    private List<Author> authorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddBook = findViewById(R.id.btnAddBook);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        requestQueue = Volley.newRequestQueue(this);

        authorList = new ArrayList<>();
        initAuthorListSpinner();

        bottomNavigationView.setSelectedItemId(R.id.booksOnMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.authorOnMenu:
                        startActivity(new Intent(BooksActivity.this, AuthorsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.infoOnMenu:
                        startActivity(new Intent(BooksActivity.this, InfoActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.homeOnMenu:
                        startActivity(new Intent(BooksActivity.this, HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.booksOnMenu:
                        return true;
                    default:return false;
                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<>();
        bookAdapter = new Book_Adapter(bookList,this);
        recyclerView.setAdapter(bookAdapter);

        getAllBooks();

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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction==ItemTouchHelper.RIGHT){
                    deleteBook(bookList.get(position).getId(),position);
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int redColor = ContextCompat.getColor(BooksActivity.this,R.color.red);
                int whiteColor = ContextCompat.getColor(BooksActivity.this,R.color.white);
                new ItemDecorator.Builder(c,recyclerView,viewHolder,dX,actionState)
                .setFromStartToEndBgColor(redColor)
                .setFromStartToEndIconTint(whiteColor)
                .setFromStartToEndIcon(R.drawable.ic_delete_24)
                .create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateEditMenuBook(null,0);
            }
        });

        bookAdapter.setOnBookClickListener(new BookClickListener() {
            @Override
            public void onBookClick(int position) {
                showCreateEditMenuBook(bookList.get(position),position);
            }
        });

        bookAdapter.setOnAboutBookClickListener(new AboutBookClickListener() {
            @Override
            public void onAboutBookClick(int position) {
                Book book = bookList.get(position);
                startActivity(new Intent(BooksActivity.this,AboutBookActivity.class)
                .putExtra("book_data",book)
                );
            }
        });

    }

    private void initAuthorListSpinner() {
       String url = "http://192.168.43.163/authors_names";
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               try {
                   JSONArray jsonArray = response.getJSONArray("authors_book");
                   for (int i=0; i<jsonArray.length(); i++){
                       JSONObject jsonObject = jsonArray.getJSONObject(i);
                       Author author = new Author();
                       author.setId(jsonObject.getInt("id_author"));
                       author.setFull_Name(jsonObject.getString("full_name"));
                       author.setImg_res(jsonObject.getString("img_res"));
                       authorList.add(author);
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
            Toast.makeText(BooksActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
           }
       });
       requestQueue.add(jsonObjectRequest);
    }

    int selected_author_id;
    private void showCreateEditMenuBook(Book book, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.edit_book_menu,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        TextView txtViewBN = view.findViewById(R.id.txtViewBN);
        TextView txtViewMenu = view.findViewById(R.id.txtViewMenu);
        Spinner spinnerAuthors = view.findViewById(R.id.spinnerAuthors);
        ImageButton btnCheckImg = view.findViewById(R.id.btnCheckImg);
        EditText editTxtUrlImg = view.findViewById(R.id.editTxtUrlImg);
        EditText txtEditBookName = view.findViewById(R.id.txtEditBookName);
        EditText txtEditDescription = view.findViewById(R.id.txtEditDescription);
        EditText txtEditGenre = view.findViewById(R.id.txtEditGenre);
        EditText txtEditAboutBook = view.findViewById(R.id.txtEditAboutBook);
        EditText txtEditPost = view.findViewById(R.id.txtEditPost);
        EditText txtEditIntFact = view.findViewById(R.id.txtEditIntFact);
        ImageView imgBook = view.findViewById(R.id.imgBook);

        AuthBookAdapter authBookAdapter = new AuthBookAdapter(this,authorList);
        spinnerAuthors.setAdapter(authBookAdapter);

        spinnerAuthors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Author selected_author = (Author) parent.getItemAtPosition(position);
                selected_author_id = selected_author.getId();
                //Toast.makeText(BooksActivity.this, "Selected:"+selected_author_id, Toast.LENGTH_SHORT).show();
                //Toast.makeText(BooksActivity.this, "Selected:"+position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (book!=null){
            txtViewMenu.setText("Edit Book Menu");
            txtViewBN.setVisibility(View.VISIBLE);
            Glide.with(imgBook.getContext()).load(book.getImg_res()).into(imgBook);
            editTxtUrlImg.setText(book.getImg_res());
            txtViewBN.setText(book.getName_book()+" id:"+book.getId());
            txtEditBookName.setText(book.getName_book());
            txtEditDescription.setText(book.getDescription());
            txtEditGenre.setText(book.getGenre());
            txtEditAboutBook.setText(book.getAbout_book());
            txtEditPost.setText(book.getPost());
            txtEditIntFact.setText(book.getInt_fact());
            spinnerAuthors.setSelection(getPosAuthor(book));
        } else {
            txtViewBN.setVisibility(View.GONE);
            txtViewMenu.setText("Create Book Menu");
        }

        btnCheckImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editTxtUrlImg.getText().toString();
                Glide.with(imgBook.getContext()).load(url).into(imgBook);
            }
        });

        builder.setPositiveButton(book==null?"Create":"Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String,String>params = new HashMap<String,String>();
                if (editTxtUrlImg.getText().toString().equals("")){
                   Toast.makeText(BooksActivity.this, "Book img_res is null!", Toast.LENGTH_SHORT).show();
                } else if(txtEditBookName.getText().toString().equals("")){
                    Toast.makeText(BooksActivity.this, "Book name is null!", Toast.LENGTH_SHORT).show();
                } else if (txtEditDescription.getText().toString().equals("")){
                    Toast.makeText(BooksActivity.this, "Book description is null!", Toast.LENGTH_SHORT).show();
                } else if (txtEditGenre.getText().toString().equals("")){
                    Toast.makeText(BooksActivity.this, "Book genre is null!", Toast.LENGTH_SHORT).show();
                } else if (txtEditAboutBook.getText().toString().equals("")){
                    Toast.makeText(BooksActivity.this, "About book is null!", Toast.LENGTH_SHORT).show();
                } else if (txtEditPost.getText().toString().equals("")){
                    Toast.makeText(BooksActivity.this, "Book post is null!", Toast.LENGTH_SHORT).show();
                } else if (txtEditIntFact.getText().toString().equals("")){
                    Toast.makeText(BooksActivity.this, "Book interest fact is null!", Toast.LENGTH_SHORT).show();
                } else {
                    params.put("name_book",txtEditBookName.getText().toString());
                    params.put("description",txtEditDescription.getText().toString());
                    params.put("image_res",editTxtUrlImg.getText().toString());
                    params.put("genre",txtEditGenre.getText().toString());
                    params.put("author_id",String.valueOf(selected_author_id));
                    params.put("about_book",txtEditAboutBook.getText().toString());
                    params.put("post",txtEditPost.getText().toString());
                    params.put("int_fact",txtEditIntFact.getText().toString());
                    if (book==null) addBook(params);
                    else updateBook(params,book.getId(),position);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    private int getPosAuthor(Book book) {
        int pos=0;
        for (Author currAuth:authorList)
        {
            if (currAuth.getId()==book.getId_author()) break;
            else pos++;
        }
        return pos;
    }

    private void filterSearch(String newText) {
        List<Book> filteredListBook = new ArrayList<>();
        for (Book currBook:bookList){
            if (currBook.getName_book().toLowerCase().contains(newText.toLowerCase())){
                filteredListBook.add(currBook);
            }
        }
        bookAdapter.filteredList(filteredListBook);
    }

    private void getAllBooks() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_Books, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("books");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Book book = new Book(
                                jsonObject.getInt("id_book"),
                                jsonObject.getString("name_book"),
                                jsonObject.getString("description"),
                                jsonObject.getString("image_res"),
                                jsonObject.getString("genre"),
                                jsonObject.getInt("author_id"),
                                jsonObject.getString("about_book"),
                                jsonObject.getString("post"),
                                jsonObject.getString("int_fact")
                        );
                        bookList.add(book);
                    }
                    bookAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             Toast.makeText(BooksActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void deleteBook(int id_delete, int position){
        String url = url_Books+"/"+id_delete;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        Toast.makeText(BooksActivity.this, "Book is delete!", Toast.LENGTH_LONG).show();
                        bookList.remove(position);
                        bookAdapter.notifyItemRemoved(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            Toast.makeText(BooksActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void addBook(Map<String,String> params){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_Books, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("status")){
                     Toast.makeText(BooksActivity.this, "Inserted post_id:"+jsonObject.getInt("post_id"), Toast.LENGTH_LONG).show();
                     Book book = new Book(
                             jsonObject.getInt("post_id"),
                             params.get("name_book"),
                             params.get("description"),
                             params.get("image_res"),
                             params.get("genre"),
                             Integer.parseInt(params.get("author_id")),
                             params.get("about_book"),
                             params.get("post"),
                             params.get("int_fact")
                     );
                     bookList.add(book);
                     bookAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(BooksActivity.this, "Fail Insert!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            Toast.makeText(BooksActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void updateBook(Map<String,String> params,int id_update, int position){
        String url = url_Books+"/"+id_update;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")){
                       Toast.makeText(BooksActivity.this, "UpdateResponse:Updated!", Toast.LENGTH_LONG).show();
                        bookList.set(position,new Book(
                                id_update,
                                params.get("name_book"),
                                params.get("description"),
                                params.get("image_res"),
                                params.get("genre"),
                                Integer.parseInt(params.get("author_id")),
                                params.get("about_book"),
                                params.get("post"),
                                params.get("int_fact")
                        ));
                        bookAdapter.notifyItemChanged(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(BooksActivity.this, "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String>headers = new HashMap<String,String>();
                headers.put("Content-Type","application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onDestroy() {
        Log.d("tt", "onDestroyHomeB");
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
                        User.closeSeance(BooksActivity.this);
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