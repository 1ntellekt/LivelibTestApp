package com.example.livelibtestapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Author_Adapter extends RecyclerView.Adapter<Author_Adapter.AuthorHolder> {

    private List<Author> authorList;
    private AuthorClickListener authorClickListener;
    private Context context;

    public Author_Adapter(List<Author> authorList, Context context) {
        this.authorList = authorList;
        this.context = context;
    }

    public void setOnAuthorClickListener(AuthorClickListener listener){
        authorClickListener = listener;
    }
    public void filterListSearch(List<Author>filteredList){
        authorList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AuthorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AuthorHolder(LayoutInflater.from(context).inflate(R.layout.item_author,parent,false),authorClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull Author_Adapter.AuthorHolder holder, int position) {
        Author author = authorList.get(position);
        holder.authorNameTxt.setText(author.getFull_Name());
        holder.authorBiographyTxt.setText(author.getBiography());
        //Picasso.get().load(author.getImg_res()).fit().centerInside().into(holder.authorImgView);
        Glide.with(holder.authorImgView.getContext()).load(author.getImg_res()).into(holder.authorImgView);
    }
    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public class AuthorHolder extends RecyclerView.ViewHolder{
        public TextView authorNameTxt, authorBiographyTxt;
        public ImageView authorImgView;
        public AuthorHolder(@NonNull View itemView, AuthorClickListener authorClickListener) {
            super(itemView);
            authorBiographyTxt = itemView.findViewById(R.id.authorBiographyTxt);
            authorNameTxt = itemView.findViewById(R.id.authorNameTxt);
            authorImgView = itemView.findViewById(R.id.authorImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (authorClickListener!=null){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            authorClickListener.onAuthorClick(pos);
                        }
                    }
                }
            });
        }
    }

}
