package com.ccit19.merdog_client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {
    Context context;
    List<Product> productList =new ArrayList<Product>();

    public ProductAdapter(Context context){
        this.context = context;
    }

    //출력할 총갯수를 결정하는 메소드
    @Override
    public int getCount(){
        return productList.size();
    }
    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return productList.get(i);
    }

    //아이템별 아이디를 반환하는 메소드
    @Override
    public long getItemId(int i) {
        return i;
    }

    //가장 중요한 부분
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.product_item, null);
        Product product = productList.get(i);
        //뷰에 다음 컴포넌트들을 연결시켜줌
        TextView product_id = v.findViewById(R.id.product_id);
        TextView product_name = v.findViewById(R.id.product_name);
        TextView product_ticket = v.findViewById(R.id.product_ticket);
        ImageView product_img = v.findViewById(R.id.product_img);
        TextView product_price = v.findViewById(R.id.product_price);

        product_id.setText(productList.get(i).getProduct_id());
        product_name.setText(productList.get(i).getProduct_name());
        product_ticket.setText(productList.get(i).getProduct_ticket());
        product_price.setText(productList.get(i).getProduct_price());
        Glide.with(v).load(productList.get(i).getProduct_image())   //이미지
                .override(180, 180)
                .placeholder(R.drawable.ic_perm_identity_24px)
                .error(R.drawable.ic_perm_identity_24px)
                .into(product_img);

        //이렇게하면 findViewWithTag를 쓸 수 있음 없어도 되는 문장임

        //만든뷰를 반환함
        return v;
    }

    public void add(Product product) {
        this.productList.add(product);
        notifyDataSetChanged();
    }
}
