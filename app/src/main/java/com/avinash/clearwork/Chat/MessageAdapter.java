package com.avinash.clearwork.Chat;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avinash.clearwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    ArrayList<MessageObject> messageList;

    public MessageAdapter(ArrayList<MessageObject> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        if(messageList.get(position).getSenderId().equals(messageList.get(position).getAppUserID())){
            holder.mLayoutRight.setVisibility(View.VISIBLE);
            holder.mLayoutLeft.setVisibility(View.GONE);

            holder.mMessageRight.setText(messageList.get(position).getMessage());
            holder.mSenderRight.setText(messageList.get(position).getSenderId());



            if(messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty())
                holder.mViewMediaRight.setVisibility(View.GONE);
            holder.mViewMediaRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList())
                            .setStartPosition(0)
                            .show();
                }
            });
        }else{

            holder.mLayoutRight.setVisibility(View.GONE);
            holder.mLayoutLeft.setVisibility(View.VISIBLE);
            holder.mMessage.setText(messageList.get(position).getMessage());
            holder.mSender.setText(messageList.get(position).getSenderId());



            if(messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty())
                holder.mViewMedia.setVisibility(View.GONE);
            holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList())
                            .setStartPosition(0)
                            .show();
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

     class MessageViewHolder extends RecyclerView.ViewHolder {


        TextView mMessage, mSender, mMessageRight, mSenderRight;
         LinearLayout mLayoutLeft, mLayoutRight;
         Button mViewMedia, mViewMediaRight;
         MessageViewHolder(View view){
            super(view);
            mLayoutLeft = view.findViewById(R.id.layoutLeft);
            mLayoutRight = view.findViewById(R.id.layoutRight);

            mMessage = view.findViewById(R.id.message);
            mSender = view.findViewById(R.id.sender);
            mViewMedia = view.findViewById(R.id.viewMedia);

            mMessageRight = view.findViewById(R.id.messageRight);
            mSenderRight = view.findViewById(R.id.senderRight);
            mViewMediaRight = view.findViewById(R.id.viewMediaRight);
        }
    }
}
