package com.ccit19.merdog_client.backServ.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ccit19.merdog_client.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatListAdapter extends BaseAdapter {
    List<ChatList> chatLists = new ArrayList<ChatList>();
    Context context;

    public ChatListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatLists.size();
    }

    @Override
    public Object getItem(int position) {
        return chatLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatListViewHolder holder = new ChatListViewHolder();
        LayoutInflater chatlistInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ChatList chatList = chatLists.get(position);

        convertView = chatlistInflater.inflate(R.layout.chat_list, null);

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        holder.pet_img =convertView.findViewById(R.id.cl_Imgv_petimg);
        holder.pet_name=convertView.findViewById(R.id.cl_Txv_petname);
        holder.message=convertView.findViewById(R.id.cl_Txv_message);
        holder.date=convertView.findViewById(R.id.cl_Txv_date);
        convertView.setTag(holder);
        Glide.with(context).load(chatList.getPet_img())
                .override(200, 200)
                .placeholder(R.drawable.ic_perm_identity_24px)
                .error(R.drawable.ic_perm_identity_24px)
                .into(holder.pet_img);
        holder.pet_name.setText(chatList.getPet_name());
        holder.message.setText(chatList.getMessage());
        try {
            Date m_date =new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(chatList.getDate());
            SimpleDateFormat dayformat = new SimpleDateFormat ( "yyyy-MM-dd");
            if (dayformat.format(m_date).equals(dayformat.format(new Date()))){
                SimpleDateFormat format2 = new SimpleDateFormat ( "a h:mm");
                holder.date.setText(format2.format(m_date));
            }else {
                holder.date.setText(dayformat.format(m_date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String s=chatList.getChat_room();
        //holder.chat_room.setText(chatList.getChat_room());
        //SimpleDateFormat format2 = new SimpleDateFormat ( "HH시mm분");
        //Date time = new Date();
        //holder.msgtime.setText(format2.format(time));

        return convertView;
    }

    public void add(ChatList chatList) {
        this.chatLists.add(chatList);
        notifyDataSetChanged();
    }
}

class ChatListViewHolder {
    public ImageView pet_img;
    public TextView pet_name;
    public TextView message;
    public TextView date;
}