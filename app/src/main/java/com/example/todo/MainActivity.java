package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Service;
import android.os.Build;
import android.os.Bundle;

import android.Manifest;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.todo.BuildConfig.APPLICATION_ID;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private AdView adView;
    private AdRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= 10)
            getSupportActionBar().hide();
        else
            getActionBar().hide();

        getAllPermission();
        initView();
        initFragment(new TodoFragment(getApplicationContext()), false);
    }

    private void initAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener(){

            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                    request = new AdRequest.Builder()
                        .build();
            }
        });
    }

    private void getAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
        }
    }

    private void initView(){
        bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A03BDA52BCF46627BDA62F08CD24AA2D").build();
        if(adRequest.isTestDevice(this)){
            adView.loadAd(adRequest);
            //test ad
        }else{
            //ad
            initAds();
            adView.loadAd(request);
        }

        Log.d("MY_ADDS", "initView: " + adRequest.isTestDevice(getApplicationContext()));

    }

    public void initFragment(final Fragment fragment, boolean addToBackStage){
        this.fragment = fragment;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.containerFragment, fragment);

        if(addToBackStage)
            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    public void closeFragment(Fragment oldFragment, Fragment newFragment){
        assert fragment.getFragmentManager() != null;
        fragment.getFragmentManager().beginTransaction().remove(oldFragment).replace(R.id.containerFragment, newFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.todo_item:
                initFragment(new TodoFragment(getApplicationContext()), true);
                return true;
            case R.id.note_item:
                initFragment(new NoteFragment(), true);
                return true;
            case R.id.settings_item:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                initFragment(new TodoFragment(getApplicationContext()), true);
                return true;
                default: break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
