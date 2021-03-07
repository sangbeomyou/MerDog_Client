package com.ccit19.merdog_client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

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
import com.bumptech.glide.RequestManager;
import com.ccit19.merdog_client.backServ.AppController;
import com.ccit19.merdog_client.backServ.chat.Message;
import com.ccit19.merdog_client.backServ.chat.MessageAdapter;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityChatBinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import gun0912.tedbottompicker.TedBottomPicker;

public class ChatActivity extends AppCompatActivity implements RoomListener {
    ActivityChatBinding binding;
    private String myid;
    private String channelID = "GJCIaUNfqtlHkHrF";
    private String roomName, chat_state = null, pet_name;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private Uri selectedUri;
    private RequestManager requestManager;
    private int sending_idx = 0;
    private int fail_idx = 0;
    String imagecode;
    Bitmap bm;
    String imgname;
    String url_ = urlset.getInstance().getData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ChatActivity.this, R.layout.activity_chat);
        binding.setActivity(this);
        roomName = getIntent().getStringExtra("chat_room");
        chat_state = getIntent().getStringExtra("chat_state");
        pet_name = getIntent().getStringExtra("pet_name");
        myid = SaveSharedPreference.getUserID(this);
        setSupportActionBar(findViewById(R.id.chta_toolbar));

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle(pet_name + "의 상담방");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);

        binding.msgText.setTextIsSelectable(true);
        messageAdapter = new MessageAdapter(this);
        binding.messagesView.setAdapter(messageAdapter);

        MemberData data = new MemberData(myid, getRandomColor());

        String url = url_ + "/chat/load";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // Create request
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success) {
                                JSONArray id_type = jsonObject.getJSONArray("id_type");
                                JSONArray send_id = jsonObject.getJSONArray("send_id");
                                JSONArray message_type = jsonObject.getJSONArray("message_type");
                                JSONArray message = jsonObject.getJSONArray("message");
                                JSONArray date = jsonObject.getJSONArray("date");
                                JSONArray chat_request_id = jsonObject.getJSONArray("chat_request_id");
                                for (int i = 0; i < id_type.length(); i++) {
                                    if (i > 0) {
                                        if (!chat_request_id.get(i).toString().equals(chat_request_id.get(i - 1).toString())) {
                                            //Message sys_msg = new Message(send_id.get(i).toString()+"님과의 상담이 시작되었습니다.", "system",date.get(i).toString(), false);
                                            Message sys_msg = new Message("상담이 시작되었습니다.", "system", date.get(i).toString(), false);
                                            messageAdapter.add(sys_msg);
                                            binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                                        }
                                    } else {
                                        //Message sys_msg = new Message(send_id.get(i).toString()+"님과의 상담이 시작되었습니다.", "system",date.get(i).toString(), false);
                                        Message sys_msg = new Message("상담이 시작되었습니다.", "system", date.get(i).toString(), false);
                                        messageAdapter.add(sys_msg);
                                        binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                                    }


                                    boolean belongsToCurrentUser = id_type.get(i).equals("user");
                                    Message load_msg = new Message(message.get(i).toString(), send_id.get(i).toString(), date.get(i).toString(), belongsToCurrentUser);
                                    messageAdapter.add(load_msg);
                                    binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                                    if (id_type.length() != i + 1) {
                                        if (!chat_request_id.get(i).toString().equals(chat_request_id.get(i + 1).toString())) {
                                            Message sys_msg = new Message("상담이 종료되었습니다.", "system", date.get(i).toString(), false);
                                            messageAdapter.add(sys_msg);
                                            binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                                        }
                                    }

                                }
                                if (chat_state.equals("finish")) {
                                    Message sys_msg = new Message("상담이 종료되었습니다.", "system", "2019", false);
                                    messageAdapter.add(sys_msg);
                                    binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "불러오기 실패", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
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

                params.put("chat_room", roomName);
                params.put("id_type", "user");
                params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                if (chat_state != null) {
                    if (!chat_state.equals("ing")) {
                        binding.chatInput.setVisibility(View.GONE);
                        params.put("chat_state", chat_state);
                    } else {
                        scaledrone = new Scaledrone(channelID, data);
                        scaledrone.connect(new Listener() {
                            @Override
                            public void onOpen() {
                                System.out.println("Scaledrone connection open");
                                scaledrone.subscribe(roomName, ChatActivity.this);
                                binding.acBtnSend.setClickable(true);
                            }

                            @Override
                            public void onOpenFailure(Exception ex) {
                                System.err.println(ex);
                            }

                            @Override
                            public void onFailure(Exception ex) {
                                System.err.println(ex);
                            }

                            @Override
                            public void onClosed(String reason) {
                                System.err.println(reason);
                            }
                        });
                    }
                } else {
                    chat_state = "ing";
                    Message sys_msg = new Message("상담이 시작되었습니다.", "system", "", false);
                    messageAdapter.add(sys_msg);
                    binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                    scaledrone = new Scaledrone(channelID, data);
                    scaledrone.connect(new Listener() {
                        @Override
                        public void onOpen() {
                            System.out.println("Scaledrone connection open");
                            scaledrone.subscribe(roomName, ChatActivity.this);
                            binding.acBtnSend.setClickable(true);
                        }

                        @Override
                        public void onOpenFailure(Exception ex) {
                            System.err.println(ex);
                        }

                        @Override
                        public void onFailure(Exception ex) {
                            System.err.println(ex);
                        }

                        @Override
                        public void onClosed(String reason) {
                            System.err.println(reason);
                        }
                    });
                }
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


        binding.acImgbtnImgselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedBottomPicker.with(ChatActivity.this)
                        //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(selectedUri)
                        .setPeekHeight(1200)
                        .show(uri -> {



                            //Log.d("ted", "uri: " + uri);
                            //Log.d("ted", "uri.getPath(): " + uri.getPath());
                            selectedUri = uri;
                            imgname=uri.getLastPathSegment();
                            binding.msgText.setText(uri.getPath());
                            Bitmap bm = BitmapFactory.decodeFile(uri.getPath());
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();

                            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos); //저장된이미지를 jpeg로 포맷 품질100으로하여 출력

                            byte[] ba = bos.toByteArray();

                            imagecode= Base64.encodeToString(ba,Base64.NO_WRAP);
                            try {
                                sendMessage(v,"img");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
/*
                            mSelectedImagesContainer.setVisibility(View.VISIBLE);
                            iv_image.setVisibility(View.VISIBLE);
                            iv_video.setVisibility(View.GONE);
                            chat_send.setVisibility(View.VISIBLE);
                            chat_editText.setEnabled(false);
                            chat_editText.getText().clear();
                            requestManager
                                    .load(uri)
                                    .into(iv_image);
                            bitmap_camera = resize(getApplicationContext(), selectedUri, 500);
                            check = 1;*/
                        });

            }
        });
        binding.msgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    binding.acBtnSend.setBackgroundColor(getResources().getColor(R.color.colorMain));
                } else {
                    binding.acBtnSend.setBackgroundColor(Color.WHITE);
                }
            }
        });


        binding.acBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.msgText.getText().toString().equals("")) {
                    binding.acBtnSend.setEnabled(false);
                    try {
                        sendMessage(v,"text");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void sendMessage(View view,String type) throws JSONException {
        String message = binding.msgText.getText().toString();
        if (message.length() > 0) {
            final Message rmessage = new Message(message, myid, "send", true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sending_idx = messageAdapter.getCount();
                    messageAdapter.add(rmessage);
                    binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                }
            });
            String url = url_ + "/chat/send";
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            // Create request
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            boolean success = false;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                success = jsonObject.getBoolean("result");
                                if (success) {

                                } else {
                                    fail_idx = sending_idx;
                                    messageAdapter.remove(sending_idx);
                                    final Message fmessage = new Message(message, myid, "fail", true);
                                    messageAdapter.add(fmessage);
                                    binding.messagesView.setSelection(binding.messagesView.getCount() - 1);

                                    Toast.makeText(getApplicationContext(), "전송 실패\n채팅방에 재입장해주세요.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            binding.acBtnSend.setEnabled(true);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    fail_idx = sending_idx;
                    messageAdapter.remove(sending_idx);
                    final Message fmessage = new Message(message, myid, "fail", true);
                    messageAdapter.add(fmessage);
                    binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    binding.acBtnSend.setEnabled(true);
                }
            })
            {
                @Override
                public  Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("chat_room", roomName);
                    params.put("chat_request_id", getIntent().getStringExtra("chat_request_id"));
                    params.put("id_type", "user");
                    params.put("send_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                    if (type.equals("text")){
                        params.put("message_type", "text");
                        params.put("file_encode", "null");
                        params.put("message", message);
                    }else if (type.equals("img")){
                        params.put("message_type", "img");
                        params.put("file_encode", imagecode);
                        params.put("message", imgname);
                    }
                    params.put("state", "on");
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
            binding.msgText.getText().clear();
        }
    }

    public void popUpProfile(View view) {
        Toast.makeText(getApplicationContext(), "팝업!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
        JsonParser parser = new JsonParser();
        JsonElement statej = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("state");
        String state = statej.toString().replaceAll("\"", "");

        if (state.equals("off")) {
            Intent intent = new Intent(getApplicationContext(), RateActivity.class);
            intent.putExtra("chat_request_id", getIntent().getStringExtra("chat_request_id"));
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
        }

        JsonElement idObject = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("message");
        String msg = idObject.toString().replaceAll("\"", "");

        JsonElement type_j = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("id_type");
        String type = type_j.toString().replaceAll("\"", "");

        JsonElement sender = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("send_id");
        String send = sender.toString().replaceAll("\"", "");
        JsonElement datej = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("time");
        String date = datej.toString().replaceAll("\"", "");

        final ObjectMapper mapper = new ObjectMapper();
        //final MemberData data = new MemberData();

        boolean belongsToCurrentUser = type.equals("user");
        final Message rmessage = new Message(msg, send, date, belongsToCurrentUser);
        //final MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (belongsToCurrentUser) {
                    messageAdapter.remove(sending_idx);
                }
                messageAdapter.add(rmessage);
                binding.messagesView.setSelection(binding.messagesView.getCount() - 1);
            }
        });

    }


    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while (sb.length() < 7) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // 해당 버튼을 눌렀을 때 적절한 액션을 넣는다.
                Intent intent = new Intent();
                intent.putExtra("result", "0");
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.action_finish:
                String url = url_ + "/chat/send";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                // Create request
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                boolean success = false;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getBoolean("result");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "상담이 종료되었습니다.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), RateActivity.class);
                                        intent.putExtra("chat_request_id", getIntent().getStringExtra("chat_request_id"));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                        startActivity(intent);
                                        //setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
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

                        params.put("chat_room", roomName);
                        params.put("chat_request_id", getIntent().getStringExtra("chat_request_id"));
                        params.put("state", "off");
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scaledrone != null)
            scaledrone.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!chat_state.equals("finish"))
            getMenuInflater().inflate(R.menu.chat_room_menu, menu);

        return true;
    }
}
class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

}