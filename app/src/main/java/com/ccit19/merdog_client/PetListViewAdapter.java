package com.ccit19.merdog_client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

public class PetListViewAdapter extends BaseAdapter {

    Context context;
    List<PetListViewItem> petList =new ArrayList<PetListViewItem>();

    public PetListViewAdapter(Context context){
        this.context = context;
    }

    //출력할 총갯수를 결정하는 메소드
    @Override
    public int getCount(){
        return petList.size();
    }
    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return petList.get(i);
    }

    //아이템별 아이디를 반환하는 메소드
    @Override
    public long getItemId(int i) {
        return i;
    }

    //가장 중요한 부분
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.petlistview_item, null);
        PetListViewItem petListViewItem = petList.get(i);
        //뷰에 다음 컴포넌트들을 연결시켜줌
        ImageView pet_img = (ImageView)v.findViewById(R.id.petimageview);
        TextView pet_name = (TextView)v.findViewById(R.id.pet_name);
        TextView pet_main_type = (TextView)v.findViewById(R.id.pet_kind);
        TextView pet_sub_type = (TextView)v.findViewById(R.id.pet_breed);
//        holder.pet_name = (TextView)v.findViewById(R.id.pet_name);
//        holder.pet_kind = (TextView)v.findViewById(R.id.pet_kind);
//        holder.pet_breed = (TextView)v.findViewById(R.id.pet_breed);
        Glide.with(context).load(petList.get(i).getPetImg())
                .override(200, 200)
                .placeholder(R.drawable.ic_perm_identity_24px)
                .error(R.drawable.ic_perm_identity_24px)
                .into(pet_img);
        pet_name.setText(petList.get(i).getPetName());
        pet_main_type.setText(petList.get(i).getPetMainType());
        pet_sub_type.setText(petList.get(i).getPetSubType());

        //이렇게하면 findViewWithTag를 쓸 수 있음 없어도 되는 문장임

        //만든뷰를 반환함
        return v;
    }

    public void add(PetListViewItem petListViewItem) {
        this.petList.add(petListViewItem);
        notifyDataSetChanged();
    }
}


