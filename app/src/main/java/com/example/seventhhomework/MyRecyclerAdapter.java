package com.example.seventhhomework;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyInnerViewHolder> {
    private ArrayList<String> name;
    private ArrayList<String> title;
    private ArrayList<String> url;
    private ArrayList<String> src;
    private Activity activity;

    public MyRecyclerAdapter(ArrayList<String> name, ArrayList<String> title, ArrayList<String> url,ArrayList<String> src, Activity activity) {
        this.name = name;
        this.title = title;
        this.url = url;
        this.src=src;
        this.activity = activity;
    }

    public void reSet(ArrayList<String> name, ArrayList<String> title, ArrayList<String> url,ArrayList<String> src) {
        this.name = name;
        this.title = title;
        this.url = url;
        this.src=src;
    }

    public void reFresh(){
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyRecyclerAdapter.MyInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item, parent, false);
        return new MyInnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.MyInnerViewHolder holder, int position) {
        holder.tvName.setText(name.get(position));
        holder.tvTitle.setText(title.get(position));
        if(src.get(position)!=null) {
            RequestOptions options =new RequestOptions()
                    .placeholder(R.drawable.android)
                    .error(R.drawable.picture_load_fail);
            Glide.with(activity).load(Uri.parse(src.get(position))).apply(options).into(holder.ivPicture);
        }else{
            Glide.with(activity).load(R.drawable.android).into(holder.ivPicture);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("rvSize",""+title.size());
        return title.size();
    }

    public class MyInnerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvName;
        private ImageView ivPicture;

        public MyInnerViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvTitle = itemView.findViewById(R.id.title);
            ivPicture = itemView.findViewById(R.id.picture);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                   openSrc();
                }
            });
        }
        public void openSrc() {
            Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url.get(getAdapterPosition())));
            activity.startActivity(intent);
        }
    }
}
