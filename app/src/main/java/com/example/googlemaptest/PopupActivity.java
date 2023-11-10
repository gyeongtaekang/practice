package com.example.googlemaptest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PopupActivity extends AppCompatActivity {

    TextView txtView;

    TextView txtView2;

    TextView txtView3;

    TextView txtView4;

    TextView txtView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //타이틀 바 없애기
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        // UI 객체 생성
        txtView = findViewById(R.id.textView8);
        txtView2 = findViewById(R.id.textView10);
        txtView3 = findViewById(R.id.textView12);
        txtView4 = findViewById(R.id.textView14);

        // 데이터 가져오기
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String snippet = intent.getStringExtra("snippet");
        String spinner = intent.getStringExtra("spinner");
        String website = intent.getStringExtra("website");

        // UI에 데이터 설정
        txtView.setText(title);
        txtView2.setText(snippet);
        txtView3.setText(spinner);
        txtView4.setText(website);

        // 팝업 띄우기
        //showPopupDialog();

        // 닫기 버튼 클릭 시 액티비티 종료
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.buttonGoToWebsite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL="http://".concat(txtView4.getText().toString());
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(URL));
                startActivity(i); // 웹사이트로 이동
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        return; //뒤로가기 버튼 막기
    }

}
