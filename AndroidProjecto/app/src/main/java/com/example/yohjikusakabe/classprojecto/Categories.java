package com.example.yohjikusakabe.classprojecto;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Categories extends AppCompatActivity {
    private String category_;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        TextView textView = findViewById(R.id.categoryText);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            category_ = bundle.getString("Category");
            textView.setText(category_);
        }
        db = FirebaseFirestore.getInstance();
        db.collection("reviews").whereEqualTo("Category",category_)
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
//                           Add no pic function.
                            createNewCategoryPlace(null,name,locAddress,description,lat,longitude);
                        } else {
//                           Add the function
                            createNewCategoryPlace(picURL,name,locAddress,description,lat,longitude);
                        }

                    }
                } else {
                    Log.e("checkNearbyLocal","Error retrieving documents");
                }
            }
        });
    }
    private int numToDB(int num) {
        Resources r = getResources();
        int number = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, num, r.getDisplayMetrics());
        return num;
    }

    private void createNewCategoryPlace(final String imageURL,final String name, final String address, final String snippeto,final double lat_,final double long_) {
        RelativeLayout r = new RelativeLayout(this);
        ImageButton imageButton = new ImageButton(this);
        TextView namePlace = new TextView(this);
        TextView addressPlace = new TextView(this);
        TextView snippet = new TextView(this);

        int height_ = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        int width_= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(
               height_,
                width_
        );

        paramsImage.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsImage.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        paramsImage.setMargins(numToDB(10),0,0,0);
        imageButton.setLayoutParams(paramsImage);
        imageButton.setId(View.generateViewId());
        if (imageURL != null) {
            Glide.with(this).load(imageURL).centerCrop().into(imageButton);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Intent intent = new Intent(Categories.this,LocationPage.class);
                intent.putExtra("Title",name);
                intent.putExtra("picURL",imageURL);
                intent.putExtra("Address",address);
                intent.putExtra("Longitude",long_);
                intent.putExtra("Latitude",lat_);
                intent.putExtra("Description",snippeto);
                startActivity(intent);
            }
        });

        RelativeLayout.LayoutParams paramsName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        paramsName.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        paramsName.addRule(RelativeLayout.RIGHT_OF,imageButton.getId());
        paramsName.setMargins(numToDB(10),0,0,0);
        namePlace.setLayoutParams(paramsName);
        namePlace.setId(View.generateViewId());
        namePlace.setText(name);

//        Address/Phone
        RelativeLayout.LayoutParams paramsAddress = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        paramsAddress.addRule(RelativeLayout.ALIGN_START,namePlace.getId());
        paramsAddress.addRule(RelativeLayout.BELOW,namePlace.getId());
        paramsAddress.setMargins(numToDB(10),0,0,0);
        addressPlace.setId(View.generateViewId());
        addressPlace.setLayoutParams(paramsAddress);
        addressPlace.setText(address);
        addressPlace.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        // Snippet

        RelativeLayout.LayoutParams paramsSnippet = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        paramsSnippet.addRule(RelativeLayout.ALIGN_START,addressPlace.getId());
        paramsSnippet.addRule(RelativeLayout.BELOW,addressPlace.getId());
        paramsSnippet.setMargins(0,0,numToDB(10),numToDB(10));
        snippet.setLayoutParams(paramsSnippet);
        snippet.setText(snippeto);
        snippet.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        RelativeLayout.LayoutParams paramsRelativeLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                numToDB(300)
        );
//        paramsRelativeLayout.setMargins(0,numToDB(10),0,0);
        r.setPadding(0,numToDB(10),0,0);
        r.setLayoutParams(paramsRelativeLayout);
        r.addView(imageButton);
        r.addView(namePlace);
        r.addView(addressPlace);
        r.addView(snippet);

        // add to linear layout
        LinearLayout linearLayout = findViewById(R.id.parentCategory);
        linearLayout.addView(r);

    }

}
