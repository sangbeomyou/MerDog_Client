package com.ccit19.merdog_client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
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
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BillingInfoActivity extends AppCompatActivity {
    private BillingAdapter adapter;
    private RefundAdapter adapter2;
    private ListView billing_list, refund_list;
    private CustomAnimationDialog customAnimationDialog;
    //결제 리스트뷰 배열
    JSONArray a_product_id;
    JSONArray a_payment_id;
    JSONArray a_state;
    JSONArray a_method;
    JSONArray a_date;

    //환불 리스트뷰 배열
    JSONArray b_refund_id;
    JSONArray b_payment_id;
    JSONArray b_order_id;
    JSONArray b_refund_state;
    JSONArray b_refund_date;
    JSONArray b_comment;

    String url_ = urlset.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_info);
        customAnimationDialog = new CustomAnimationDialog(BillingInfoActivity.this);

        refund_list = findViewById(R.id.refund_list);
        billing_list = findViewById(R.id.billing_list);

        //어뎁터와 초기화 부분 petList와 어뎁터를 연결
        adapter = new BillingAdapter(getApplicationContext());
        adapter2 = new RefundAdapter(getApplicationContext());
        billing_list.setAdapter(adapter);
        refund_list.setAdapter(adapter2);

        getBillingList();
        getRefundList();

        //결제리스트뷰 클릭
        billing_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogInterface.OnClickListener refund_Y = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String order_id = "test";
                        String url = url_ + "/userapp/refund";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        Billing billing=(Billing) parent.getItemAtPosition(position);

                        if(!adapter2.refundList.contains(billing.payment_id)){
                            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                                        @Override
                                        public void onResponse(String response) {
                                            boolean success = false;
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                success = jsonObject.getBoolean("result");
                                                customAnimationDialog.show();
                                                if (success) {
                                                    customAnimationDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "환불신청 되었습니다.", Toast.LENGTH_LONG).show();
                                                }else{

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    customAnimationDialog.dismiss();
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
                                    params.put("order_id", order_id);
                                    params.put("payment_id", billing.payment_id);
                                    return params;
                                }
                            };
                            request.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            queue.add(request);
                        }else{
                            Toast.makeText(getApplicationContext(), "이미 환불신청된 건입니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                DialogInterface.OnClickListener refund_N = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog.Builder refund_Dialog = new AlertDialog.Builder(BillingInfoActivity.this);
                refund_Dialog.setTitle("환불신청을 하시겠습니까?");
                refund_Dialog.setPositiveButton("신청",refund_Y);
                refund_Dialog.setNegativeButton("취소",refund_N);
                refund_Dialog.show();
            }
        });

        //환불리스트뷰 클릭
        refund_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogInterface.OnClickListener refund_delete_Y = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = url_ + "/userapp/refund_list";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        boolean success = false;
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            success = jsonObject.getBoolean("result");
                                            customAnimationDialog.show();
                                            if (success) {
                                                customAnimationDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "환불신청이 삭제되었습니다.", Toast.LENGTH_LONG).show();
                                            }else{

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                customAnimationDialog.dismiss();
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
                                Refund refund=(Refund) parent.getItemAtPosition(position);
                                params.put("refund_id", refund.refund_id);
                                return params;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(request);
                    }
                };
                DialogInterface.OnClickListener refund_delete_N = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog.Builder refund_delete_Dialog = new AlertDialog.Builder(BillingInfoActivity.this);
                refund_delete_Dialog.setTitle("환불신청을 삭제 하시겠습니까?");
                refund_delete_Dialog.setPositiveButton("삭제",refund_delete_Y);
                refund_delete_Dialog.setNegativeButton("취소",refund_delete_N);
                refund_delete_Dialog.show();

            }
        });
    }
    public void getBillingList(){
        //결제리스트뷰 출력
        String url = url_ + "/userapp/payment_list";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            customAnimationDialog.show();
                            if (success) {
                                a_product_id = jsonObject.getJSONArray("product_id");
                                a_payment_id = jsonObject.getJSONArray("payment_id");
                                a_date = jsonObject.getJSONArray("date");
//                                a_method = response.getJSONArray("method");
                                a_state = jsonObject.getJSONArray("state");
                                for (int count=0;count<a_payment_id.length();count++){
                                    String productid=a_product_id.getString(count);
                                    String paymentid=a_payment_id.getString(count);
                                    String state=a_state.getString(count);
//                                    String method=a_method.getString(count);
                                    String date=a_date.getString(count);
                                    //중간에 결제방식(method) 지움
                                    Billing billing = new Billing(paymentid, productid, state, date);
                                    adapter.add(billing);
                                }
                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        customAnimationDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                customAnimationDialog.dismiss();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void getRefundList(){
        //환불 리스트뷰 출력
        String url = url_ + "/userapp/refund_list";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            customAnimationDialog.show();
                            if (success) {
                                b_refund_id = jsonObject.getJSONArray("refund_id");
                                b_payment_id = jsonObject.getJSONArray("payment_id");
                                b_order_id = jsonObject.getJSONArray("order_id");
                                b_refund_state = jsonObject.getJSONArray("state");
                                b_refund_date = jsonObject.getJSONArray("date");
                                b_comment = jsonObject.getJSONArray("coment");
                                for (int count=0;count<b_payment_id.length();count++){
                                    String r_refundid=b_refund_id.getString(count);
                                    String r_orderid=b_order_id.getString(count);
                                    String r_paymentid=b_payment_id.getString(count);
                                    String r_state=b_refund_state.getString(count);
                                    String r_comment=b_comment.getString(count);
                                    String r_date=b_refund_date.getString(count);
                                    Refund refund = new Refund(r_refundid, r_paymentid, r_orderid, r_state, r_comment, r_date);
                                    adapter2.add(refund);
                                }
                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        customAnimationDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                customAnimationDialog.dismiss();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
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


}
class Billing {

    String payment_id;
    String product_id;
//    String method;
    String state;
    String date;

    public String getproduct_id() {
        return product_id;
    }
    public void setproduct_id(String product_id2) {
        this.product_id = product_id;
    }


    public String getpayment_id() {
        return payment_id;
    }
    public void setpayment_id(String payment_id) {
        this.payment_id = payment_id;
    }


//    public String getmethod() {
//        return method;
//    }
//    public void setmethod(String method) {
//        this.method = method;
//    }

    public String getstate() {
        return state;
    }
    public void setstate(String state) {
        this.state = state;
    }

    public String getdate() {
        return date;
    }
    public void setdate(String date) {
        this.date = date;
    }

    public Billing(String payment_id, String product_id, String state, String date){  // 중간에 String method 보류상태
        this.product_id = product_id;
        this.payment_id = payment_id;
//        this.method = "환불계좌 "+ method;
        this.state = state;
        this.date = date;
    }
}

class Refund {

    String r_payment_id;
    String refund_id;
    String order_id;
    String comment;
    String refund_state;
    String refund_date;

    public String getR_payment_id() {
        return r_payment_id;
    }
    public void setR_payment_id(String r_payment_id) {
        this.r_payment_id = r_payment_id;
    }


    public String getRefund_id() {
        return refund_id;
    }
    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }


    public String getOrder_id() {
        return order_id;
    }
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getRefund_state() {
        return refund_state;
    }
    public void setRefund_state(String refund_state) {
        this.refund_state = refund_state;
    }

    public String getRefund_date() {
        return refund_date;
    }
    public void setRefund_date(String refund_date) {
        this.refund_date = refund_date;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Refund(String refund_id, String r_payment_id, String order_id, String refund_state, String comment, String refund_date){
        this.refund_id = refund_id;
        this.r_payment_id = r_payment_id;
        this.order_id = order_id;
        this.refund_state = refund_state;
        this.comment = comment;
        this.refund_date = refund_date;
    }
}