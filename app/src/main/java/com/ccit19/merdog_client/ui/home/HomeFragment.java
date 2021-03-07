package com.ccit19.merdog_client.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ccit19.merdog_client.MainActivity;
import com.ccit19.merdog_client.MatchChatActivity;
import com.ccit19.merdog_client.R;
import com.ccit19.merdog_client.backServ.AppController;
import com.ccit19.merdog_client.backServ.BackPressed;
import com.ccit19.merdog_client.backServ.GpsTracker;
import com.ccit19.merdog_client.backServ.MainPetList;
import com.ccit19.merdog_client.backServ.MainPetListAdapder;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView m_petLists;
    private MainPetListAdapder adapter;
    ArrayList<MainPetList> list = new ArrayList<>();
    int spinner_pos = 2;
    int type_pos = 0;
    String select_pet = "";
    EditText body_edt;
    View root;
    private GpsTracker gpsTracker;
    double latitude;
    double longitude;
    ImageButton btn_location;
    TextView location;
    Button startChat;
    String url_ = urlset.getInstance().getData();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);


        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = root.findViewById(R.id.main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));

        //아이템추가
        String url = url_ + "/userapp/pet_list";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest listForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success) {
                                JSONArray pet_imgArr = jsonObject.getJSONArray("pet_img");
                                JSONArray pet_idArr = jsonObject.getJSONArray("pet_id");
                                JSONArray pet_nameArr = jsonObject.getJSONArray("pet_name");

                                for (int count = 0; count < pet_idArr.length(); count++) {
                                    String pet_img = pet_imgArr.getString(count);
                                    String pet_id = pet_idArr.getString(count);
                                    String pet_name = pet_nameArr.getString(count);
                                    list.add(new MainPetList(pet_img, pet_id, pet_name));
                                }
                                select_pet = list.get(0).getPet_id();
                                adapter = new MainPetListAdapder(root.getContext(), list);
                                recyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener(new MainPetListAdapder.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        select_pet = list.get(position).getPet_id();
                                        // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(root.getContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(root.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(root.getContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(root.getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(root.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        })
        {
            @Override
            public  Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", SaveSharedPreference.getUserIdx(root.getContext()));
                return params;
            }
        };
        listForm.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(listForm);

        Spinner range_spinner = (Spinner) root.findViewById(R.id.fh_spn_distance);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> range_adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.match_distance, R.layout.spinner_layout);
// Specify the layout to use when the list of choices appears
        range_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        range_spinner.setAdapter(range_adapter);
        range_spinner.setSelection(2);


        Spinner type_spinner = (Spinner) root.findViewById(R.id.fh_spn_title);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.match_type, R.layout.spinner_layout);
// Specify the layout to use when the list of choices appears
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        type_spinner.setAdapter(type_adapter);

        btn_location = root.findViewById(R.id.fh_btn_location);
        location = root.findViewById(R.id.fh_txv_location);
        startChat = root.findViewById(R.id.startChat);
        body_edt = root.findViewById(R.id.fh_Edt_body);
        searchlocate();
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchlocate();
            }
        });


        //상담요청시
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bo1 = select_pet.isEmpty();
                boolean bo2 = body_edt.getText().toString().equals("");
                if (select_pet.isEmpty() || body_edt.getText().toString().equals("")) {
                    Toast.makeText(root.getContext(), "정보를 채워주세요.", Toast.LENGTH_LONG).show();
                } else {
                    String url = url_ + "/chat/request";//
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    // Create request
                    //params.put("user_pw", binding.userPw.getText().toString());
                    //아이디, 강아지정보
                    StringRequest requestChat = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    boolean success = false;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        success = jsonObject.getBoolean("result");
                                        if (success) {
                                            Intent intent = new Intent(root.getContext(), MatchChatActivity.class);
                                            intent.putExtra("chat_request_id", jsonObject.getString("chat_request_id"));
                                            intent.putExtra("time", jsonObject.getString("time"));
                                            startActivity(intent);
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setMessage("입력하신 내용이 사라집니다.\n정말로 뒤로 가시겠습니까?")
                                                    .setTitle("알림!");
                                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {

                                                }
                                            });

                                            switch (jsonObject.getString("warning")) {
                                                case "stop_member":
                                                    builder.setMessage("정지된 회원입니다.\n고객센터에 문의하세요.")
                                                            .setTitle("알림!");
                                                    break;
                                                case "no_ticket":
                                                    builder.setMessage("이용권이 없습니다.\n이용권을 먼저 구매하세요.")
                                                            .setTitle("알림!");
                                                    break;
                                                case "no_matching":
                                                    builder.setMessage("상담가능한 의사가 없습니다.")
                                                            .setTitle("알림!");
                                                    break;
                                                case "no_fcm":
                                                    builder.setMessage("알림 발송에 실패했습니다.\n잠시후 다시 시도해 주세요.")
                                                            .setTitle("알림!");
                                                    break;
                                            }
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
                                Toast.makeText(root.getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(root.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(root.getContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(root.getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(root.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    {
                        @Override
                        public  Map<String, String> getParams() throws AuthFailureError
                        {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("user_id", SaveSharedPreference.getUserIdx(root.getContext()));
                            params.put("pet_id", select_pet);
                            params.put("latitude", String.valueOf(latitude));
                            params.put("longitude", String.valueOf(longitude));
                            switch (range_spinner.getSelectedItem().toString()) {
                                case "5km":
                                    params.put("distance", "5");
                                    break;
                                case "10km":
                                    params.put("distance", "10");
                                    break;
                                case "15km":
                                    params.put("distance", "15");
                                    break;
                                case "20km":
                                    params.put("distance", "20");
                                    break;
                                case "상관없음":
                                    params.put("distance", "100");
                                    break;
                            }
                            params.put("count", "1");
                            params.put("chat_title", type_spinner.getSelectedItem().toString());
                            params.put("chat_content", body_edt.getText().toString());
                            params.put("address", location.getText().toString());
                            return params;
                        }
                    };
                    requestChat.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(requestChat);
                }

            }
        });
        /*homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/


        return root;


    }

    public void searchlocate() {
        gpsTracker = new GpsTracker(root.getContext());

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        location.setText(address);
    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(root.getContext(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(root.getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(root.getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(root.getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        String fin_add = address.getAddressLine(0).replace("대한민국 ", "");
        return fin_add;

    }

}