package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
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
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityLoginBinding;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private static String OAUTH_CLIENT_ID = "jyvqXeaVOVmV";
    private static String OAUTH_CLIENT_SECRET = "527300A0_COq1_XV33cf";
    private static String OAUTH_CLIENT_NAME = "ashly9696";
    public static OAuthLogin mOAuthLoginModule;
    private BackPressed backPressed;
    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;
    private static String id_naver;
    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backPressed=new BackPressed(this);
        ActivityLoginBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_login);
        binding.setActivity(this);
        //setContentView(R.layout.activity_login);
        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        //카카오
        //네아로
        initData();
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                LoginActivity.this
                ,"Xty1vhqe2nBG5qskGJXr"
                ,"2xiNqWNzyR"
                ,"ashly9696"
        );

        binding.userid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    binding.textInputLayout.setError("공백문자는 사용할 수 없습니다.");
                } else {
                    binding.textInputLayout.setError(null); // null은 에러 메시지를 지워주는 기능
                }
            }
        });

        binding.userPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    binding.textInputLayout2.setError("공백문자는 사용할 수 없습니다.");
                } else {
                    binding.textInputLayout2.setError(null); // null은 에러 메시지를 지워주는 기능
                }
            }
        });

        binding.forgetac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FindAC2_Activity.class));
            }
        });

        binding.eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //binding.eLogin.set;
                ProgressBar progressBar=new ProgressBar(getApplicationContext());
                //binding.eLogin.set;
                String url = url_ + "/userapp/login";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                // Create request
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                boolean success = false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("result");
                                    if (success){
                                        Toast.makeText(getApplicationContext(),"로그인 되었습니다.",Toast.LENGTH_LONG).show();
                                        SaveSharedPreference.setUserInfo(getApplicationContext(),jsonObject.getString("user_num"), binding.userid.getText().toString(), 0, jsonObject.getString("token"));
                                        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish(); // Activity stack에서 제거
                                    }else if (jsonObject.getString("state").equals("off")){
                                        binding.checkwrong.setText("비활성화 된 계정입니다.\n고객센터에 문의해주세요.");
                                    }else {
                                        binding.checkwrong.setText("로그인 정보가 맞지 않습니다.");
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
                        params.put("user_id", binding.userid.getText().toString());
                        params.put("user_pw", binding.userPw.getText().toString());
                        params.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
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
        binding.comKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*callback = new SessionCallback();
                Session.getCurrentSession().addCallback(callback);
                Session.getCurrentSession().checkAndImplicitOpen();*/
            }
        });
        binding.naverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });

        binding.sginUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Regit_1Activity.class);
                startActivity(intent);
            }
        });
    }

   private void initData() {
       mOAuthLoginInstance = OAuthLogin.getInstance();

       mOAuthLoginInstance.showDevelopersLog(true);
       mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
       /*
        * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
        * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
        */
       //mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
   }

   private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
       @Override
       public void run(boolean success) {
           if (success) {
               String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
               String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
               long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
               String tokenType = mOAuthLoginInstance.getTokenType(mContext);
               new RequestApiTask().execute();
           } else {
               String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
               String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
               Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
           }
       }

   };

    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
            //id_naver=content["name"];
            JsonParser parser = new JsonParser();
            JsonElement dataObject = parser.parse(content)
                    .getAsJsonObject().get("response");
            JsonElement idObject = parser.parse(String.valueOf(dataObject))
                    .getAsJsonObject().get("id");
            String na_id = idObject.toString().replaceAll("\"", "");
            //new RequestNaverId().execute();
            String url = url_ + "/userapp/login";// 자체 서버 통신
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            // Create request
            StringRequest loginForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            boolean success= false;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                success = jsonObject.getBoolean("result");
                                if (success){
                                    Toast.makeText(getApplicationContext(),"로그인 되었습니다.",Toast.LENGTH_LONG).show();
                                    SaveSharedPreference.setUserInfo(getApplicationContext(), jsonObject.getString("user_num"),na_id, 2, jsonObject.getString("token"));
                                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish(); // Activity stack에서 제거
                                }else {
                                    Toast.makeText(getApplicationContext(),"회원가입이 필요합니다.",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(getApplicationContext(), Regit_1Activity.class);
                                    intent.putExtra("type",2);
                                    intent.putExtra("id",na_id);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                    params.put("type","naver");
                    params.put("user_id",na_id);
                    params.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
                    return params;
                }
            };
            loginForm.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(loginForm);
        }
    }
    //이미지 인코딩에 문자열로 만들기
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return encodedImage;
    }

    @Override
    public void onBackPressed() {
        backPressed.backPressedTwice();
    }
}


