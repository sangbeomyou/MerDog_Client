package com.ccit19.merdog_client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.bumptech.glide.Glide;
import com.ccit19.merdog_client.backServ.AppController;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {
    private ImageView product_img;
    private ListView product_list;
    private ProductAdapter adapter;
    private Button btn_buy;
    private TextView tv_product_ticket;
    private CustomAnimationDialog customAnimationDialog;
    JSONArray product_image;
    JSONArray product_id;
    JSONArray product_name;
    JSONArray product_ticket;
    JSONArray product_content;
    JSONArray product_price;
    String url_ = urlset.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        customAnimationDialog = new CustomAnimationDialog(ProductActivity.this);

        startProgress();
        getTicket();

        product_img = findViewById(R.id.product_img);
        product_list = findViewById(R.id.product_list);
        tv_product_ticket = findViewById(R.id.tv_product_ticket);

        //어뎁터와 초기화 부분 petList와 어뎁터를 연결
        adapter = new ProductAdapter(getApplicationContext());
        product_list.setAdapter(adapter);

        //이용권 개수 받아오기
        String url = url_ + "/userapp/ticket";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success) {
                                String string = "남은 이용권: ";
                                String string2 = "개";
                                tv_product_ticket.setText(string+jsonObject.getString("ticket")+string2);
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
        })             {
            @Override
            public  Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

        //상품정보 불러오기
        String url2 = url_ + "/userapp/product";
        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
        StringRequest productForm = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            startProgress();
                            if (success) {
                                product_id = jsonObject.getJSONArray("product_id");
                                product_name = jsonObject.getJSONArray("product_name");
                                product_ticket = jsonObject.getJSONArray("product_ticket");
                                product_price = jsonObject.getJSONArray("product_price");
                                product_image = jsonObject.getJSONArray("product_img");

                                for (int count=0;count<product_id.length();count++){
                                    String productid=product_id.getString(count);
                                    String productname=product_name.getString(count);
                                    String productticket=product_ticket.getString(count);
                                    String productimage=product_image.getString(count);
                                    String productprice=product_price.getString(count);
                                    Product product = new Product(productid, productname, productticket, productimage, productprice);
                                    adapter.add(product);
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
        })             {
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
        queue2.add(productForm);

        product_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogInterface.OnClickListener payment_Y = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //결제신청
                        //일단은 바로 결제가 되게 진행
                        String url =  url_ + "/userapp/payment";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest ticketForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        boolean success = false;
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            success = jsonObject.getBoolean("result");
                                            if (success) {
                                                Toast.makeText(getApplicationContext(), "결제되었습니다.", Toast.LENGTH_LONG).show();
                                                getTicket();
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

                                params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                                Product product=(Product) parent.getItemAtPosition(position);
                                params.put("product_id", product.product_id);
                                return params;
                            }
                        };
                        ticketForm.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(ticketForm);
                    }
                };
                DialogInterface.OnClickListener payment_N = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog.Builder payment_Dialog = new AlertDialog.Builder(ProductActivity.this);
                payment_Dialog.setTitle("결제하시겠습니까?");
                payment_Dialog.setPositiveButton("결제",payment_Y);
                payment_Dialog.setNegativeButton("취소",payment_N);
                payment_Dialog.show();
            }
        });
    }

    //로딩중 progress
    private void startProgress() {

        customAnimationDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                customAnimationDialog.dismiss();
            }
        }, 1000);

    }

    public void getTicket(){
        //이용권 개수 받아오기
        String url = url_ + "/userapp/ticket";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest ticketForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success) {
                                String string = "남은 이용권: ";
                                String string2 = "개";
                                tv_product_ticket.setText(string+jsonObject.getString("ticket")+string2);
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

                params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                return params;
            }
        };
        ticketForm.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(ticketForm);
    }
}
class Product {

    String product_id;
    String product_name;
    String product_image;
    String product_ticket;
    String product_price;

    public String getProduct_id() {
        return product_id;
    }
    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }


    public String getProduct_name() {
        return product_name;
    }
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }


    public String getProduct_ticket() {
        return product_ticket;
    }
    public void setProduct_ticket(String product_ticket) {
        this.product_ticket = product_ticket;
    }

    public String getProduct_image() {
        return product_image;
    }
    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_price() {
        return product_price;
    }
    public void setPetImg(String product_price) {
        this.product_price = product_price;
    }

    public Product(String product_id, String product_name, String product_ticket, String product_image, String product_price){
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_ticket = product_ticket;
        this.product_image = product_image;
        this.product_price = product_price;
    }
}