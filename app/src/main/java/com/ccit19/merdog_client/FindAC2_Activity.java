package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.ccit19.merdog_client.databinding.ActivityFindAc2Binding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindAC2_Activity extends AppCompatActivity {
ActivityFindAc2Binding binding;
    String findid="";
    String kakao_id="";
    String naver_id="";
    String url_ = urlset.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_find_ac2_);
        binding.setActivity(this);
        setSupportActionBar(binding.fa2Toolbar);

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle("비밀번호 재설정");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);



        this.SetListener();
    }
    //버튼 클릭 이벤트
    public void SetListener(){
        View.OnClickListener  Listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.fa_btn_findid:
                        String find_url = url_ + "/userapp/find_id";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        // Create request
                        StringRequest sendMSGForm = new StringRequest(Request.Method.POST, find_url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        boolean success = false;
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            success = jsonObject.getBoolean("result");
                                            if (success) {
                                                findid=jsonObject.getString("user_id");
                                                kakao_id=jsonObject.getString("user_kakao");
                                                naver_id=jsonObject.getString("user_naver");

                                                AlertDialog.Builder builder = new AlertDialog.Builder(FindAC2_Activity.this);
                                                builder.setMessage("ID:"+findid+"\nkakao: "+kakao_id.toString()+"\nnaver: "+naver_id.toString())
                                                        .setTitle(R.string.id);
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id)
                                                    {
                                                        Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(FindAC2_Activity.this);
                                                builder.setMessage("입력하신 내역에 일치하는 계정이 없습니다.")
                                                        .setTitle("비밀번호 재설정 실패");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id)
                                                    {

                                                    }
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                                } else if (error instanceof ParseError) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        {
                            @Override
                            public  Map<String, String> getParams() throws AuthFailureError
                            {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("user_name", binding.faEdtName.getText().toString());
                                params.put("user_phone", binding.faEdtPhone1.getText().toString());
                                return params;
                            }
                        };
                        sendMSGForm.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(sendMSGForm);
                        break;
                    case R.id.fa_btn_editpass:
                        String url = url_ + "/userapp/find_pw";
                        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
                        // Create request
                        StringRequest edpassForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        boolean success = false;
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            success = jsonObject.getBoolean("result");
                                            if (success) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(FindAC2_Activity.this);
                                                builder.setMessage("입력하신 문자메세지를 확인해 주세요.")
                                                        .setTitle("비밀번호 재설정 완료");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id)
                                                    {
                                                        finish();
                                                    }
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(FindAC2_Activity.this);
                                                builder.setMessage("입력하신 내역에 일치하는 계정이 없습니다.")
                                                        .setTitle("비밀번호 재설정 실패");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id)
                                                    {

                                                    }
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                                } else if (error instanceof ParseError) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        {
                            @Override
                            public  Map<String, String> getParams() throws AuthFailureError
                            {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("user_id", binding.faEdtId.getText().toString());
                                params.put("user_phone", binding.faEdtPhone2.getText().toString());
                                return params;
                            }
                        };
                        edpassForm.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue2.add(edpassForm);
                        break;
                }
            }

        };
        binding.faBtnFindid.setOnClickListener(Listener);
        binding.faBtnEditpass.setOnClickListener(Listener);
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
