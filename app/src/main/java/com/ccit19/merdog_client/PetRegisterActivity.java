package com.ccit19.merdog_client;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.ccit19.merdog_client.backServ.BackPressed;
import com.ccit19.merdog_client.backServ.SaveSharedPreference;
import com.ccit19.merdog_client.backServ.urlset;
import com.ccit19.merdog_client.databinding.ActivityPetRegisterBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

public class PetRegisterActivity extends AppCompatActivity {
    ActivityPetRegisterBinding binding;
    BackPressed backPressed=new BackPressed(this);
    ArrayAdapter<CharSequence> adspin1, adspin2;
    String datepicker;

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    int currentDate = Calendar.getInstance().get(Calendar.DATE);
    int ageYear = 0;
    int ageMonth = 0;
    int ageDay = 0;

    final String TAG = getClass().getSimpleName();
    private final int GET_GALLERY_IMAGE1 = 0,GET_CAMERA_IMAGE1 = 2;
    private Bitmap bitmap;
    private Uri filePath;
    private ArrayAdapter<String> arrayAdapter;
    private String petseledted;
    private CustomAnimationDialog customAnimationDialog;

    //펫등록 체크변수
    boolean nameCheck = false;
    boolean birthCheck = false;
    boolean ageCheck = false;
    boolean genderCheck = false;
    boolean kindCheck = false;
    boolean breedCheck = false;

    String url_ = urlset.getInstance().getData();
    // 갤러리에서 이미지선택되면 가지고 오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.petImg.setImageBitmap(bitmap);
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
                        binding.petImg.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pet_register);
        binding.setActivity(this);
        customAnimationDialog = new CustomAnimationDialog(PetRegisterActivity.this);

        binding.petDatePicker.init(binding.petDatePicker.getYear(), binding.petDatePicker.getMonth(), binding.petDatePicker.getDayOfMonth(),
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
                    binding.petAge.setText(String.valueOf(ageYear));
                    binding.checkPetage.setText("올바른 년도를 입력해주세요");
                    binding.checkPetage.setTextColor(Color.parseColor("#E53A40"));
                    ageCheck = false;
                }else if (ageYear == 0){
                    if(ageMonth < 0){
                        ageYear = 0;
                        binding.petAge.setText(String.valueOf(ageYear));
                        binding.checkPetage.setText("올바른 년도를 입력해주세요");
                        binding.checkPetage.setTextColor(Color.parseColor("#E53A40"));
                        ageCheck = false;
                    }else if (ageMonth == 0){
                        if(ageDay < 0){
                            ageYear = 0;
                            binding.petAge.setText(String.valueOf(ageYear));
                            binding.checkPetage.setText("올바른 년도를 입력해주세요");
                            binding.checkPetage.setTextColor(Color.parseColor("#E53A40"));
                            ageCheck = false;
                        }else if (ageDay == 0){
                            ageYear = 0;
                            binding.petAge.setText(String.valueOf(ageYear));
                            binding.checkPetage.setText("");
                            ageCheck = true;
                        }else{
                            ageYear = 0;
                            binding.petAge.setText(String.valueOf(ageYear));
                            binding.checkPetage.setText("");
                            ageCheck = true;
                        }
                    }else{
                        ageYear = 0;
                        binding.petAge.setText(String.valueOf(ageYear));
                        binding.checkPetage.setText("");
                        ageCheck = true;
                    }
                }else {
                    binding.petAge.setText(String.valueOf(ageYear));
                    binding.checkPetage.setText("");
                    ageCheck = true;
                }
            }
        });

        setSupportActionBar(binding.prToolbar);

        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setTitle("펫 등록");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);

        //펫 스피너
        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner_pet_kind, android.R.layout.simple_spinner_dropdown_item);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.petKind.setAdapter(adspin1);

        binding.petKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(adspin1.getItem(position).equals("강아지")){
                    adspin2 = ArrayAdapter.createFromResource(PetRegisterActivity.this, R.array.spinner_dog_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.petBreed.setAdapter(adspin2);
                    kindCheck = true;
                    breedCheck =true;
                } else if(adspin1.getItem(position).equals("고양이")) {
                    adspin2 = ArrayAdapter.createFromResource(PetRegisterActivity.this, R.array.spinner_cat_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.petBreed.setAdapter(adspin2);
                    kindCheck = true;
                    breedCheck =true;
                } else if(adspin1.getItem(position).equals("햄스터")) {
                    adspin2 = ArrayAdapter.createFromResource(PetRegisterActivity.this, R.array.spinner_hamster_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.petBreed.setAdapter(adspin2);
                    kindCheck = true;
                    breedCheck =true;
                } else if(adspin1.getItem(position).equals("고슴도치")) {
                    adspin2 = ArrayAdapter.createFromResource(PetRegisterActivity.this, R.array.spinner_hedgehog_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.petBreed.setAdapter(adspin2);
                    kindCheck = true;
                    breedCheck =true;
                } else if(adspin1.getItem(position).equals("토끼")) {
                    adspin2 = ArrayAdapter.createFromResource(PetRegisterActivity.this, R.array.spinner_rabbit_breed, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.petBreed.setAdapter(adspin2);
                    kindCheck = true;
                    breedCheck =true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                kindCheck = false;
                breedCheck =false;
            }
        });

        //radiogroup에서 선택된 아이디값을 변수에 저장
        binding.petradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                petseledted = radioButton.getText().toString();
                genderCheck = true;
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
        binding.petImg.setOnClickListener(new View.OnClickListener() {
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
                AlertDialog.Builder petD = new AlertDialog.Builder(PetRegisterActivity.this);
                petD.setTitle("사진을 선택해주세요");
                petD.setPositiveButton("사진활영",petcamera);
                petD.setNeutralButton("앨범선택",petalbum);
                petD.setNegativeButton("취소",petcancel);
                petD.show();
            }
        });

        //펫이름 정규식
        binding.petName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String namePattern = "^[ㄱ-ㅎ가-힣a-zA-Z]{1,8}$";   //한글,영문 최대 8자 입력가능
                Matcher matcher = Pattern.compile(namePattern).matcher(binding.petName.getText());
                if (!matcher.matches()) {
                    binding.checkPetname.setText("한글,영문만 최대 8자 입력가능합니다.");
                    binding.checkPetname.setTextColor(Color.parseColor("#E53A40"));
                    nameCheck = false;
                } else if (matcher.matches()) {
                    binding.checkPetname.setText("");
                    nameCheck = true;
                }
            }
        });

//        //펫생년월일 정규식
//        binding.petBirth.addTextChangedListener(new TextWatcher() {
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
//                String agePattern = "^(19|20)[0-9]{2}([1-9][1-9]|1[1-2])(0[1-9]|[1-2][0-9]|3[0-1])$";
//                Matcher matcher = Pattern.compile(agePattern).matcher(binding.petBirth.getText());
//                if (!matcher.matches()) {
//                    binding.checkPetbirth.setText("생년월일 숫자 8자리를 입력해주세요.(ex.19940915)");
//                    binding.checkPetbirth.setTextColor(Color.parseColor("#E53A40"));
//                    birthCheck = false;
//                } else if (matcher.matches()) {
//                    binding.checkPetbirth.setText("");
//                    birthCheck = true;
//                }
//            }
//        });

//        //펫나이 정규식
//        binding.petAge.addTextChangedListener(new TextWatcher() {
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
//                Matcher matcher = Pattern.compile(agePattern).matcher(binding.petAge.getText());
//                if (!matcher.matches()) {
//                    binding.checkPetage.setText("나이 최대 2자리 숫자를 입력해주세요");
//                    binding.checkPetage.setTextColor(Color.parseColor("#E53A40"));
//                    ageCheck = false;
//                } else if (matcher.matches()) {
//                    binding.checkPetage.setText("");
//                    ageCheck = true;
//                }
//            }
//        });

        //등록버튼
        binding.bPetadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener petreg_Y = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customAnimationDialog.show();
                        if(ageCheck && nameCheck && kindCheck && breedCheck && genderCheck){
                            String url = url_ + "/userapp/pet_register";
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


                            StringRequest regitForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    boolean success = false;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        success = jsonObject.getBoolean("result");
                                        if (success) {
                                            customAnimationDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), PetListActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
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

                                    String pet_image_name;
                                    if(filePath == null) {
                                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                                        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
                                        pet_image_name = imageFileName;
                                    }else {
                                        pet_image_name = getFileName(filePath);
                                    }

                                    String text1 = binding.petKind.getSelectedItem().toString();
                                    String text2 = binding.petBreed.getSelectedItem().toString();

                                    /* Create request */
                                    params.put("user_id", SaveSharedPreference.getUserIdx(getApplicationContext()));

                                    if(bitmap != null){     //사진등록을 안하면 이미지업로드x
                                        String pet_image = getStringImage(bitmap);
                                        params.put("pet_img_name", pet_image_name);
                                        params.put("pet_img", pet_image);
                                    }

                                    params.put("pet_name", binding.petName.getText().toString());
//                    params.put("pet_birth", binding.petBirth.getText().toString());
                                    params.put("pet_birth", datepicker);
                                    params.put("pet_age", binding.petAge.getText().toString());
                                    params.put("pet_gender", petseledted);
                                    params.put("pet_notice", binding.petNotice.getText().toString());
                                    params.put("pet_main_type", text1);
                                    params.put("pet_sub_type", text2);
                                    return params;
                                }
                            };
                            regitForm.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            queue.add(regitForm);

                        } else if(!nameCheck){  //순서대로
                            customAnimationDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "펫이름을 입력해주세요.", Toast.LENGTH_LONG).show();
                        } else if(!ageCheck){
                            customAnimationDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "올바른 생년월일을 입력해주세요.", Toast.LENGTH_LONG).show();
                        }  else if(!genderCheck){
                            customAnimationDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "펫성별을 입력해주세요.", Toast.LENGTH_LONG).show();
                        } else if(!kindCheck){
                            customAnimationDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "펫종류를 입력해주세요.", Toast.LENGTH_LONG).show();
                        } else if(!breedCheck){
                            customAnimationDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "펫품종을 입력해주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                DialogInterface.OnClickListener petreg_N = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog.Builder petreg_Dialog = new AlertDialog.Builder(PetRegisterActivity.this);
                petreg_Dialog.setTitle("펫을 등록하시겠습니까?");
                petreg_Dialog.setPositiveButton("등록",petreg_Y);
                petreg_Dialog.setNegativeButton("취소",petreg_N);
                petreg_Dialog.show();
            }
        });

    }


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

    //권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    // 현재 날짜(년/월/일 시/분/초)
    public String doYearMonthDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }

    //나이계산
    public int calculateAgeForKorean(String ssn) { // ssn의 형식은 yyyymmdd 임

        String today = ""; // 오늘 날짜
        int manAge = 0; // 만 나이

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        today = formatter.format(new Date()); // 시스템 날짜를 가져와서 yyyyMMdd 형태로 변환

        // today yyyyMMdd
        int todayYear = Integer.parseInt(today.substring(0, 4));
        int todayMonth = Integer.parseInt(today.substring(4, 6));
        int todayDay = Integer.parseInt(today.substring(6, 8));

        int ssnYear = Integer.parseInt(ssn.substring(0, 4));
        int ssnMonth = Integer.parseInt(ssn.substring(4, 6));
        int ssnDay = Integer.parseInt(ssn.substring(6, 8));


        manAge = todayYear - ssnYear;

        if (todayMonth < ssnMonth) { // 생년월일 "월"이 지났는지 체크
            manAge--;
        } else if (todayMonth == ssnMonth) { // 생년월일 "일"이 지났는지 체크
            if (todayDay < ssnDay) {
                manAge--; // 생일 안지났으면 (만나이 - 1)
            }
        }

        return manAge + 1; // 한국나이를 측정하기 위해서 +1살 (+1을 하지 않으면 외국나이 적용됨)
    }


    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, 101);
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

    @Override
    public void onBackPressed(){
        backPressed.backPressedAlert();
    }
}