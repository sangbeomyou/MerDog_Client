package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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
import com.ccit19.merdog_client.backServ.AppController;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityMatchChatBinding;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MatchChatActivity extends AppCompatActivity {
ActivityMatchChatBinding binding;
    private static final int COUNT_DOWN_INTERVAL = 1000;

    private int count = 1;
    private CountDownTimer countDownTimer;
    String request_id;
    String request_time;
    String url_ = urlset.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_chat);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_match_chat);
        binding.setActivity(this);

        request_id= getIntent().getStringExtra("chat_request_id");
        request_time= getIntent().getStringExtra("time");

        countDownTimer(Integer.parseInt(request_time)*1000);
        countDownTimer.start();
        binding.bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = url_ + "/chat/cancel";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                // Create request
                StringRequest chatcancelForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                boolean success = false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("result");
                                    if (success){
                                        Toast.makeText(getApplicationContext(),"매칭이 취소되었습니다.",Toast.LENGTH_LONG).show();
                                        countDownTimer.cancel();
                                        finish();
                                    }else {
                                        Toast.makeText(getApplicationContext(),"실패하였습니다.",Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"서버오류입니다.\n잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                })
                {
                    @Override
                    public  Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("chat_request_id", getIntent().getStringExtra("chat_request_id"));
                        return params;
                    }
                };
                chatcancelForm.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(chatcancelForm);
            }
        });
    }

    public void countDownTimer(int time) {
        countDownTimer = new CountDownTimer(time, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                count++;

                String url = url_ + "chat/request";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                // Create request
                StringRequest chatcancelForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                boolean success = false;
                                boolean result = false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("state");

                                    if (success){
                                        Toast.makeText(getApplicationContext(),"매칭을 성공하였습니다.",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                        intent.putExtra("chat_room",jsonObject.getString("chat_room"));
                                        intent.putExtra("chat_request_id",request_id);
                                        intent.putExtra("pet_name", jsonObject.getString("pet_name"));
                                       // intent.putExtra("pet_name",response.getString("pet_name"));
                                        startActivity(intent);
                                        countDownTimer.cancel();
                                        finish();
                                    }else {
                                        result=jsonObject.getBoolean("result");
                                        if (result){
                                            Toast.makeText(getApplicationContext(),"매칭중 입니다.",Toast.LENGTH_LONG).show();
                                        }else {
                                            binding.textView5.setText("매칭에 실패하였습니다.\n주변에 응답 가능한 의사가 없습니다.");
                                            binding.textView5.setTextSize(25);
                                            Toast.makeText(getApplicationContext(),"매칭을 실패하였습니다.",Toast.LENGTH_LONG).show();
                                            binding.progressBar2.setVisibility(View.GONE);
                                            binding.imageView.setVisibility(View.VISIBLE);
                                            countDownTimer.cancel();
                                            binding.bCancel.setVisibility(View.GONE);
                                            binding.bBack.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"서버오류입니다.\n잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                })
                {
                    @Override
                    public  Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("chat_request_id",request_id);
                        params.put("count", String.valueOf(count));
                        return params;
                    }
                };
                chatcancelForm.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(chatcancelForm);
                countDownTimer.start();
            }
        };
    }
    @Override
    public void onStop() {
        super.onStop();
        finish();
        countDownTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
        }
        countDownTimer = null;
    }
    @Override
    public void onBackPressed() {
        if (binding.bBack.getVisibility()==View.VISIBLE){
            super.onBackPressed();
        }
        //super.onBackPressed();
    }
}
