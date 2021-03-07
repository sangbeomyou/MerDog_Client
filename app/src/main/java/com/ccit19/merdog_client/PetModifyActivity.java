package com.ccit19.merdog_client;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.ccit19.merdog_client.backServ.BackPressed;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityPetModifyBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

public class PetModifyActivity extends AppCompatActivity {
    ActivityPetModifyBinding binding;
    BackPressed backPressed=new BackPressed(this);
    ArrayAdapter<CharSequence> adspin1, adspin2;

    final String TAG = getClass().getSimpleName();
    private CustomAnimationDialog customAnimationDialog;

    private final int GET_GALLERY_IMAGE1 = 0,GET_CAMERA_IMAGE1 = 2;
    private Uri filePath;
    private Bitmap bitmap;
    private String petseledted;
    private String neutralized_female = "중성화암컷";
    private String neutralized_male = "중성화수컷";
    private String female = "암컷";
    private String male = "수컷";
    //데이트피커 변수
    String datepicker;
    String getdate;
    String cutyear;
    String cutmonth;
    String cutday;
    String getYear = "";
    String getMonth = "";
    String getDay = "";
    //추출한 날짜 변수
    int intyear;
    int intmonth;
    int intday;
    //현재날짜 변수
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    int currentDate = Calendar.getInstance().get(Calendar.DATE);
    //나이계산 변수
    int ageYear = 0;
    int ageMonth = 0;
    int ageDay = 0;
    //수정등록시 체크변수
    boolean m_nameCheck = false;
    boolean m_ageCheck = false;
    boolean m_birthCheck = false;
    boolean m_genderCheck = false;
    boolean m_kindCheck = false;
    boolean m_breedCheck = false;

    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pet_modify);
        customAnimationDialog = new CustomAnimationDialog(PetModifyActivity.this);

        binding.modifyDatepicker.init(binding.modifyDatepicker.getYear(), binding.modifyDatepicker.getMonth(), binding.modifyDatepicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //데이트피커
                        if((monthOfYear+1) < 10){
                            datepicker = year+""+"0"+(monthOfYear+1)+dayOfMonth;
                            if(dayOfMonth <10){
                                datepicker = year+""+"0"+(monthOfYear+1)+"0"+dayOfMonth;
                            }
                        }else{
                            datepicker = year+""+(monthOfYear+1)+""+dayOfMonth;
                            if(dayOfMonth < 10){
                                datepicker = year+""+(monthOfYear+1)+""+"0"+dayOfMonth;
                            }
                        }

                        //나이계산
                        ageYear = (currentYear - year);
                        ageMonth = (currentMonth - monthOfYear);
                        ageDay = (currentDate - dayOfMonth);
                        if(ageYear < 0){
                            ageYear = 0;
                            binding.pmEtPetage.setText(String.valueOf(ageYear));
                            binding.pmCheckPetage.setText("올바른 년도를 입력해주세요");
                            binding.pmCheckPetage.setTextColor(Color.parseColor("#E53A40"));
                            m_ageCheck = false;
                        }else if (ageYear == 0){
                            if(ageMonth < 0){
                                ageYear = 0;
                                binding.pmEtPetage.setText(String.valueOf(ageYear));
                                binding.pmCheckPetage.setText("올바른 년도를 입력해주세요");
                                binding.pmCheckPetage.setTextColor(Color.parseColor("#E53A40"));
                                m_ageCheck = false;
                            }else if (ageMonth == 0){
                                if(ageDay < 0){
                                    ageYear = 0;
                                    binding.pmEtPetage.setText(String.valueOf(ageYear));
                                    binding.pmCheckPetage.setText("올바른 년도를 입력해주세요");
                                    binding.pmCheckPetage.setTextColor(Color.parseColor("#E53A40"));
                                    m_ageCheck = false;
                                }else if (ageDay == 0){
                                    ageYear = 0;
                                    binding.pmEtPetage.setText(String.valueOf(ageYear));
                                    binding.pmCheckPetage.setText("");
                                    m_ageCheck = true;
                                }else{
                                    ageYear = 0;
                                    binding.pmEtPetage.setText(String.valueOf(ageYear));
                                    binding.pmCheckPetage.setText("");
                                    m_ageCheck = true;
                                }
                            }else{
                                ageYear = 0;
                                binding.pmEtPetage.setText(String.valueOf(ageYear));
                                binding.pmCheckPetage.setText("");
                                m_ageCheck = true;
                            }
                        }else {
                            binding.pmEtPetage.setText(String.valueOf(ageYear));
                            binding.pmCheckPetage.setText("");
                            m_ageCheck = true;
                        }
                    }
                });

        //라디오버튼 String값 저장
        binding.pmPetradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                petseledted = radioButton.getText().toString();
                m_genderCheck = true;
            }
        });

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        // 이미지뷰 클릭시 다이알 로그
        binding.pmImgPetimg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogInterface.OnClickListener petcamera = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        doTakePhotoAction();
                    }
                };
                DialogInterface.OnClickListener petalbum = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener petcancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog.Builder pet_img_Dialog = new AlertDialog.Builder(PetModifyActivity.this);
                pet_img_Dialog.setTitle("사진을 선택해주세요");
                pet_img_Dialog.setPositiveButton("사진활영",petcamera);
                pet_img_Dialog.setNeutralButton("앨범선택",petalbum);
                pet_img_Dialog.setNegativeButton("취소",petcancel);
                pet_img_Dialog.show();
            }
        });

        //펫 스피너
        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner_pet_kind, android.R.layout.simple_spinner_dropdown_item);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.pmSpinPetkind.setAdapter(adspin1);

        binding.pmSpinPetkind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(adspin1.getItem(position).equals("강아지")){
                    adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_dog_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.pmSpinPetbreed.setAdapter(adspin2);
                    boolean m_kindCheck = true;
                    boolean m_breedCheck = true;
                } else if(adspin1.getItem(position).equals("고양이")) {
                    adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_cat_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.pmSpinPetbreed.setAdapter(adspin2);
                    boolean m_kindCheck = true;
                    boolean m_breedCheck = true;
                } else if(adspin1.getItem(position).equals("햄스터")) {
                    adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_hamster_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.pmSpinPetbreed.setAdapter(adspin2);
                    boolean m_kindCheck = true;
                    boolean m_breedCheck = true;
                } else if(adspin1.getItem(position).equals("고슴도치")) {
                    adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_hedgehog_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.pmSpinPetbreed.setAdapter(adspin2);
                    boolean m_kindCheck = true;
                    boolean m_breedCheck = true;
                } else if(adspin1.getItem(position).equals("토끼")) {
                    adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_rabbit_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.pmSpinPetbreed.setAdapter(adspin2);
                    boolean m_kindCheck = true;
                    boolean m_breedCheck = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                boolean m_kindCheck = false;
                boolean m_breedCheck = false;
            }
        });


        //펫상세 불러오기
        String url = url_ + "/userapp/pet_detail";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest InfoForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customAnimationDialog.show();
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            if (success){
                                String[] petbreed;
                                binding.pmEtPetname.setText(jsonObject.getString("pet_name"));  //이름
                                binding.pmEtPetage.setText(jsonObject.getString("pet_age"));    //나이

                                //response에서 자릿수 추출
                                getdate = jsonObject.getString("pet_birth");
                                cutyear = getdate.substring(0,4);
                                cutmonth = getdate.substring(4,6);
                                cutday = getdate.substring(6,8);
                                //추출날짜 스트링 인티저로 변환
                                intyear = Integer.parseInt(cutyear);
                                intmonth = Integer.parseInt(cutmonth);
                                intday = Integer.parseInt(cutday);

                                if(jsonObject.isNull("pet_notice")){
                                    binding.pmEtPetnotice.setText("");
                                }else{
                                    binding.pmEtPetnotice.setText(jsonObject.getString("pet_notice"));
                                }
                                binding.modifyDatepicker.updateDate(intyear, (intmonth)-1,intday);

                                Glide.with(getApplicationContext()).load(jsonObject.getString("pet_img"))   //이미지
                                        .override(180, 180)
                                        .placeholder(R.drawable.ic_perm_identity_24px)
                                        .error(R.drawable.ic_perm_identity_24px)
                                        .into(binding.pmImgPetimg);

                                if(neutralized_female.equals(jsonObject.getString("pet_gender"))){
                                    binding.pmNeutralizedFemale.setChecked(true);
                                } else if(neutralized_male.equals(jsonObject.getString("pet_gender"))){
                                    binding.pmNeutralizedMale.setChecked(true);
                                } else if(male.equals(jsonObject.getString("pet_gender"))){
                                    binding.pmFemale.setChecked(true);
                                } else if(female.equals(jsonObject.getString("pet_gender"))){
                                    binding.pmMale.setChecked(true);
                                }

                                String[] petkind=getResources().getStringArray(R.array.spinner_pet_kind);  //스피너
                                for (int i=0;i<petkind.length;i++){
                                    if (petkind[i].trim().equals(jsonObject.getString("pet_main_type"))){
                                        binding.pmSpinPetkind.setSelection(i);
                                        switch (i){
                                            case 0:
                                                adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_dog_breed, android.R.layout.simple_spinner_dropdown_item);
                                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                binding.pmSpinPetbreed.setAdapter(adspin2);
                                                petbreed=getResources().getStringArray(R.array.spinner_dog_breed);
                                                for (int j=0;j<petbreed.length;j++){
                                                    if (petbreed[j].trim().equals(jsonObject.getString("pet_sub_type"))){
                                                        binding.pmSpinPetbreed.setSelection(j);
                                                        break;
                                                    }
                                                }
                                                break;
                                            case 1:
                                                adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_cat_breed, android.R.layout.simple_spinner_dropdown_item);
                                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                binding.pmSpinPetbreed.setAdapter(adspin2);

                                                petbreed=getResources().getStringArray(R.array.spinner_cat_breed);
                                                for (int j=0;j<petbreed.length;j++){
                                                    if (petbreed[j].trim().equals(jsonObject.getString("pet_sub_type"))){
                                                        binding.pmSpinPetbreed.setSelection(j);
                                                        break;
                                                    }
                                                }
                                                break;
                                            case 2:
                                                adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_hamster_breed, android.R.layout.simple_spinner_dropdown_item);
                                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                binding.pmSpinPetbreed.setAdapter(adspin2);
                                                petbreed=getResources().getStringArray(R.array.spinner_hamster_breed);
                                                for (int j=0;j<petbreed.length;j++){
                                                    if (petbreed[j].trim().equals(jsonObject.getString("pet_sub_type"))){
                                                        binding.pmSpinPetbreed.setSelection(j);
                                                        break;
                                                    }
                                                }
                                                break;
                                            case 3:
                                                adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_hedgehog_breed, android.R.layout.simple_spinner_dropdown_item);
                                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                binding.pmSpinPetbreed.setAdapter(adspin2);
                                                petbreed=getResources().getStringArray(R.array.spinner_hedgehog_breed);
                                                for (int j=0;j<petbreed.length;j++){
                                                    if (petbreed[j].trim().equals(jsonObject.getString("pet_sub_type"))){
                                                        binding.pmSpinPetbreed.setSelection(j);
                                                        break;
                                                    }
                                                }
                                                break;
                                            case 4:
                                                adspin2 = ArrayAdapter.createFromResource(PetModifyActivity.this, R.array.spinner_rabbit_breed, android.R.layout.simple_spinner_dropdown_item);
                                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                binding.pmSpinPetbreed.setAdapter(adspin2);
                                                petbreed=getResources().getStringArray(R.array.spinner_rabbit_breed);
                                                for (int j=0;j<petbreed.length;j++){
                                                    if (petbreed[j].trim().equals(jsonObject.getString("pet_sub_type"))){
                                                        binding.pmSpinPetbreed.setSelection(j);
                                                        break;
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                }
                                m_ageCheck = true;
                                m_nameCheck = true;
                                m_kindCheck = true;
                                m_breedCheck = true;
                                m_genderCheck = true;

                            }else {

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
                    Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요."+error.getMessage(),Toast.LENGTH_LONG).show();
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

                params.put("pet_id", getIntent().getStringExtra("pet_id"));
                return params;
            }
        };
        InfoForm.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(InfoForm);

        //펫이름 정규식
        binding.pmEtPetname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String namePattern = "^[ㄱ-ㅎ가-힣a-zA-Z]{1,8}$";   //한글,영문 최대 8자 입력가능
                Matcher matcher = Pattern.compile(namePattern).matcher(binding.pmEtPetname.getText());
                if (!matcher.matches()) {
                    binding.pmCheckPetname.setText("한글,영문만 최대 8자 입력가능합니다.");
                    binding.pmCheckPetname.setTextColor(Color.parseColor("#E53A40"));
                    m_nameCheck = false;
                } else if (matcher.matches()) {
                    binding.pmCheckPetname.setText("");
                    m_nameCheck = true;
                }
            }
        });

//        //펫생년월일 정규식
//        binding.pmEtPetbirth.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//                String agePattern = "^(19|20)[0-9]{2}(0[1-9]|1[1-2])(0[1-9]|[1-2][0-9]|3[0-1])$";
//                Matcher matcher = Pattern.compile(agePattern).matcher( binding.pmEtPetbirth.getText());
//                if (!matcher.matches()) {
//                    binding.pmCheckPetbirth.setText("생년월일 숫자 8자리를 입력해주세요.(ex.19940915)");
//                    binding.pmCheckPetbirth.setTextColor(Color.parseColor("#E53A40"));
//                    m_birthCheck = false;
//                } else if (matcher.matches()) {
//                    binding.pmCheckPetbirth.setText("");
//                    m_birthCheck = true;
//                }
//            }
//        });
//
//        //펫나이 정규식
//        binding.pmEtPetage.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//                String agePattern = "^[0-9]{1,2}$";
//                Matcher matcher = Pattern.compile(agePattern).matcher(binding.pmEtPetage.getText());
//                if (!matcher.matches()) {
//                    binding.pmCheckPetage.setText("나이 최대 2자리 숫자를 입력해주세요");
//                    binding.pmCheckPetage.setTextColor(Color.parseColor("#E53A40"));
//                    m_ageCheck = false;
//                } else if (matcher.matches()) {
//                    binding.pmCheckPetage.setText("");
//                    m_ageCheck = true;
//                }
//            }
//        });

        //수정버튼
        //클릭시 변경된값 서버전송
        binding.pmBtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener petmodify_Y = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        customAnimationDialog.show();
                        if(m_ageCheck && m_nameCheck && m_breedCheck && m_kindCheck && m_genderCheck) {
                            String url = url_ + "/userapp/pet_update";
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            StringRequest ModifyForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            boolean success = false;
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                success = jsonObject.getBoolean("result");
                                                if (success) {
                                                    customAnimationDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getApplicationContext(), PetListActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                } else {
                                                    //TODO:
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
                                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요." + error.getMessage(), Toast.LENGTH_LONG).show();
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

                                    String pet_image_name;
                                    if (filePath == null) {
                                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                                        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
                                        pet_image_name = imageFileName;
                                    } else {
                                        pet_image_name = getFileName(filePath);
                                    }
                                    //TODO: 사진이변경되었을때 예외처리
                                    if(bitmap != null){
                                        String pm_img_petimg = getStringImage(bitmap);
                                        params.put("pet_img_name", pet_image_name);
                                        params.put("pet_img", pm_img_petimg);
                                    }
                                    String text1 =  binding.pmSpinPetkind.getSelectedItem().toString();
                                    String text2 = binding.pmSpinPetbreed.getSelectedItem().toString();

                                    params.put("pet_id", getIntent().getStringExtra("pet_id"));
                                    params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                                    params.put("pet_name", binding.pmEtPetname.getText().toString());
                                    params.put("pet_birth", datepicker);
                                    params.put("pet_age", binding.pmEtPetage.getText().toString());
                                    params.put("pet_gender", petseledted);
                                    params.put("pet_notice", binding.pmEtPetnotice.getText().toString());
                                    params.put("pet_main_type", text1);
                                    params.put("pet_sub_type", text2);
                                    return params;
                                }
                            };
                            ModifyForm.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            queue.add(ModifyForm);
                        } else {
                            customAnimationDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "반려동물의 정보를 정확히 입력해주세요", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                DialogInterface.OnClickListener petmodify_N = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog.Builder pet_modify_Dialog = new AlertDialog.Builder(PetModifyActivity.this);
                pet_modify_Dialog.setTitle("펫정보를 수정하시겠습니까?");
                pet_modify_Dialog.setPositiveButton("수정",petmodify_Y);
                pet_modify_Dialog.setNegativeButton("취소",petmodify_N);
                pet_modify_Dialog.show();
            }
        });

        //삭제버튼
        //삭제시 다이얼로그 띄워서 한번더 확인
        binding.pmBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener petdelete_Y = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = url_ + "/userapp/pet_destroy";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest DeleteForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        boolean success = false;
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            success = jsonObject.getBoolean("result");
                                            if (success){
                                                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getApplicationContext(), PetListActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }else {

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요."+error.getMessage(),Toast.LENGTH_LONG).show();
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

                                params.put("pet_id", getIntent().getStringExtra("pet_id"));
                                params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));
                                return params;
                            }
                        };
                        DeleteForm.setRetryPolicy(new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(DeleteForm);
                    }
                };
                DialogInterface.OnClickListener petdelete_N = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog.Builder pet_delete_Dialog = new AlertDialog.Builder(PetModifyActivity.this);
                pet_delete_Dialog.setTitle("펫을 삭제하시겠습니까?");
                pet_delete_Dialog.setPositiveButton("삭제",petdelete_Y);
                pet_delete_Dialog.setNegativeButton("취소",petdelete_N);
                pet_delete_Dialog.show();
            }
        });
    }

    // 갤러리에서 이미지선택되면 가지고 오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.pmImgPetimg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //사진촬영 이미지 선택되면 가지고 오기
        switch (requestCode) {
            case GET_CAMERA_IMAGE1:
                if (resultCode == RESULT_OK && data.hasExtra("data")) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        binding.pmImgPetimg.setImageBitmap(bitmap);
                    }
                }
                break;
        }
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

    //이미지 리사이즈
    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }
            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }

    //카메라
    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기

    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Intent.createChooser(intent, "image"), GET_CAMERA_IMAGE1);
    }
    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(Intent.createChooser(intent, "image"), GET_GALLERY_IMAGE1);
    }

    //이미지 이름 가지고 오기
    String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //이미지 인코딩에 문자열로 만들기
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return encodedImage;
    }

    //이미지 회전
    //권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }
    @Override
    public void onBackPressed(){
        backPressed.backPressedAlert();
    }
}
