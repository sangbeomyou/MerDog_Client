package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.ccit19.merdog_client.databinding.ActivitySplashBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    Intent intent;
    AlertDialog dialog;
    String url_ = urlset.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        binding.setActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("0", "일반알림", NotificationManager.IMPORTANCE_DEFAULT);
            createNotificationChannel("1", "상담알림", NotificationManager.IMPORTANCE_HIGH);
            createNotificationChannel("2", "채팅알림", NotificationManager.IMPORTANCE_HIGH);
            createNotificationChannel("3", "결제알림", NotificationManager.IMPORTANCE_DEFAULT);
        }

        //setContentView(R.layout.activity_splash);
        if (SaveSharedPreference.getUserID(SplashActivity.this).length() == 0) {
            // call Login Activity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            String url = url_ + "/userapp/check_token";
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            // Create request
            StringRequest check_token = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            boolean success = false;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                success = jsonObject.getBoolean("result");
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "로그인되었습니다.", Toast.LENGTH_LONG).show();
                                    intent = new Intent(SplashActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "토큰이 만료되었습니다.\n다시 로그인해주세요.", Toast.LENGTH_LONG).show();
                                    SaveSharedPreference.clearUserName(getApplicationContext());
                                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                }
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        alert("서버로부터 응답이 없습니다.\n인터넷 연결을 확인해주세요.");
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        alert("서버오류입니다.\n잠시후에 다시 시도해주세요.");
                    } else if (error instanceof NetworkError) {
                        alert("인터넷 연결을 확인해주세요.");
                    } else if (error instanceof ParseError) {
                        alert("서버오류입니다.\n잠시후에 다시 시도해주세요.");
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            })
            {
                @Override
                public  Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("user_id", SaveSharedPreference.getUserIdx(SplashActivity.this));
                    params.put("token", SaveSharedPreference.getToken(SplashActivity.this));
                    return params;
                }
            };
            check_token.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(check_token);
            // Call Next Activity
        }
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000);
    }

    public void alert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(s);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void createNotificationChannel(String channel_id, String title, int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //String description = getString(R.string.des);
            NotificationChannel channel = new NotificationChannel(channel_id, title, importance);
            channel.enableLights(true);
            channel.setLightColor(Color.MAGENTA);
            if (channel_id.equals("2")) {
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{1000,
                        1000,
                        1000});
            }


            //channel.setDescription("알림");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private class splashhandler implements Runnable {
        public void run() {
            //startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            //finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialog!=null)
        dialog.dismiss();
    }
}
