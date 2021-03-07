package com.ccit19.merdog_client.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

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
import com.ccit19.merdog_client.ChatActivity;
import com.ccit19.merdog_client.R;
import com.ccit19.merdog_client.backServ.AppController;
import com.ccit19.merdog_client.backServ.chat.ChatList;
import com.ccit19.merdog_client.backServ.chat.ChatListAdapter;
import com.ccit19.merdog_client.backServ.Injection;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private ViewModelFactory mViewModelFactory;
    EditText roomid,petname,roomstate,requestid;
    TextView mPetName;
    Button mUpdateButton;
    ConstraintLayout ing, fin, main;
    private ListView n_chatLists;
    private ListView chatLists;
    private ChatListAdapter adapter;
    private ChatListAdapter n_adapter;
    JSONArray pet_imgArr;
    JSONArray chat_roomArr;
    JSONArray pet_nameArr;
    JSONArray chat_stateArr;
    JSONArray chat_request_idArr;
    JSONArray messageArr;
    JSONArray dateArr;
    String url_ = urlset.getInstance().getData();

    private final CompositeDisposable mDisposable = new CompositeDisposable();// 생성한 모든 Observable을 안드로이드 사이클에 맞춰 사용가능

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //dashboardViewModel =
        //ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ing=root.findViewById(R.id.fd_layout_ing);
        fin=root.findViewById(R.id.fd_layout_fin);


        adapter = new ChatListAdapter(root.getContext());
        chatLists = root.findViewById(R.id.dash_list_chatlist);
        chatLists.setAdapter(adapter);

        n_adapter = new ChatListAdapter(root.getContext());
        n_chatLists = root.findViewById(R.id.dash_list_now_chatlist);
        n_chatLists.setAdapter(n_adapter);

        mViewModelFactory = Injection.provideViewModelFactory(root.getContext());
        dashboardViewModel = new ViewModelProvider(this, mViewModelFactory).get(DashboardViewModel.class);
        /*roomid=root.findViewById(R.id.editText5);
        petname=root.findViewById(R.id.editText4);
        roomstate=root.findViewById(R.id.editText6);
        requestid=root.findViewById(R.id.editText3);
        mPetName=root.findViewById(R.id.text_dashboard);
        mUpdateButton=root.findViewById(R.id.button4);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    updateUserName();
            }
        });*/


        String url = url_ + "/chat/list";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest listForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success) {
                                pet_imgArr=jsonObject.getJSONArray("pet_img");
                                chat_roomArr = jsonObject.getJSONArray("chat_room");
                                pet_nameArr = jsonObject.getJSONArray("pet_name");
                                chat_stateArr = jsonObject.getJSONArray("chat_state");
                                chat_request_idArr = jsonObject.getJSONArray("chat_request_id");
                                messageArr = jsonObject.getJSONArray("message");
                                dateArr = jsonObject.getJSONArray("date");
                                for (int count = 0; count < chat_roomArr.length(); count++) {
                                    String pet_img=pet_imgArr.getString(count);
                                    String chat_room = chat_roomArr.getString(count);
                                    String pet_name = pet_nameArr.getString(count);
                                    String chat_state = chat_stateArr.getString(count);
                                    String chat_request_id = chat_request_idArr.getString(count);
                                    String message = messageArr.getString(count);
                                    String date = dateArr.getString(count);
                                    if (chat_state.equals("ing")) {
                                        n_adapter.add(new ChatList(pet_img,chat_room, pet_name, chat_state, chat_request_id, message, date));
                                        //n_chatLists.setSelection(n_chatLists.getCount() - 1);
                                    } else {
                                        adapter.add(new ChatList(pet_img,chat_room, pet_name, chat_state, chat_request_id, message, date));
                                        //.setSelection(chatLists.getCount() - 1);
                                    }
                                }
                                if (n_adapter.getCount()==0){
                                    ing.setVisibility(View.GONE);
                                }else if (adapter.getCount()==0){
                                    fin.setVisibility(View.GONE);
                                    //main.setBackgroundResource(R.drawable.img_error);
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

                params.put("id_type", "user");
                params.put("user_id", SaveSharedPreference.getUserIdx(root.getContext()));
                return params;
            }
        };
        listForm.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(listForm);

        chatLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatList chatList11 = (ChatList) parent.getItemAtPosition(position);
                Intent intent = new Intent(root.getContext(), ChatActivity.class);
                intent.putExtra("chat_room", chatList11.getChat_room());
                intent.putExtra("chat_request_id", chatList11.getChat_request_id());
                intent.putExtra("chat_state", chatList11.getChat_state());
                intent.putExtra("pet_name",chatList11.getPet_name());
                startActivityForResult(intent,0);
            }
        });

        n_chatLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatList chatList11 = (ChatList) parent.getItemAtPosition(position);
                Intent intent = new Intent(root.getContext(), ChatActivity.class);
                intent.putExtra("chat_room", chatList11.getChat_room());
                intent.putExtra("chat_request_id", chatList11.getChat_request_id());
                intent.putExtra("chat_state", chatList11.getChat_state());
                intent.putExtra("pet_name",chatList11.getPet_name());
                startActivityForResult(intent,0);

            }
        });

        return root;
    }

    /*@Override
    public void onStart() {

        // Subscribe to the emissions of the user name from the view model.
        // Update the user name text view, at every onNext emission.
        // In case of error, log the exception.
        super.onStart();

        mDisposable.add(dashboardViewModel.getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pet_name -> mPetName.setText(pet_name),
                        throwable -> Log.e(TAG, "Unable to update username", throwable)));
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
            if (resultCode == RESULT_OK) {

            } else {   // RESULT_CANCEL

            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // clear all the subscriptions
        mDisposable.clear();
    }

    private void updateUserName() {
        // Disable the update button until the user name update has been done
        mUpdateButton.setEnabled(false);
        // Subscribe to updating the user name.
        // Re-enable the button once the user name has been updated
        mDisposable.add(dashboardViewModel.updateChatList(roomid.getText().toString(),petname.getText().toString(),roomstate.getText().toString(),requestid.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mUpdateButton.setEnabled(true),
                        throwable -> Log.e(TAG, "Unable to update username", throwable)));
    }
}