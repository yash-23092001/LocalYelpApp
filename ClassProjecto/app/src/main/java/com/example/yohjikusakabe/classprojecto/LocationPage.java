package com.example.yohjikusakabe.classprojecto;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LocationPage extends AppCompatActivity {

    private String locationName;
    private FirebaseFirestore db;
    private Float averageRating = 0.0f;
    private String locInfo;
    private String locationReview_;
    private int numReviews = 0;
    private double myLat_;
    private double myLong_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_page);
        final RatingBar ratingBar = findViewById(R.id.averageRatingLocationPage);
        final TextView locationNameView = findViewById(R.id.nameLocationPage);
        final TextView locationInfo = findViewById(R.id.LocationInfo);
        final TextView locationReview = findViewById(R.id.descriptionLocation);
        ImageView images = findViewById(R.id.imageViewLocation);
        ImageView mapImage = findViewById(R.id.mapImage);

        db = FirebaseFirestore.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            locationName = extras.getString("Title");
            locInfo = extras.getString("Address");

            String pictureURL = extras.getString("picURL");
            myLat_ = extras.getDouble("Latitude");
            myLong_ = extras.getDouble("Longitude");
            locationReview_ = extras.getString("Description");
            Glide.with(this).load(pictureURL).centerCrop().into(images);
            Glide.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=" + myLat_ + "," + myLong_ + "&zoom=15&size=400x400&markers=color:blue%7Clabel:S%7C" + myLat_ +"," + myLong_ + "&key=AIzaSyDelAUGT2H6VC58Qt5Qi_vIy9RZ483L8Vc").centerCrop().into(mapImage);
        }

        db.collection("reviews/" + locationName +"/userReviews")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String review = document.getData().get("Review").toString();
                        double rating = (double)document.getData().get("Rating");
                        float ratingFloat = (float)rating;
                        String reviewer = document.getData().get("Reviewer").toString();
                        addNewReview(reviewer,review,ratingFloat);
                        averageRating = averageRating + ratingFloat;
                        numReviews++;
                    }
                    Float newAverage = averageRating / numReviews;
                    ratingBar.setRating(newAverage);
                    locationNameView.setText(locationName);
                    locationInfo.setText(locInfo);
                    locationReview.setText(locationReview_);
                } else {
                    Log.e("checkNearbyLocal","Error retrieving documents");
                }
            }
        });

    }

    private int numberToDP(int number) {
        int relativeHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, number, getResources().getDisplayMetrics());
        return relativeHeight;
    }

    private void addNewReview(String reviewer,String review, Float rating) {
        RelativeLayout r = new RelativeLayout(this);
        TextView newRating = new TextView(this);
        TextView reviewerName = new TextView(this);
        TextView theReview = new TextView(this);
        // Reviewer params
        reviewerName.setText(reviewer);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        reviewerName.setLayoutParams(params);
        reviewerName.setId(View.generateViewId());

        //Rating Bar
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params3.addRule(r.ALIGN_PARENT_TOP);
        params3.addRule(r.ALIGN_PARENT_END);
        newRating.setText(rating + " out of 5.0");
        newRating.setLayoutParams(params3);

        // Review params
        theReview.setText(review);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        int marginTen = numberToDP(10);
        params2.setMargins(0,marginTen,0,0);
        params2.addRule(r.BELOW,reviewerName.getId());
        params2.addRule(r.ALIGN_PARENT_BOTTOM);
        params2.addRule(r.ALIGN_PARENT_START);
        theReview.setLayoutParams(params2);

        // Relative Layout
        RelativeLayout.LayoutParams paramsRelativeLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        r.setPadding(marginTen,marginTen,marginTen,0);
        r.setLayoutParams(paramsRelativeLayout);
        r.addView(reviewerName);
        r.addView(theReview);
        r.addView(newRating);

        // Line for LinearLayout
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                numberToDP(1)
        );
        lineParams.setMargins(35,0,35,0);
        textView.setLayoutParams(lineParams);
        int colorAccent = Color.parseColor("#FFE2DFA2");
        textView.setTextColor(colorAccent);
        // Add Linear layout to parent
        LinearLayout parent = findViewById(R.id.parentLinearLocationPage);
        parent.addView(r);
        parent.addView(textView);
    }

}
