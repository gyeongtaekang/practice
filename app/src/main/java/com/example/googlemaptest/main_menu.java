package com.example.googlemaptest;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
/*네비게이션바를 위한*/
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.example.googlemaptest.R;

/*네비게이션바를 위한*/

public class main_menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;//네비게이션 바를 위한

    /*네비게이션 바*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 네비게이션 항목 클릭 처리
        int id = item.getItemId();

        if (id == R.id.nav_item_one) {
            // 항목 1 클릭 시 처리,
            Intent intent = new Intent(main_menu.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_item_two) {
            // 항목 2 클릭 시 처리
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        /*네비게이션바를 위한*/
        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("DeliveryShare");//메뉴에서 앱 제목

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            // 첫 번째 아이템이 기본으로 선택되게 함
            navigationView.setCheckedItem(R.id.nav_item_one);
        }
        /*네비게이션바를 위한*/



    }
}