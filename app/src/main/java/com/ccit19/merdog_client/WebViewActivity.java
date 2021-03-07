package com.ccit19.merdog_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.ccit19.merdog_client.backServ.urlset;

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    String about;
    Button check;
    String url_ = urlset.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        about=getIntent().getStringExtra("about");

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING); // 컨텐츠 사이즈 맞추기
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        Toolbar toolbar =findViewById(R.id.wv_toolbar);
        setSupportActionBar(toolbar);
        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        if (about.equals("privacy")){
            getSupportActionBar().setTitle("서비스 이용약관");
            mWebView.loadUrl(url_ + "/policy/privacy"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        }else {
            getSupportActionBar().setTitle("개인정보 처리방침");
            mWebView.loadUrl(url_ + "/policy/service"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_24px);
        //check=findViewById(R.id.priCheck);





    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
            {
                // 해당 버튼을 눌렀을 때 적절한 액션을 넣는다.
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
