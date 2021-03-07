package com.ccit19.merdog_client.backServ.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.ccit19.merdog_client.R;
import com.ccit19.merdog_client.backServ.urlset;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;
    String url_ = urlset.getInstance().getData();

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public  void remove(int pos){
        this.messages.remove(pos);
        notifyDataSetChanged();
    }


    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.getMemberData().equals("system")){//시스템 메세지일때
            convertView = messageInflater.inflate(R.layout.sys_message, null);
            holder.messageBody=convertView.findViewById(R.id.sm_Txv_msg);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        }else if(message.getDate().equals("send")){//전송중인 메세지 일때
            if (message.getText().contains("/storage")){
                convertView = messageInflater.inflate(R.layout.my_message_img, null);
                holder.imageBody=convertView.findViewById(R.id.message_body);
                holder.msgtime = convertView.findViewById(R.id.stimeView);
                holder.sending = convertView.findViewById(R.id.mm_imv_sending);
                convertView.setTag(holder);
                holder.msgtime.setVisibility(View.GONE);
                holder.sending.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getText())
                        .override(1000, 1000)
                        .placeholder(R.drawable.ic_perm_identity_24px)
                        .error(R.drawable.ic_perm_identity_24px)
                        .into(holder.imageBody);
            }else {
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody=convertView.findViewById(R.id.message_body);
                holder.msgtime = convertView.findViewById(R.id.stimeView);
                holder.sending = convertView.findViewById(R.id.mm_imv_sending);
                convertView.setTag(holder);
                holder.msgtime.setVisibility(View.GONE);
                holder.sending.setVisibility(View.VISIBLE);
                holder.messageBody.setText(message.getText());
            }

        }else if(message.getDate().equals("fail")){
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody=convertView.findViewById(R.id.message_body);
            holder.msgtime = convertView.findViewById(R.id.stimeView);
            holder.fail = convertView.findViewById(R.id.mm_imv_fail);
            convertView.setTag(holder);
            holder.msgtime.setVisibility(View.GONE);
            holder.fail.setVisibility(View.VISIBLE);
            holder.messageBody.setText(message.getText());
        }else {
            if (message.isBelongsToCurrentUser()) { //내가 보낸 메세지 일때
                if (message.getText().contains(url_ + "/storage/img/chat")){ // 이미지 이면
                    convertView = messageInflater.inflate(R.layout.my_message_img, null);

                    convertView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    holder.imageBody = (ImageView) convertView.findViewById(R.id.message_body);
                    holder.msgtime = convertView.findViewById(R.id.stimeView);
                    if (i+1<messages.size()){
                        if (messages.get(i+1).getDate().equals(message.getDate())&&messages.get(i+1).getMemberData().equals(message.getMemberData())){
                            holder.msgtime.setVisibility(View.GONE);
                        }else {
                            holder.msgtime.setVisibility(View.VISIBLE);
                        }
                    }else {
                        holder.msgtime.setVisibility(View.VISIBLE);
                    }
                    convertView.setTag(holder);
                    Glide.with(context).load(message.getText())
                            .override(1000, 1000)
                            .placeholder(R.drawable.ic_perm_identity_24px)
                            .error(R.drawable.ic_perm_identity_24px)
                            .into(holder.imageBody);

                    //SimpleDateFormat format2 = new SimpleDateFormat ( "HH시mm분");
                    try {
                        Date m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(message.getDate());
                        SimpleDateFormat format2 = new SimpleDateFormat("a h:mm");
                        holder.msgtime.setText(format2.format(m_date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else { //이미지가 아니면
                    convertView = messageInflater.inflate(R.layout.my_message, null);

                    convertView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                    holder.msgtime = convertView.findViewById(R.id.stimeView);
                    if (i+1<messages.size()){
                        if (messages.get(i+1).getDate().equals(message.getDate())&&messages.get(i+1).getMemberData().equals(message.getMemberData())){
                            holder.msgtime.setVisibility(View.GONE);
                        }else {
                            holder.msgtime.setVisibility(View.VISIBLE);
                        }
                    }else {
                        holder.msgtime.setVisibility(View.VISIBLE);
                    }
                    convertView.setTag(holder);
                    holder.messageBody.setText(message.getText());

                    //SimpleDateFormat format2 = new SimpleDateFormat ( "HH시mm분");
                    try {
                        Date m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(message.getDate());
                        SimpleDateFormat format2 = new SimpleDateFormat("a h:mm");
                        holder.msgtime.setText(format2.format(m_date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {                                 //의사가 보낸 메세지 일때
                if (message.getText().contains(url_ + "/storage/img/chat")){//이미지 이면
                    convertView = messageInflater.inflate(R.layout.their_message_img, null);
                    convertView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    holder.avatar = (View) convertView.findViewById(R.id.avatar);
                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    holder.imageBody = (ImageView) convertView.findViewById(R.id.message_body);
                    holder.msgtime=convertView.findViewById(R.id.gtimeView);
                    if (i+1<messages.size()){
                        if (messages.get(i+1).getDate().equals(message.getDate())&&messages.get(i+1).getMemberData().equals(message.getMemberData())){
                            holder.msgtime.setVisibility(View.GONE);
                        }else {
                            holder.msgtime.setVisibility(View.VISIBLE);
                        }
                    }else {
                        holder.msgtime.setVisibility(View.VISIBLE);
                    }
                    if (i>0){
                        if (messages.get(i-1).getMemberData().equals(message.getMemberData())&&messages.get(i-1).getDate().equals(message.getDate())){
                            holder.name.setVisibility(View.GONE);
                            holder.avatar.setVisibility(View.INVISIBLE);
                        }else {
                            holder.name.setVisibility(View.VISIBLE);
                            holder.avatar.setVisibility(View.VISIBLE);
                        }
                    }else {
                        holder.name.setVisibility(View.VISIBLE);
                        holder.avatar.setVisibility(View.VISIBLE);
                    }

                    convertView.setTag(holder);

                    holder.name.setText(message.getMemberData());
                    Glide.with(context).load(message.getText())
                            .override(1000, 1000)
                            .placeholder(R.drawable.ic_perm_identity_24px)
                            .error(R.drawable.ic_perm_identity_24px)
                            .into(holder.imageBody);

                    //SimpleDateFormat format2 = new SimpleDateFormat ( "HH시mm분");
                    try {
                        Date m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(message.getDate());
                        SimpleDateFormat format2 = new SimpleDateFormat("a h:mm");
                        holder.msgtime.setText(format2.format(m_date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
                    //drawable.setColor(Color.parseColor(message.getMemberData().getColor()));
                }else {//이미지가 아니면
                    convertView = messageInflater.inflate(R.layout.their_message, null);
                    convertView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    holder.avatar = (View) convertView.findViewById(R.id.avatar);
                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                    holder.msgtime=convertView.findViewById(R.id.gtimeView);
                    if (i+1<messages.size()){
                        if (messages.get(i+1).getDate().equals(message.getDate())&&messages.get(i+1).getMemberData().equals(message.getMemberData())){
                            holder.msgtime.setVisibility(View.GONE);
                        }else {
                            holder.msgtime.setVisibility(View.VISIBLE);
                        }
                    }else {
                        holder.msgtime.setVisibility(View.VISIBLE);
                    }
                    if (i>0){
                        if (messages.get(i-1).getMemberData().equals(message.getMemberData())&&messages.get(i-1).getDate().equals(message.getDate())){
                            holder.name.setVisibility(View.GONE);
                            holder.avatar.setVisibility(View.INVISIBLE);
                        }else {
                            holder.name.setVisibility(View.VISIBLE);
                            holder.avatar.setVisibility(View.VISIBLE);
                        }
                    }else {
                        holder.name.setVisibility(View.VISIBLE);
                        holder.avatar.setVisibility(View.VISIBLE);
                    }

                    convertView.setTag(holder);

                    holder.name.setText(message.getMemberData());
                    holder.messageBody.setText(message.getText());
                    //SimpleDateFormat format2 = new SimpleDateFormat ( "HH시mm분");
                    try {
                        Date m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(message.getDate());
                        SimpleDateFormat format2 = new SimpleDateFormat("a h:mm");
                        holder.msgtime.setText(format2.format(m_date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
                    //drawable.setColor(Color.parseColor(message.getMemberData().getColor()));
                }
            }
        }
        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
    public ImageView imageBody;
    public TextView msgtime;
    public ImageView sending;
    public ImageView fail;
}