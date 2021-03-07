package com.ccit19.merdog_client.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccit19.merdog_client.BillingInfoActivity;
import com.ccit19.merdog_client.CheckAcActivity;
import com.ccit19.merdog_client.LoginActivity;
import com.ccit19.merdog_client.PetListActivity;
import com.ccit19.merdog_client.PetRegisterActivity;
import com.ccit19.merdog_client.ProductActivity;
import com.ccit19.merdog_client.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ccit19.merdog_client.backServ.AppController;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    TextView setID, setPhone,  setNic ;
    Button logout, b_modify_user, b_petinfo, b_billinginfo, b_ticket, setting;
    String url_ = urlset.getInstance().getData();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        //final TextView textView = root.findViewById(R.id.text_notifications);


        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        setPhone = root.findViewById(R.id.setPhone);
        setNic = root.findViewById(R.id.setNic);

        //로그인할때 SaveSharedPreference 에 저장된 userid값을 받아옴
        setID=root.findViewById(R.id.setID);
        setID.setText(SaveSharedPreference.getUserID(getContext()).toString());

        //user_name, user_phone 값 서버요청
        String url = url_ + "/userapp/mypage";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        boolean success= false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success){
                                setPhone.setText(jsonObject.getString("user_phone"));
                                setNic.setText(jsonObject.getString("user_name"));
                            }else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(root.getContext(),"인터넷 연결을 확인해주세요."+error.getMessage(),Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(root.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(root.getContext(),"서버오류입니다.\n잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(root.getContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(root.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        })
        {
            @Override
            public  Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", SaveSharedPreference.getUserIdx(getContext()).toString()); //줌
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


        //반려견등록 액티비티호출
        b_petinfo = root.findViewById(R.id.b_petinfo);
        b_petinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), PetListActivity.class);
                startActivity(intent);
            }
        });

        //내정보수정 액티비티호출
        b_modify_user = root.findViewById(R.id.b_modify_user);
        b_modify_user.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), CheckAcActivity.class);
                startActivity(intent);
            }
        }));

        //결제페이지 액티비티호출
        b_billinginfo = root.findViewById(R.id.b_billinginfo);
        b_billinginfo.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), BillingInfoActivity.class);
                startActivity(intent);
            }
        }));

        //이용권페이지 액티비티 호출
        b_ticket = root.findViewById(R.id.b_ticket);
        b_ticket.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), ProductActivity.class);
                startActivity(intent);
            }
        }));

        setting=root.findViewById(R.id.b_setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                //intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                //intent.putExtra(Settings.EXTRA_CHANNEL_ID, myNotificationChannel.getId());
                startActivity(intent);
            }
        });

        //로그아웃 버튼
        logout=root.findViewById(R.id.b_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = url_ + "/userapp/logout";
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                boolean success= false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("result");
                                    if (success){
                                        Toast.makeText(root.getContext(),"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
                                        SaveSharedPreference.clearUserName(root.getContext());
                                        Intent intent=new Intent(root.getContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }else {
                                        Toast.makeText(root.getContext(),"로그아웃에 실패했습니다.\n잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(root.getContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(root.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(root.getContext(),"서버오류입니다.\n잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(root.getContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(root.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                })
                {
                    @Override
                    public  Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("user_id", SaveSharedPreference.getUserIdx(getContext()).toString()); //줌
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }
        });
        return root;
    }


}
