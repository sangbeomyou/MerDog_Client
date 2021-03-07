package com.ccit19.merdog_client.backServ;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_USER_IDX = "user_idx";
    static final String PREF_USER_ID = "user_id";
    static final String PREF_USER_TYPE = "m_login";
    static final String PREF_TOKEN = "m_token";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setUserInfo(Context ctx,String userIdx, String userID, int userType, String token) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_IDX, userIdx);
        editor.putString(PREF_USER_ID, userID);
        editor.putInt(PREF_USER_TYPE, userType);// 0 메인로그인, 1 카카오로그인, 2 네이버로그인
        editor.putString(PREF_TOKEN, token);
        editor.commit();
    }
    // 저장된 정보 가져오기
    public static String getUserIdx(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_IDX, "");
    }
    // 저장된 정보 가져오기
    public static String getUserID(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }
    // 저장된 정보 가져오기
    public static int getLoginType(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_USER_TYPE, 0);
    }
    public static String getToken(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_TOKEN, "");
    }
    // 로그아웃
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
