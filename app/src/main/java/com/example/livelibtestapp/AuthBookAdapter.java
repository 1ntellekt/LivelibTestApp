package com.example.livelibtestapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AuthBookAdapter extends ArrayAdapter<Author> {

    public AuthBookAdapter(Context context, List<Author> authorList){
        super(context,0,authorList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_author,parent,false);
        }
        ImageView imgAuthorBook = convertView.findViewById(R.id.imgAuthorBook);
        TextView txtViewAuthorName = convertView.findViewById(R.id.txtViewAuthorName);

        Author currAuthor = getItem(position);

        if (currAuthor!=null){
            Glide.with(getContext()).load(currAuthor.getImg_res()).into(imgAuthorBook);
            //Picasso.get().load(currAuthor.getImg_res()).fit().centerInside().into(imgAuthorBook);
            txtViewAuthorName.setText(currAuthor.getFull_Name());
        }
        return convertView;
    }

}
