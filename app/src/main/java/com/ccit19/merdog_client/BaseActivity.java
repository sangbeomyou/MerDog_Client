package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public void progressON() {
        BaseApplication.getInstance().progressON(this, null);
    }

    public void progressON(String message) {
        BaseApplication.getInstance().progressON(this, message);
    }

    public void progressOFF() {
        BaseApplication.getInstance().progressOFF();
    }

}


