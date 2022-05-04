package com.example.livelibtestapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class AboutBookActivity extends AppCompatActivity {

    private TextView txtBookName, txtBookGenre, txtAboutBook, txtPostBook, txtIntBook;
    private ImageView imgBook;
    private LinearLayout llIntFact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_book);

        imgBook = findViewById(R.id.imgBook);
        txtBookName = findViewById(R.id.txtBookName);
        txtBookGenre = findViewById(R.id.txtBookGenre);
        txtAboutBook = findViewById(R.id.txtAboutBook);
        txtPostBook = findViewById(R.id.txtPostBook);
        txtIntBook = findViewById(R.id.txtIntBook);
        llIntFact = findViewById(R.id.llIntFact);


        //Bundle bundle = intent.getBundleExtra("book_bundle");
        //Book book = bundle.getParcelable("book_intent_item");
        Book book = (Book) getIntent().getSerializableExtra("book_data");

        Glide.with(imgBook.getContext()).load(book.getImg_res()).into(imgBook);
        txtBookName.setText(book.getName_book());
        txtBookGenre.setText(book.getGenre());
        txtAboutBook.setText(book.getAbout_book());
        txtPostBook.setText(book.getPost());

        if (book.getInt_fact().equals("отсутств")){
            llIntFact.setVisibility(View.GONE);
        } else {
            llIntFact.setVisibility(View.VISIBLE);
            txtIntBook.setText(book.getInt_fact());
        }

    }


}