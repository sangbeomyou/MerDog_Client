package com.ccit19.merdog_client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.ccit19.merdog_client.backServ.BackPressed;
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityRegit2Binding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

public class Regit_2Activity extends AppCompatActivity {
    ActivityRegit2Binding binding;
    BackPressed backPressed =new BackPressed(this);
    Context context=Regit_2Activity.this;
    String phone_number;
    String cert_number;
    private static final int MILLISINFUTURE = 180 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;

    private int count = 180;
    private CountDownTimer countDownTimer;

    //상태값
    boolean idState = false;
    boolean pwState = false;
    boolean conpwState = false;
    boolean nameState = false;
    boolean confnumState = false;

    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_regit_2);
        binding.setActivity(this);
        Toolbar toolbar =findViewById(R.id.r2_toolbar);
        setSupportActionBar(toolbar);

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle("회원가입");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);




        phone_number = getIntent().getStringExtra("phone");
        binding.sUserPhone.setText(phone_number);
        //소셜로그인 판단
        if (getIntent().getIntExtra("type", 0) == 2) {
            binding.UserInfoForm.setVisibility(View.GONE);
            idState = true;
            pwState = true;
            conpwState = true;
        }
        //id 텍스트 변경시
        binding.sUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String idPattern = "^[a-z0-9\\-_]{5,20}$";   //공백제외 , -_포함, 영문소문자포함 5-20글자
                Matcher matcher = Pattern.compile(idPattern).matcher(binding.sUserId.getText());
                if (!matcher.matches()) {   //중복된 아이디값은 없는데 정규식에 위배
                    binding.idAlt.setText("5~20자의 영소문자, 숫자, 특수기호(_),(-)만 가능합니다");
                    binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                    idState = false;
                } else if (matcher.matches()) {       //중복된값이 아이디값이 있으면 사용중인 아이디 출력
                    binding.idAlt.setText("중복확인 버튼을 눌러주세요");
                    binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                    idState = false;
                }

//                binding.idAlt.setText("5~20자의 영소문자, 숫자, 특수기호(_),(-)만 가능합니다");
            }
        });
        //id 중복체크
        binding.idCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String idPattern = "^[a-z0-9\\-_]{5,20}$";   //공백제외 , -_포함, 영문소문자포함 5-20글자
                //String idPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{5,15}$";
                Matcher matcher = Pattern.compile(idPattern).matcher(binding.sUserId.getText());
                if (!binding.sUserId.getText().toString().isEmpty()&&matcher.matches()) {
                    String url = url_ + "/userapp/check_id";
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    /* Create request */
                    StringRequest idcheckForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    boolean success = false;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        success = jsonObject.getBoolean("result");
                                        if (success) {
                                            binding.idAlt.setText("사용가능한 아이디입니다.");
                                            binding.idAlt.setTextColor(Color.parseColor("#5CAB7D"));
                                            idState = true;
                                        } else {
                                            binding.idAlt.setText("이미 사용중인 아이디입니다");
                                            binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                                            idState = false;
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

                            params.put("user_id", binding.sUserId.getText().toString());
                            return params;
                        }
                    };
                    idcheckForm.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(idcheckForm);
                } else {
                    binding.idAlt.setText("아이디를 올바르게 입력해주세요.");
                    binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                }

            }
        });
        //패스워드 텍스트 확인
        binding.sUserPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pwPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*])[a-zA-Z0-9~!@#$%^&*]{5,15}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                if (!matcher.matches()) {
                    binding.passAlt.setText("영문, 숫자, 특수문자 포함 5~15자리로 입력해주세요.");
                    pwState = false;
                } else if (matcher2.find()) {
                    binding.passAlt.setText("같은문자는 4개이상 사용할수 없습니다.");
                    pwState = false;
                } else if (binding.sUserPass.getText().toString().contains(" ")) {
                    binding.passAlt.setText("공백은 입력이 불가능합니다.");
                    pwState = false;
                } else {
                    binding.passAlt.setText("");
                    pwState = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pwPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{5,15}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                if (binding.sUserPass.getText().toString().equals(binding.sPassVeri.getText().toString()) && !binding.sPassVeri.getText().toString().isEmpty() && matcher.matches()) {
                    binding.passvrifAlt.setText("비밀번호가 일치합니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#5CAB7D"));
                    conpwState = true;
                }
                /*else if(binding.sUserPass.getText().toString().equals(binding.sPassVeri.getText().toString())&&!binding.sPassVeri.getText().toString().isEmpty() && (check3 != 0)){
                    binding.passAlt.setText("영문, 숫자, 특수문자 포함 5~15자리로 입력해주세요.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#E53A40"));
                    check3++;
                }*/
                else {
                    binding.passvrifAlt.setText("비밀번호가 일치하지 않습니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#E53A40"));
                    conpwState = false;
                }
            }
        });
        //패스워드 확인 텍스트 확인
        binding.sPassVeri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pwPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*])[a-zA-Z0-9~!@#$%^&*]{5,15}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                if (!matcher.matches()) {
                    binding.passAlt.setText("영문, 숫자, 특수문자 포함 5~15자리로 입력해주세요.");
                    pwState = false;
                } else if (matcher2.find()) {
                    binding.passAlt.setText("같은문자는 4개이상 사용할수 없습니다.");
                    pwState = false;
                } else if (binding.sUserPass.getText().toString().contains(" ")) {
                    binding.passAlt.setText("공백은 입력이 불가능합니다.");
                    pwState = false;
                } else {
                    binding.passAlt.setText("");
                    pwState = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pwPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*])[a-zA-Z0-9~!@#$%^&*]{5,15}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(binding.sUserPass.getText());

                if (binding.sUserPass.getText().toString().equals(binding.sPassVeri.getText().toString()) && !binding.sPassVeri.getText().toString().isEmpty()) {
                    binding.passvrifAlt.setText("비밀번호가 일치합니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#5CAB7D"));
                    conpwState = true;
                } else if (!matcher.matches()) {
                    binding.passvrifAlt.setText("영문, 숫자, 특수문자 포함 5~15자리로 입력해주세요.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#E53A40"));
                    conpwState = false;
                } else {
                    binding.passvrifAlt.setText("비밀번호가 일치하지 않습니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#E53A40"));
                    conpwState = false;
                }
            }
        });
        //닉네임 텍스트 확인
        binding.sNicname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String nicPattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{1,10}$";   //한글,영문,숫자 최대 10자 입력가능
                Matcher matcher = Pattern.compile(nicPattern).matcher(binding.sNicname.getText());
                if (!matcher.matches()) {   //중복된 아이디값은 없는데 정규식에 위배
                    binding.nicnameAlt.setText("한글,영문,숫자만 입력가능합니다.");
                    binding.nicnameAlt.setTextColor(Color.parseColor("#E53A40"));
                    nameState = false;
                } else if (matcher.matches()) {
                    binding.nicnameAlt.setText("중복확인 버튼을 눌러주세요");
                    binding.nicnameAlt.setTextColor(Color.parseColor("#E53A40"));
                    nameState = false;
                }
            }
        });
        //닉네임 체크
        binding.nicCheck.setOnClickListener(new View.OnClickListener() { //중복확인 버튼 클릭 이벤트
            @Override
            public void onClick(final View view) {
                String nicPattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{1,10}$";   //
                Matcher matcher = Pattern.compile(nicPattern).matcher(binding.sNicname.getText());
                if (!binding.sNicname.getText().toString().isEmpty()&&matcher.matches()) {
                    String url = url_ + "/userapp/check_nick";
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    /* Create request */
                    StringRequest niccheckForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    boolean success = false;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        success = jsonObject.getBoolean("result");
                                            if (!success) {
                                                binding.nicnameAlt.setText("이미 사용중인 닉네임입니다");
                                                binding.nicnameAlt.setTextColor(Color.parseColor("#E53A40"));
                                                nameState = false;
                                            } else {
                                                binding.nicnameAlt.setText("사용가능한 닉네임입니다.");
                                                binding.nicnameAlt.setTextColor(Color.parseColor("#5CAB7D"));
                                                nameState = true;
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

                            params.put("user_name", binding.sNicname.getText().toString());
                            return params;
                        }
                    };
                    niccheckForm.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(niccheckForm);
                } else {
                    binding.nicnameAlt.setText("닉네임을 올바르게 입력해주세요.");
                    binding.nicnameAlt.setTextColor(Color.parseColor("#E53A40"));
                    nameState = false;
                }

            }
        });
        // 인증번호 전송
        binding.certSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = url_ + "/ajax/sms";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                /* Create request */
                StringRequest regitForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //String pnPattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";
                                boolean success = false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("result");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "인증번호가 발송되었습니다.", Toast.LENGTH_LONG).show();
                                        binding.certSend.setEnabled(false);
                                        countDownTimer();
                                        countDownTimer.start();
                                        binding.certNum.setEnabled(true);
                                        binding.certnumCheck.setEnabled(true);
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

                        params.put("phone", phone_number);
                        cert_number = numberGen(6, 1);
                        params.put("number", cert_number);
                        return params;
                    }
                };
                regitForm.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(regitForm);
                binding.certNum.requestFocus();
            }
        });
        // 인증번호 체크
        binding.certnumCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cert_number.equals(binding.certNum.getText().toString())) {
                    binding.certnumAlt.setText("인증되었습니다.");
                    Toast.makeText(getApplicationContext(), "인증되었습니다.", Toast.LENGTH_LONG).show();
                    binding.certnumCheck.setEnabled(false);
                    binding.certSend.setText("인증완료");
                    binding.certNum.setEnabled(false);
                    binding.certnumCheck.setEnabled(false);
                    countDownTimer.cancel();
                    confnumState = true;
                } else {
                    binding.certnumAlt.setText("올바른 인증번호를 입력하세요.");
                    confnumState = false;
                }
            }
        });
        //이용약관 로드
        binding.usePolish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("about","privacy");
                startActivity(intent);
            }
        });
        //개인정보 활용 로드
        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("about","service");
                startActivity(intent);
            }
        });
        //완료버튼 체크
        binding.finButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (idState && pwState && conpwState && nameState && confnumState && binding.checkBox2.isChecked()) {
                    String url = url_ + "/userapp/register";
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    /* Create request */
                    StringRequest regitForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    boolean success = false;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        success = jsonObject.getBoolean("result");
                                        if (success) {
                                            Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
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

                            if (getIntent().getIntExtra("type", 0) == 2) {
                                params.put("user_id", getIntent().getStringExtra("id"));
                                params.put("type", "naver");
                                params.put("user_name", binding.sNicname.getText().toString());
                                params.put("user_phone", getIntent().getStringExtra("phone"));
                            } else {
                                params.put("user_id", binding.sUserId.getText().toString());
                                params.put("user_pw", binding.sPassVeri.getText().toString());
                                params.put("user_name", binding.sNicname.getText().toString());
                                params.put("user_phone", getIntent().getStringExtra("phone"));
                            }
                            return params;
                        }
                    };
                    regitForm.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(regitForm);

/*
                    //서버통신 완료후
                    Response.Listener<String> responseListener= new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                boolean success=jsonObject.getBoolean("data");
                                if (success){
                                    Toast.makeText(view.getContext(),"가입이 완료되었습니다.\n로그인 해주세요.",Toast.LENGTH_LONG).show();
                                    getActivity().finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(view.getContext(),"서버오류입니다.\n잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    RegisterRequest registerRequest=new RegisterRequest(userid.getText().toString(),userpw.getText().toString(),userNic.getText().toString(),userPhone.getText().toString(),responseListener);
                    RequestQueue queue=Volley.newRequestQueue(view.getContext());
                    queue.add(registerRequest); //기존 통신*/
                } else if(!idState) {
                    Toast.makeText(getApplicationContext(), "아이디를 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if(!pwState) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if(!nameState) {
                    Toast.makeText(getApplicationContext(), "닉네임을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if(!binding.checkBox2.isChecked()) {
                    Toast.makeText(getApplicationContext(), "이용약관에 동의해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
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
                binding.certSend.setText((count / 60) + ":" + String.format("%02d", count % 60));//분:초로 구분되어 보여짐
                count--;
            }

            public void onFinish() {
                count = 180;
                cert_number = numberGen(6, 1);// 끝났을경우 인증번호로 다른거로 바꿔서 인증막음
                binding.certSend.setText("재전송");
                binding.certSend.setEnabled(true);// 전송버튼 활성화
                binding.certNum.setEnabled(false);// 번호입력상자 비활성화
                binding.certnumCheck.setEnabled(false);// 번호체크버튼 비활성화
            }
        };
    }/*
    public void backPressAlt(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("입력하신 내용이 사라집니다.\n정말로 뒤로 가시겠습니까?")
                .setTitle("알림!");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    @Override
    public void onBackPressed(){
        backPressed.backPressedAlert();
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
                backPressed.backPressedAlert();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}