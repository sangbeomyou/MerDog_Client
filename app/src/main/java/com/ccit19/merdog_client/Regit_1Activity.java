package com.ccit19.merdog_client;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.ccit19.merdog_client.backServ.AppController;
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityRegit1Binding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

public class Regit_1Activity extends AppCompatActivity  {
    ActivityRegit1Binding binding;
    private TextView phoneAlt;
    boolean phoneState=false;
    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;
        binding= DataBindingUtil.setContentView(this,R.layout.activity_regit_1);
        binding.setActivity(this);
        //setContentView(R.layout.activity_regit_1);

        Toolbar toolbar =findViewById(R.id.r1_toolbar);
        setSupportActionBar(toolbar);

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle("회원가입");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);




        binding.phoneAlt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "이동",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(), FindAC1_Activity.class);
                intent.putExtra("phone",binding.phonenum.getText().toString());
                startActivity(intent);
            }
        });

        binding.phonenum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phonePattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";
                Matcher matcher = Pattern.compile(phonePattern).matcher(binding.phonenum.getText());

                if(!matcher.matches()){
                    binding.phoneAlt2.setText("형식에 맞지 않는 번호입니다.");
                    binding.phoneAlt2.setTextColor(Color.parseColor("#E53A40"));
                    phoneState=false;
                }else if(matcher.matches()){
                    binding.phoneAlt2.setText("확인버튼을 눌러주세요.");
                    binding.phoneAlt2.setTextColor(Color.parseColor("#5CAB7D"));
                    phoneState=true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.nextB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.phonenum.getText().toString().isEmpty()&&phoneState){
                    String url = url_ + "/userapp/check_phone";
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    /* Create request */
                    StringRequest loginForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    boolean success= false;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        success = jsonObject.getBoolean("result");
                                        if (success){
                                            Toast.makeText(getApplicationContext(),"인증되었습니다.",Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(getApplicationContext(), Regit_2Activity.class);
                                            intent.putExtra("id",getIntent().getStringExtra("id"));
                                            intent.putExtra("phone",binding.phonenum.getText().toString());
                                            intent.putExtra("type",getIntent().getIntExtra("type",0));
                                            startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
                                        }else {
                                            Toast.makeText(getApplicationContext(),"이미 있는 번호입니다.\n다른 번호를 입력해주세요.",Toast.LENGTH_LONG).show();
                                            binding.phoneAlt.setVisibility(View.VISIBLE);
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

                            params.put("user_phone", binding.phonenum.getText().toString());
                            return params;
                        }
                    };
                    loginForm.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(loginForm);
                }else {
                    Toast.makeText(getApplicationContext(),"번호를 올바르게 입력해주세요.",Toast.LENGTH_LONG).show();

                }
            }
        });
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
