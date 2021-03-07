package com.ccit19.merdog_client.backServ;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class BackPressed {
    private long backKeyPressedTime = 0;    // '뒤로' 버튼을 클릭했을 때의 시간
    private long TIME_INTERVAL = 2000;      // 첫번째 버튼 클릭과 두번째 버튼 클릭 사이의 종료를 위한 시간차를 정의
    private Toast toast;                    // 종료 안내 문구 Toast
    private Activity activity;              // 종료할 액티비티의 Activity 객체

    public BackPressed(Activity _activity) {
        this.activity = _activity;
    }

    // 종료할 액티비티에서 호출할 함수
    public void backPressedTwice() {

        // '뒤로' 버튼 클릭 시간과 현재 시간을 비교 게산한다.

        // 마지막 '뒤로'버튼 클릭 시간이 이전 '뒤로'버튼 클릭시간과의 차이가 TIME_INTERVAL(여기서는 2초)보다 클 때 true
        if (System.currentTimeMillis() > backKeyPressedTime + TIME_INTERVAL) {
            // 현재 시간을 backKeyPressedTime에 저장한다.
            backKeyPressedTime = System.currentTimeMillis();
            // 종료 안내문구를 노출한다.
            showMessage();
        }else{
            // 마지막 '뒤로'버튼 클릭시간이 이전 '뒤로'버튼 클릭시간과의 차이가 TIME_INTERVAL(2초)보다 작을때
            // Toast가 아직 노출중이라면 취소한다.
            toast.cancel();
            activity.finish();
        }
    }

    public void showMessage() {
        toast = Toast.makeText(activity, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
    public void backPressedAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("입력하신 내용이 사라집니다.\n정말로 뒤로 가시겠습니까?")
                .setTitle("알림!");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                activity.finish();
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
