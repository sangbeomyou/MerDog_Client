package com.ccit19.merdog_client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BillingAdapter extends BaseAdapter {
    Context context;
    List<Billing> billingList =new ArrayList<Billing>();

    public BillingAdapter(Context context){
        this.context = context;
    }

    //출력할 총갯수를 결정하는 메소드
    @Override
    public int getCount(){
        return billingList.size();
    }
    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return billingList.get(i);
    }

    //아이템별 아이디를 반환하는 메소드
    @Override
    public long getItemId(int i) {
        return i;
    }

    //가장 중요한 부분
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.billing_item, null);
        Billing billing = billingList.get(i);
        //뷰에 다음 컴포넌트들을 연결시켜줌
        TextView product_id = v.findViewById(R.id.product_id2);
        TextView payment_id = v.findViewById(R.id.payment_id);
//        TextView method = v.findViewById(R.id.method);
        TextView state = v.findViewById(R.id.state);
        TextView date = v.findViewById(R.id.date);

        product_id.setText(billingList.get(i).getproduct_id());
        payment_id.setText(billingList.get(i).getpayment_id());
//        method.setText(billingList.get(i).getmethod());
        state.setText(billingList.get(i).getstate());
        date.setText(billingList.get(i).getdate());


        //이렇게하면 findViewWithTag를 쓸 수 있음 없어도 되는 문장임

        //만든뷰를 반환함
        return v;
    }

    public void add(Billing billing) {
        this.billingList.add(billing);
        notifyDataSetChanged();
    }
}
