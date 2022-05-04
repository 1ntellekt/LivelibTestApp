package com.example.livelibtestapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Book_Adapter extends RecyclerView.Adapter<Book_Adapter.BookHolder> {

    private List<Book> bookList;
    private Context context;
    private BookClickListener bookClickListener;
    private AboutBookClickListener aboutBookClickListener;

    public Book_Adapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    public void setOnBookClickListener(BookClickListener listener){
        bookClickListener = listener;
    }

    public void setOnAboutBookClickListener(AboutBookClickListener listener){
        aboutBookClickListener = listener;
    }

    public void filteredList(List<Book> filteredBookList){
        bookList = filteredBookList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookHolder(LayoutInflater.from(context).inflate(R.layout.item_book,parent,false),bookClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        Book book = bookList.get(position);
        holder.txtBookName.setText(book.getName_book());
        holder.txtGenre.setText(book.getGenre());
        holder.txtDescription.setText(book.getDescription());
        Glide.with(holder.imgBook.getContext()).load(book.getImg_res()).into(holder.imgBook);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }


    public class BookHolder extends RecyclerView.ViewHolder {
        public TextView txtBookName, txtGenre, txtDescription;
        public ImageView imgBook;
        public AppCompatButton btnAboutBook;
        public BookHolder(@NonNull View itemView, BookClickListener bookClickListener) {
            super(itemView);
            txtBookName = itemView.findViewById(R.id.txtBookName);
            txtGenre = itemView.findViewById(R.id.txtGenre);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgBook = itemView.findViewById(R.id.imgBook);
            btnAboutBook = itemView.findViewById(R.id.btnAboutBook);

            btnAboutBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aboutBookClickListener!=null){
                        int position = getAdapterPosition();
                        aboutBookClickListener.onAboutBookClick(position);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bookClickListener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                         bookClickListener.onBookClick(position);
                        }
                    }
                }
            });
        }
    }


}
