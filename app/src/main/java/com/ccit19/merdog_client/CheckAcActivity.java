package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.ccit19.merdog_client.databinding.ActivityCheckAcBinding;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckAcActivity extends AppCompatActivity {
ActivityCheckAcBinding binding;
    String saved_user_id;
    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_check_ac);
        binding.setActivity(this);
        saved_user_id=SaveSharedPreference.getUserID(getApplicationContext());
        setSupportActionBar(binding.caTollbar);

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle("계정 확인");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);
        int type=SaveSharedPreference.getLoginType(getApplicationContext());

        if(type==0){
            binding.cBtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!binding.cEdtPass.getText().toString().isEmpty()){
                        String url = url_ + "/userapp/login";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        /* Create request */
                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        boolean success= false;
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            success = jsonObject.getBoolean("result");
                                            if (success){
                                                Toast.makeText(getApplicationContext(),"인증되었습니다.",Toast.LENGTH_LONG).show();
                                                SaveSharedPreference.clearUserName(getApplicationContext());
                                                SaveSharedPreference.setUserInfo(getApplicationContext(), jsonObject.getString("user_num"),saved_user_id, 0, jsonObject.getString("token"));
                                                Intent intent=new Intent(getApplicationContext(), EditAC_Activity.class);
                                                startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
                                                finish();
                                            }else {
                                                Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(getApplicationContext(),"서버로부터 응답이 없습니다.",Toast.LENGTH_LONG).show();
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

                                params.put("user_id", SaveSharedPreference.getUserID(getApplicationContext()));
                                params.put("user_pw", binding.cEdtPass.getText().toString());
                                params.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
                                return params;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(request);
                    }else {
                        Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else if (type==1){
            Intent intent=new Intent(getApplicationContext(), EditAC_Activity.class);
            startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            finish();
        }else {
            Intent intent=new Intent(getApplicationContext(), EditAC_Activity.class);
            startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
            {
                // 해당 버튼을 눌렀을 때 적절한 액션을 넣는다.
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
