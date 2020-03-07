package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.todo.fragments.SettingsFragment;
import com.example.todo.fragments.TodoFragment;
import com.example.todo.utils.setteings.Settings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private AdView adView;
    private AdRequest request;

    private ArrayList<Integer> colors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSettings();
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

    private void loadSettings(){
        Settings settings = new Settings(getApplicationContext());
        colors = settings.loadBackgroundColor();
    }

    @SuppressLint("ResourceType")
    private void initView(){
        bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A03BDA52BCF46627BDA62F08CD24AA2D").addTestDevice("6A59F7B812C24D21F3C41428379D5749").build();
//        if(adRequest.isTestDevice(this)){
//            adView.loadAd(adRequest);
//            //test ad
//        }else{
//            //ad
//            initAds();
//            adView.loadAd(request);
//        }

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
//                initFragment(new NoteFragment(), true);
                Toast.makeText(getApplicationContext(), "Available in future :)", Toast.LENGTH_LONG).show();
                return false;
            case R.id.settings_item:
                initFragment(new SettingsFragment(), true);
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
