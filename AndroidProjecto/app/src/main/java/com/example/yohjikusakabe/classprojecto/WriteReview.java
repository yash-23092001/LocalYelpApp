package com.example.yohjikusakabe.classprojecto;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WriteReview extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private String locationName_;
    private String locationType_;
    private String postalAddress_;
//    private String phoneNumber_;
    private String reviewText_;
    private String userName_;
    private String pictureURL_;
    private String description_;
    private Float rating_;
    private GeoPoint coordinates_;
    Double longitude_ = 0.0;
    Double latitude_ = 0.0;

    private DocumentReference mDocRef;
    private Boolean writeReviewCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // If found from Search.
        TextView writeReview = findViewById(R.id.newSpotText);
        EditText locationName2 = findViewById(R.id.locationNameWriteReview);

        Bundle bundle = getIntent().getExtras();
        // Then search used.
        if (bundle != null) {
            writeReview.setText("Write a review");
            String name = bundle.getString("Name");
            locationName2.setText(name);
            writeReviewCounter = true;
            EditText description = findViewById(R.id.descriptionLocation);
            description.setVisibility(View.GONE);
            EditText address = findViewById(R.id.locationAddressWriteReview);
            address.setVisibility(View.GONE);
        }


        Spinner spinner = findViewById(R.id.typeSpinner);
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("Food");
        categories.add("Fun Stuff");
        categories.add("Transportation");
        categories.add("Event");
//        categories.add("Nightlife");
//        categories.add("Leisure");
//        categories.add("Other Stuff");
//        categories.add("Shopping");
//      Set up spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
//      Buttons
        Button submitReview = findViewById(R.id.submitReview);
        submitReview.setOnClickListener(this);

        Button uploadImage = findViewById(R.id.uploadImage);
        uploadImage.setOnClickListener(this);
//        EditTexts
//        Find CurrentUserName
        mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userName_ = documentSnapshot.getString("Username");
                    Log.d("Main Activity",userName_);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Main Activity","Failed fetch");
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item for category
        locationType_ = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
//    Break this into smaller functions later.
    @Override
    public void onClick(View view) {

        if (view == findViewById(R.id.uploadImage)) {

        }
        if (view == findViewById(R.id.submitReview)) {
//          Retrieve editText info
            final EditText locationName = findViewById(R.id.locationNameWriteReview);
            EditText locationAddress = findViewById(R.id.locationAddressWriteReview);
//            EditText phoneNumber = findViewById(R.id.phoneNumberWriteReview);
            EditText reviewText = findViewById(R.id.reviewWriteReview);
            EditText pictureURL = findViewById(R.id.PictureURLWriteReview);
            EditText description = findViewById(R.id.descriptionLocation);
            RatingBar ratingBar = findViewById(R.id.ratingBarWrite);

            locationName_ = locationName.getText().toString().trim();
            postalAddress_ = locationAddress.getText().toString().trim();
            reviewText_ = reviewText.getText().toString().trim();
            pictureURL_ = pictureURL.getText().toString().trim();
            description_ = description.getText().toString().trim();
            rating_ = ratingBar.getRating();

            if (TextUtils.isEmpty(locationName_) && writeReviewCounter == false) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(postalAddress_) && writeReviewCounter == false) {
                Toast.makeText(this, "Please enter a address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(reviewText_) && writeReviewCounter == false) {
                Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show();
                return;
            }

            // Finds Coords using address
            Geocoder geocoder = new Geocoder(WriteReview.this);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(postalAddress_, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            longitude_ = 0.0;
            latitude_ = 0.0;
            String city_ = null;
            if (writeReviewCounter == false) {
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    longitude_ = address.getLongitude();
                    latitude_ = address.getLatitude();
                    city_ = address.getLocality();
                    coordinates_ = new GeoPoint(latitude_,longitude_);
                } else {
                    Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            // Upload picture (optional)

            mDocRef = FirebaseFirestore.getInstance().document("reviews/" + locationName_);

            final Map<String, Object> locationData = new HashMap<>();
            locationData.put("Name",locationName_);
            locationData.put("Category",locationType_);
            locationData.put("Address",postalAddress_);
            locationData.put("Coordinates", coordinates_);
            locationData.put("Latitude",latitude_);
            locationData.put("Longitude",longitude_);
            locationData.put("Locality", city_);
            locationData.put("PictureURL", pictureURL_);

            // Only change description is entered.
            locationData.put("Description", description_);


            // Deal with two locations with the same name here. CAN USE .add instead of .set for random ID. ***** Also so we don't overwrite.
            mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String nameHolder = task.getResult().get("Name").toString();
//                        GeoPoint coordinateCheck = task.getResult().getGeoPoint("Coordinates");
                        if (nameHolder == locationName_) {
                            Toast.makeText(WriteReview.this, "Location already exists!", Toast.LENGTH_SHORT).show();
                            // Create new document with same name but [Coordinates] to distinguish??
                        } else {
                            mDocRef.set(locationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("WriteReview","Document has been saved");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("WriteReview","Document failed to save");
                                }
                            });
                        }
                    }
                }
            });

//        Sets Review review/joe's bar/userReviews/Yohji.
            mDocRef = FirebaseFirestore.getInstance().document("reviews/" + locationName_ + "/userReviews/" + userName_);
            Map<String, Object> userData = new HashMap<>();
            userData.put("Review",reviewText_);
            userData.put("Rating",rating_);
            userData.put("Reviewer",userName_);

//          Sets review data for the location.
            mDocRef.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("WriteReview","Document has been saved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("WriteReview","Document failed to save");
                }
            });
//           Add review to userData for "MyReviews"
            mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "userReviews/" + locationName_);
            mDocRef.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("WriteReview","Document has been saved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("WriteReview","Document failed to save");
                }
            });
            Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }
    }
}
