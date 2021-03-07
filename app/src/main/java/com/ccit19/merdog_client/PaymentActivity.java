package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    ArrayAdapter<CharSequence> adspin;
    private TableLayout product_table_layout;
    private TextView p_product_id, p_product_name, p_product_ticket, p_product_price;
    private RadioGroup p_rg_method;
    private RadioButton p_method_passbook;
    private Spinner p_bank_name;
    private EditText p_bank_depo;
    private Button p_btn_payment;
    private String radioselectedItem;
    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        p_product_id = findViewById(R.id.p_product_id);
        p_product_name = findViewById(R.id.p_product_name);
        p_product_ticket = findViewById(R.id.p_product_ticket);
        p_product_price = findViewById(R.id.p_product_price);
        p_btn_payment = findViewById(R.id.p_btn_payment);
        p_bank_depo = findViewById(R.id.p_bank_depo);
        p_rg_method = findViewById(R.id.p_rg_method);
        p_method_passbook= findViewById(R.id.p_method_passbook);
        p_bank_name = findViewById(R.id.p_bank_name);

        //radiogroup에서 선택된 아이디값을 변수에 저장 , 결제방법
        p_rg_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                radioselectedItem = "passbook";
            }
        });

        //은행명 스피너
        adspin = ArrayAdapter.createFromResource(this, R.array.spinner_bank_name, android.R.layout.simple_spinner_dropdown_item);
        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        p_bank_name.setAdapter(adspin);


        //상품정보값 받아오기
        String url = url_ + "/userapp/product";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest productForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success) {
                                p_product_id.setText(getIntent().getStringExtra("product_id"));
                                p_product_name.setText(getIntent().getStringExtra("product_name"));
                                p_product_ticket.setText(getIntent().getStringExtra("product_ticket"));
                                p_product_price.setText(getIntent().getStringExtra("product_price"));
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

                return params;
            }
        };
        productForm.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(productForm);


        p_btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //결제신청
                String url = url_ + "/userapp/payment";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest paymentForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                boolean success = false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("result");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "결제신청이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "결제 실패 백종환에게 문의", Toast.LENGTH_LONG).show();
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

                        String selectedItem = p_bank_name.getSelectedItem().toString();
                        params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                        params.put("product_id", getIntent().getStringExtra("product_id"));
                        params.put("method", radioselectedItem);
                        params.put("bank_name", selectedItem);
                        params.put("bank_depo", p_bank_depo.getText().toString());
                        return params;
                    }
                };
                paymentForm.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(paymentForm);
            }
        });

    }
}
