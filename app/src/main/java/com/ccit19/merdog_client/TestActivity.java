package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ccit19.merdog_client.databinding.ActivityTestBinding;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;
    //private SessionCallback mKakaocallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_test);
        binding.setActivity(this);
        binding.comKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isKakaoLogin();
            }
        });

    }
    /*private void isKakaoLogin() {
        // 카카오 세션을 오픈한다
        mKakaocallback = new SessionCallback();
        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
        com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, TestActivity.this);
    }

    protected void KakaorequestMe() {

        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;

                if (ErrorCode == ClientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(getApplicationContext(), "세션닫힘", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                String profileUrl = userProfile.getProfileImagePath();
                String userId = String.valueOf(userProfile.getId());
                String userName = userProfile.getNickname();
                Toast.makeText(getApplicationContext(), "성공?", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(getApplicationContext(), "자동가입?", Toast.LENGTH_SHORT).show();
                // 자동가입이 아닐경우 동의창
            }
        });
    }



    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            Toast.makeText(getApplicationContext(), "정보?", Toast.LENGTH_SHORT).show();
            KakaorequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Toast.makeText(getApplicationContext(), "세션열기 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}
