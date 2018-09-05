package com.example.yohjikusakabe.classprojecto;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Things to work on:

//Review with same name and place: Check longitude and latitude and name, if location already exists,
//        then add on to collection under username. Otherwise overwritten.
//
//        Upload pictures: Create intent with storage and then find out how to upload high quality picture to Firestore.
//
//        Download pictures async: High-res picture cause lag in the program.
//
//        Linking database to reviews: At the moment, able to read/write from database. Just need to implement it to connect to the UI. (waiting on upload picture to finish).

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextMessage;
    //    Use this to get userCoordinates later.
    private FusedLocationProviderClient mFusedLocationClient;

    private double longitude_;
    private double latitude_;
    private DocumentReference mDocRef;
    public String userNameProfile = "potato";
    private String userCity_;
    public Date date;

    public double getLatitude() {return latitude_;}
    public double getLongitude() {return longitude_;}
    public String getCity() {return userCity_;}

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentHome();
                    return true;
                case R.id.navigation_local:
                    switchToFragmentLocal();
                    return true;
                case R.id.navigation_profile:
                    switchToFragmentProfile();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find coordinates for Google Maps
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // If location is found. If not found, probably need to open Google maps if on emulator.
                            longitude_ = location.getLongitude();
                            latitude_ = location.getLatitude();
                        } else {
                            Log.e("MainActivity","Error location is null!");
                            Toast.makeText(MainActivity.this, "Location is null, please open Google Maps at least once!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//      Find city that user is in.
        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude_, longitude_, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            if (addresses.size() > 0) {
                        userCity_ = addresses.get(0).getSubLocality();
            }
        } else {
            Toast.makeText(this, "Can't find city?", Toast.LENGTH_SHORT).show();
        }

        findNearbyLocations();

        switchToFragmentHome();
        // Handles tab bar on bottom
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mDocRef = FirebaseFirestore.getInstance().document("users/" +FirebaseAuth.getInstance().getCurrentUser().getUid());
        fetchQuote();
    }
    public void fetchQuote() {
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userNameProfile = documentSnapshot.getString("Username");
                    date = documentSnapshot.getDate("Date Created");
                    Log.d("Main Activity",userNameProfile);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               Log.e("Main Activity","Failed fetch");
            }
        });
    }


    public void switchToFragmentHome() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainScreen, new HomeFragment()).commit();
    }
    public void switchToFragmentLocal() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainScreen, new LocalFragment()).commit();
    }
    public void switchToFragmentProfile() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainScreen, new ProfileFragment()).commit();
    }

    public void findNearbyLocations() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
// Debug menu!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onClick(View view) {

    }
}

