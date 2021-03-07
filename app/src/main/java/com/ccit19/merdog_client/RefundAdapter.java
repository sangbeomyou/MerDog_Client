package com.ccit19.merdog_client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RefundAdapter extends BaseAdapter {
    Context context;
    List<Refund> refundList =new ArrayList<Refund>();

    public RefundAdapter(Context context){
        this.context = context;
    }

    //출력할 총갯수를 결정하는 메소드
    @Override
    public int getCount(){
        return refundList.size();
    }
    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return refundList.get(i);
    }

    //아이템별 아이디를 반환하는 메소드
    @Override
    public long getItemId(int i) {
        return i;
    }

    //가장 중요한 부분
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.refund_item, null);
        Refund refund = refundList.get(i);
        //뷰에 다음 컴포넌트들을 연결시켜줌
        TextView refund_id = v.findViewById(R.id.refund_id);
        TextView r_payment_id = v.findViewById(R.id.r_payment_id);
        TextView order_id = v.findViewById(R.id.order_id);
        TextView refund_state = v.findViewById(R.id.refund_state);
        TextView refund_date = v.findViewById(R.id.refund_date);
        TextView comment = v.findViewById(R.id.comment);

        refund_id.setText(refundList.get(i).getRefund_id());
        r_payment_id.setText(refundList.get(i).getR_payment_id());
        order_id.setText(refundList.get(i).getOrder_id());
        refund_state.setText(refundList.get(i).getRefund_state());
        refund_date.setText(refundList.get(i).getRefund_date());
        comment.setText(refundList.get(i).getComment());


        //이렇게하면 findViewWithTag를 쓸 수 있음 없어도 되는 문장임

        //만든뷰를 반환함
        return v;
    }

    public void add(Refund refund) {
        this.refundList.add(refund);
        notifyDataSetChanged();
    }
}
