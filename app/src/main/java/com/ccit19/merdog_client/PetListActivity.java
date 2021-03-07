package com.ccit19.merdog_client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.ccit19.merdog_client.databinding.ActivityPetListBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class PetListActivity extends AppCompatActivity {
    ActivityPetListBinding binding;
    private PetListViewAdapter petadapter;
    private CustomAnimationDialog customAnimationDialog;

    JSONArray pet_img;
    JSONArray pet_name;
    JSONArray pet_main_type;
    JSONArray pet_sub_type;
    JSONArray pet_id;
    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_list);
        binding.setActivity(this);
        customAnimationDialog = new CustomAnimationDialog(PetListActivity.this);

        setSupportActionBar(binding.plToolbar);

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle("내 펫 목록");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);


        //어뎁터와 초기화 부분 petList와 어뎁터를 연결
        petadapter = new PetListViewAdapter(getApplicationContext());
        binding.petlist.setAdapter(petadapter);

        binding.bPetadd.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PetRegisterActivity.class);
                startActivity(intent);
            }
        });

        String url = url_ + "/userapp/pet_list";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest regitForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        startProgress();
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success) {
                                pet_img = jsonObject.getJSONArray("pet_img");
                                pet_name = jsonObject.getJSONArray("pet_name");
                                pet_main_type = jsonObject.getJSONArray("pet_main_type");
                                pet_sub_type = jsonObject.getJSONArray("pet_sub_type");
                                pet_id = jsonObject.getJSONArray("pet_id");

                                for (int count = 0; count < pet_id.length(); count++) {
                                    String img_url = "jsonArray";
                                    String petimg = pet_img.getString(count);
                                    String petname = pet_name.getString(count);
                                    String petmaintype = pet_main_type.getString(count);
                                    String petsubtype = pet_sub_type.getString(count);
                                    PetListViewItem petListViewItem = new PetListViewItem(petimg, petname, petmaintype, petsubtype);
                                    petadapter.add(petListViewItem);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        customAnimationDialog.dismiss();
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
        regitForm.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(regitForm);

        binding.petlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(getApplicationContext(), PetModifyActivity.class);
                    intent.putExtra("pet_id", pet_id.get(position).toString());
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                // 해당 버튼을 눌렀을 때 적절한 액션을 넣는다.
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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

class PetListViewItem {

    String pet_img;
    String pet_name;
    String pet_main_type;
    String pet_sub_type;

    public String getPetImg() {
        return pet_img;
    }

    public String getPetName() {
        return pet_name;
    }

    public String getPetMainType() {
        return pet_main_type;
    }

    public String getPetSubType() {
        return pet_sub_type;
    }

    public PetListViewItem(String pet_img, String pet_name, String pet_main_type, String pet_sub_type) {
        this.pet_img = pet_img;
        this.pet_name = pet_name;
        this.pet_main_type = pet_main_type;
        this.pet_sub_type = pet_sub_type;
    }
}