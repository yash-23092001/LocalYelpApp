package com.example.yohjikusakabe.classprojecto;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocalFragment extends Fragment {

    FirebaseFirestore db;
    View mView;
    Double myLat_;
    Double myLong_;
    LinearLayout horizontalLinear;
    String category_ = null;

    public LocalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
          db = FirebaseFirestore.getInstance();
          mView = inflater.inflate(R.layout.fragment_local, container, false);
          myLong_ = ((MainActivity)this.getActivity()).getLongitude();
          myLat_ = ((MainActivity)this.getActivity()).getLatitude();
          Bundle bundle = getArguments();
          checkNearby();

        return mView;
    }

    // Add new location to scroll view.
    public void addNewLocationThing(final String pictureURL, final String title, final String locInfo, final double lat, final double longitude, final String description) {
        horizontalLinear = mView.findViewById(R.id.linearParentScroll);
        LinearLayout newLayout = new LinearLayout(getActivity());
        ImageButton locationImage = new ImageButton(getActivity());
        TextView locationName = new TextView(getActivity());

        // Convert pixels to dp.
        int height_ = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
        int width_= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, getResources().getDisplayMetrics());
        int leftMargin_ = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

        if (pictureURL != null) {
            Glide.with(this).load(pictureURL).centerCrop().into(locationImage);
        }

        LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(
                width_,
                height_
        );
        paramsImage.setMargins(leftMargin_,0,0,0);
        locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LocationPage.class);
                intent.putExtra("Title",title);
                intent.putExtra("picURL",pictureURL);
                intent.putExtra("Address",locInfo);
                intent.putExtra("Longitude",longitude);
                intent.putExtra("Latitude",lat);
                intent.putExtra("Description",description);
                startActivity(intent);
            }
        });
        locationImage.setLayoutParams(paramsImage);
        // Set title
        locationName.setText(title);
        int black = Color.parseColor("#000000");
        locationName.setTextColor(black);
        locationName.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(leftMargin_,0,0,0);
        locationName.setLayoutParams(params);

        // Create linear view for location
        newLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        newLayout.setOrientation(LinearLayout.VERTICAL);

        newLayout.addView(locationImage);
        newLayout.addView(locationName);

        // Add new linear view to parent view.
        horizontalLinear.addView(newLayout);

    }

    public void checkNearby() {
        db.collection("reviews")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getData().get("Name").toString();
                    String picURL = document.getData().get("PictureURL").toString();
                    String locAddress = document.getData().get("Address").toString();
                    Double lat = (Double) document.getData().get("Latitude");
                    Double longitude = (Double) document.getData().get("Longitude");
                    String description = document.getData().get("Description").toString();
                    if (picURL.isEmpty()) {
                        addNewLocationThing(null,name,locAddress,lat,longitude,description);
                    } else {
                        addNewLocationThing(picURL,name,locAddress,lat,longitude,description);
                    }

                    }
                } else {
                    Log.e("checkNearbyLocal","Error retrieving documents");
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
