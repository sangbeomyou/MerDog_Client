package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityFindAc1Binding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FindAC1_Activity extends AppCompatActivity {
    ActivityFindAc1Binding binding;
    String cert_number;
    private static final int MILLISINFUTURE = 180 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private int count = 180;
    private CountDownTimer countDownTimer;
    String url_ = urlset.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_find_ac1_);
        binding.setActivity(this);

        setSupportActionBar(binding.fa1Toolbar);

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle("계정 찾기");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);

        binding.fBtnCertsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = url_+ "/ajax/sms";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                /* Create request */
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //String pnPattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";
                                boolean success = false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("result");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "인증번호가 발송되었습니다.", Toast.LENGTH_LONG).show();
                                        binding.fBtnCertsend.setEnabled(false);
                                        countDownTimer();
                                        countDownTimer.start();
                                        binding.fEdtCertnum.setEnabled(true);
                                        binding.fBtnCertnumcheck.setEnabled(true);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
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
                        params.put("phone", binding.fEdtPhonenum.getText().toString());
                        cert_number = numberGen(6, 1);
                        params.put("number", cert_number);
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
                binding.fEdtCertnum.requestFocus();
            }
        });

        binding.fBtnCertnumcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cert_number.equals(binding.fEdtCertnum.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "인증되었습니다.", Toast.LENGTH_LONG).show();
                    binding.fBtnCertnumcheck.setEnabled(false);
                    binding.fBtnCertsend.setText("인증완료");
                    binding.fEdtCertnum.setEnabled(false);
                    binding.fBtnCertnumcheck.setEnabled(false);
                    countDownTimer.cancel();
                    Intent intent=new Intent(getApplicationContext(), FindAC1_Activity.class);
                    intent.putExtra("user_id","");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "올바른 인증번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    //랜덤 숫자 생성
    public static String numberGen(int len, int dupCd) {

        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수

        for (int i = 0; i < len; i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if (dupCd == 1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            } else if (dupCd == 2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if (!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                } else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i -= 1;
                }
            }
        }
        return numStr;
    }
    //휴대폰 번호 카운트다운 타이머
    public void countDownTimer() {
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                binding.fBtnCertsend.setText((count / 60) + ":" + String.format("%02d", count % 60));//분:초로 구분되어 보여짐
                count--;
            }

            public void onFinish() {
                count = 180;
                cert_number = numberGen(6, 1);// 끝났을경우 인증번호로 다른거로 바꿔서 인증막음
                binding.fBtnCertsend.setText("재전송");
                binding.fBtnCertsend.setEnabled(true);// 전송버튼 활성화
                binding.fEdtCertnum.setEnabled(false);// 번호입력상자 비활성화
                binding.fBtnCertnumcheck.setEnabled(false);// 번호체크버튼 비활성화
            }
        };
    }
    //액티비티 종료시
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
