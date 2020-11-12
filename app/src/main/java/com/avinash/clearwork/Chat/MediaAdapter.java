package com.avinash.clearwork.Chat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avinash.clearwork.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewoHolder>{

    ArrayList<String> mediaList;
    Context context;

    public MediaAdapter(Context context, ArrayList<String> mediaList){

        this.context = context;
        this.mediaList = mediaList;

    }

    @NonNull
    @Override
    public MediaViewoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, null, false);
        MediaViewoHolder mediaViewoHolder = new MediaViewoHolder(layoutView);
        return mediaViewoHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewoHolder holder, int position) {
        Glide.with(context).load(Uri.parse(mediaList.get(position))).into(holder.mMedia);

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class MediaViewoHolder extends RecyclerView.ViewHolder{

        ImageView mMedia;

        public MediaViewoHolder(@NonNull View itemView) {
            super(itemView);
            mMedia = itemView.findViewById(R.id.media);
        }
    }
}
