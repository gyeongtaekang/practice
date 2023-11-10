package com.example.googlemaptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;
import android.Manifest;//내 위치 추적을 위해 추가

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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private HashMap<String, Marker> markers = new HashMap<>();
    private static final int YOUR_REQUEST_CODE = 1;//내 위치 추적을 위해 추가

    private Button mapTypeButton;

    @Override    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);//내 위치 찿는 코드

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Spinner foodSpinner = findViewById(R.id.foodSpinner); /*여기서 부터 필터링하는 코드 */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.food_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodSpinner.setAdapter(spinnerAdapter);

        foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFood = parent.getItemAtPosition(position).toString();
                filterMarkers(selectedFood);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });  /*여기까지 필터링 하는 코드*/
    }
    /*네비게이션 바*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 네비게이션 항목 클릭 처리
        int id = item.getItemId();

        if (id == R.id.nav_item_one) {
            // 항목 1 클릭 시 처리
        } else if (id == R.id.nav_item_two) {
            // 항목 2 클릭 시 처리
            Intent intent = new Intent(MainActivity.this, main_menu.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
/*네비게이션 바*/

    private void filterMarkers(String food) {
        for (Marker marker : markers.values()) {
            HashMap<String, String> markerInfo = (HashMap<String, String>) marker.getTag();
            if(markerInfo != null){
                if(food.equals("모두 보기") || markerInfo.get("category").equals(food)) {
                    marker.setVisible(true); // 선택된 음식 카테고리의 마커 또는 모든 마커를 보이게 함
                } else {
                    marker.setVisible(false); // 나머지 마커는 보이지 않게 함
                }
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        //내 위치 추적을 위해 추가
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, YOUR_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        fusedLocationClient.getLastLocation() //내 위치 추적을 위해 추가
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        }
                    }
                });//내 위치 추적을 위해 추가


        LatLng SEOUL = new LatLng(35.846004, 127.14467);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() { // 클릭한 위치에 마커 생성하는 코드

            public void onMapClick(final LatLng latLng) {
                // EditText를 생성
                final EditText titleInput = new EditText(MainActivity.this);
                titleInput.setHint("제목");

                final EditText snippetInput = new EditText(MainActivity.this);
                snippetInput.setHint("내용");

                final EditText websiteInput = new EditText(MainActivity.this);
                websiteInput.setHint("오픈채팅방 주소");

                // Spinner 생성 및 설정
                final Spinner spinner = new Spinner(MainActivity.this);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.food_options_for_creation, android.R.layout.simple_spinner_item); // 수정된 부분
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);


                // LinearLayout에 EditText와 Spinner를 추가
                LinearLayout dialogLayout = new LinearLayout(MainActivity.this);
                dialogLayout.setOrientation(LinearLayout.VERTICAL);
                dialogLayout.addView(titleInput); // 제목
                dialogLayout.addView(snippetInput); // 내용
                dialogLayout.addView(websiteInput); // 웹사이트 주소
                dialogLayout.addView(spinner); // Spinner

                // AlertDialog를 생성하고 설정
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setView(dialogLayout);
                dialogBuilder.setTitle("마커 정보 입력");
                dialogBuilder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 선택한 항목에 따라 마커 아이콘 설정
                        String selected = spinner.getSelectedItem().toString();
                        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(); // 기본 아이콘
                        if (selected.equals("치킨")) {
                            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chicken);
                            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                            int width = (int) ((float) height * originalBitmap.getWidth() / originalBitmap.getHeight());
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                            icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                        } else if (selected.equals("피자")) {
                            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pizza);
                            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                            int width = (int) ((float) height * originalBitmap.getWidth() / originalBitmap.getHeight());
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                            icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                        }
                        else if (selected.equals("족발")) {
                            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jokbal);
                            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                            int width = (int) ((float) height * originalBitmap.getWidth() / originalBitmap.getHeight());
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                            icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                        }
                        else if (selected.equals("디저트")) {
                            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dissert);
                            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                            int width = (int) ((float) height * originalBitmap.getWidth() / originalBitmap.getHeight());
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                            icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                        }

                        // 마커 추가
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(titleInput.getText().toString());
                        markerOptions.snippet(snippetInput.getText().toString());
                        markerOptions.icon(icon);
                        Marker marker = mMap.addMarker(markerOptions);
                        marker.setTag(websiteInput.getText().toString()); // 마커에 태그로 웹사이트 주소 저장

                        // 마커에 태그로 웹사이트 주소와 카테고리 정보 저장
                        HashMap<String, String> markerInfo = new HashMap<>();
                        markerInfo.put("website", websiteInput.getText().toString());
                        markerInfo.put("category", spinner.getSelectedItem().toString());
                        marker.setTag(markerInfo);

                        // 모든 마커를 리스트에 저장
                        markers.put(marker.getId(), marker);


                    }
                });

                dialogBuilder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // AlertDialog를 보여줌
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        // InfoWindow 클릭 리스너 설정
       /* mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(final Marker marker) {
                // 여기서 AlertDialog.Builder를 생성하고 설정
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("이 마커를 삭제하거나 웹사이트로 이동하시겠습니까?");

                // 삭제 버튼 추가
                builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.remove(); // 마커 삭제
                        markers.remove(marker.getId()); // HashMap에서 마커 정보 삭제
                        dialog.dismiss(); // 대화 상자 닫기
                    }
                });

                // 웹사이트로 이동 버튼 추가
                builder.setPositiveButton("웹사이트로 이동", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 마커의 tag에서 웹사이트 정보 가져오기
                        HashMap<String, String> markerInfo = (HashMap<String, String>) marker.getTag();
                        if (markerInfo != null) {
                            String url = markerInfo.get("website");
                            if (url != null && !url.isEmpty()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                startActivity(intent); // 웹사이트로 이동
                            }
                        }
                    }
                });

                // 대화 상자 표시
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });*/
        // InfoWindow 클릭 리스너 설정
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                HashMap<String, String> markerInfo = (HashMap<String, String>) marker.getTag();

                if (markerInfo != null) {
                    Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                    intent.putExtra("title", marker.getTitle());
                    intent.putExtra("snippet", marker.getSnippet());
                    intent.putExtra("website", markerInfo.get("website"));
                    intent.putExtra("spinner", markerInfo.get("category"));
                    startActivity(intent);
                }
            }

        });


        mMap.getUiSettings().setZoomControlsEnabled(true);
/*        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));                 // 초기 위치*/
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));                         // 줌의 정도
       /* googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);*/            // 위성타입 지도 설정
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);                //지도타입 지도
    }

}