package com.ccit19.merdog_client.backServ;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ccit19.merdog_client.R;
import com.ccit19.merdog_client.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPetListAdapder extends RecyclerView.Adapter<MainPetListAdapder.ViewHolder> {

    private ArrayList<MainPetList> mData = null;
    Context context;
    private int checkedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pet_img;
        TextView pet_name;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            pet_img = itemView.findViewById(R.id.mpl_imv_petimg);
            pet_name = itemView.findViewById(R.id.mpl_Txv_petname);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                        checkedPosition = pos;
                        v.setBackground(ContextCompat.getDrawable(context, R.drawable.roundborder_hili));
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public MainPetListAdapder(Context context, ArrayList<MainPetList> list) {
        this.context = context;
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public MainPetListAdapder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.mainpet_list, parent, false);
        MainPetListAdapder.ViewHolder vh = new MainPetListAdapder.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MainPetListAdapder.ViewHolder holder, int position) {
        MainPetList text = mData.get(position);
        Glide.with(context).load(text.getPet_img())
                .override(200, 200)
                .placeholder(R.drawable.ic_perm_identity_24px)
                .error(R.drawable.ic_perm_identity_24px)
                .into(holder.pet_img);
        holder.pet_name.setText(text.getPet_name());
        if (checkedPosition == position) {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.roundborder_hili));
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.roundborder));
        }

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }
}